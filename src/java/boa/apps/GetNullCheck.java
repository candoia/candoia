package main;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import util.Config;
import util.FileIO;

public class GetNullCheck {
	private static String[] fixingPatterns = {"issue[\\s]+[0-9]+", "issues[\\s]+[0-9]+", "issue[\\s]+#[0-9]+", "issues[\\s]+#[0-9]+", "issue[\\s]+# [0-9]+", "bug[\\s]+[0-9]+", "bug[\\s]+[0-9]+","bug[\\s]+#[0-9]+", "bug[\\s]+#[0-9]+", "bug[\\s]+# [0-9]+", "fix", "bug id=[0-9]+"};
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		GetNullCheck gnc = new GetNullCheck();
		System.out.println(gnc.countSvnJavaNullChecks(Config.json100Path));
		long endTime = System.currentTimeMillis();
		FileIO.writeStringToFile("Time: " + (endTime - startTime) / 1000.000, gnc.getClass().getSimpleName() + System.currentTimeMillis() + ".txt");
		System.out.println("Time: " + (endTime - startTime) / 1000.000);
	}
	private int countNullChecks(String url) {
		int count = 0;
		SVNRepository rep = null;
		Collection logEntries = null;
		try {
			SVNURL svnUrl = SVNURL.parseURIEncoded(url);
			FSRepositoryFactory.setup();
			rep = SVNRepositoryFactory.create(svnUrl);
			logEntries = rep.log(new String[] {""}, null, 0, -1, true, true);
		} catch (SVNException e){ e.printStackTrace(); }
		if (logEntries != null)
			for (Iterator entries = logEntries.iterator(); entries.hasNext();) {
				SVNLogEntry logEntry = (SVNLogEntry) entries.next();
				if (isFixingCommit(logEntry.getMessage()))
					count += countNullCheckAdditions(rep, logEntry);
			}
		return count;
	}
	private int countNullCheckAdditions(SVNRepository rep, SVNLogEntry logEntry) {
		int numOfNullCheckAdds = 0;
		HashMap<String, String> modPaths = getModifiedPaths(rep, logEntry);
		for (String modPath : modPaths.keySet()) {
			String fromPath = modPaths.get(modPath);
			int n1 = countNullChecks(rep, logEntry.getRevision() - 1, fromPath);
			int n2 = countNullChecks(rep, logEntry.getRevision(), modPath);
			if (n2 > n1)
				numOfNullCheckAdds++;
		}
		return numOfNullCheckAdds;
	}
	private int countNullChecks(SVNRepository rep, long revision, String path) {
		String fileContent = getFile(rep, path, revision);
		ASTNode ast = createAst(fileContent);
    	return countNullChecks(ast);
	}
	private ASTNode createAst(String fileContent) {
		Map options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(fileContent.toCharArray());
    	parser.setCompilerOptions(options);
    	ASTNode ast = parser.createAST(null);
		return ast;
	}
	
	private int countNullChecks(ASTNode ast) {
		class NullCheckConditionVisitor extends ASTVisitor {
			int numOfNullChecks = 0;
			
			@Override
			public boolean visit(ConditionalExpression node) {
				node.getExpression().accept(new NullCheckExpressionVisitor());
				return super.visit(node);
			}
			
			@Override
			public boolean visit(IfStatement node) {
				node.getExpression().accept(new NullCheckExpressionVisitor());
				return super.visit(node);
			}
			
			class NullCheckExpressionVisitor extends ASTVisitor {
				@Override
				public boolean visit(InfixExpression node) {
					if (node.getOperator() == Operator.EQUALS || node.getOperator() == Operator.NOT_EQUALS) {
						if (node.getRightOperand() instanceof NullLiteral || node.getLeftOperand() instanceof NullLiteral)
							numOfNullChecks++;
					}
					return super.visit(node);
				}
			}
		};
		NullCheckConditionVisitor v = new NullCheckConditionVisitor();
		ast.accept(v);
		return v.numOfNullChecks;
	}
	public String getFile(SVNRepository rep, String path, long revision) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			rep.getFile(path, revision, null, out);
		}
		catch (SVNException e) {
			e.printStackTrace();
		}
		
		return out.toString();
	}
	private HashMap<String, String> getModifiedPaths(SVNRepository repository, SVNLogEntry logEntry) {
		long revision = logEntry.getRevision();
		HashMap<String, String> modPaths = new HashMap<String, String>();
		HashSet<String> changedPaths = new HashSet<String>(logEntry.getChangedPaths().keySet());
		HashMap<String, String> rDirPaths = new HashMap<String, String>();
		int numOfModFiles = 0;
		for (String changedPath : changedPaths) {
			if (changedPath.endsWith(".java")) {
				SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPath);
				if (entryPath.getType() == SVNLogEntryPath.TYPE_MODIFIED)
					numOfModFiles++;
			}
			else {
				try {
					if (repository.checkPath(changedPath, revision-1) == SVNNodeKind.DIR) {
						SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPath);
						if (entryPath.getType() == SVNLogEntryPath.TYPE_ADDED) {
							String fromPath = entryPath.getCopyPath();
							if (fromPath != null) {
								rDirPaths.put(changedPath, fromPath);
							}
						}
					}
				} catch (SVNException e) {
					e.printStackTrace();
				}
			}
		}
		if (numOfModFiles == 0)
			return modPaths;
		for (String changedPath : changedPaths) {
			if (changedPath.endsWith(".java")) {
				SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPath);
				if (entryPath.getType() != SVNLogEntryPath.TYPE_DELETED) {
					if (entryPath.getType() == SVNLogEntryPath.TYPE_MODIFIED) {
						String fromPath = entryPath.getCopyPath();
						if (fromPath == null) {
							for (String rDirPath : rDirPaths.keySet()) {
								if (changedPath.startsWith(rDirPath)) {
									fromPath = rDirPaths.get(rDirPath) + changedPath.substring(rDirPath.length());
									break;
								}
							}
							if (fromPath == null)
								fromPath = changedPath;
						}
						modPaths.put(changedPath, fromPath);
					}
				}
			}
		}
		return modPaths;
	}
	public int countSvnJavaNullChecks(String jsonPath) {
		int count = 0;
		File[] files = new File(jsonPath).listFiles();
		for (int i = 0; i < files.length; i++) {
			String link = getJavaSvnLink(files[i]);
			if (link != null)  {
				String name = FileIO.getSVNRepoRootName(link);
				link = "file:///" + Config.svnRootPath + "/" + name;
				System.out.println((i+1) + ": " + link);
				count += countNullChecks(link);
			}
		}
		return count;
	}
	private String getJavaSvnLink(File file) {
		String jsonTxt = "";
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			byte[] bytes = new byte[(int) file.length()];
			in.read(bytes);
			in.close();
			jsonTxt = new String(bytes);
		} catch (Exception e) { System.err.println("Error reading file " + file.getAbsolutePath()); }
        JSONObject json = null;
        try {
        	json = (JSONObject) JSONSerializer.toJSON(jsonTxt);
        } catch (JSONException e) { System.err.println("Error parsing file " + file.getAbsolutePath());}
        if (json == null) return null;
        JSONObject jsonProject = json.getJSONObject("Project");
        if (jsonProject == null || jsonProject.isNullObject()) {
        	System.err.println("Error reading project from file " + file.getAbsolutePath());
			return null;
        }
        if (jsonProject.has("SVNRepository")) {
        	String svnUrl = null;
    		JSONObject svnRep = jsonProject.getJSONObject("SVNRepository");
			if (svnRep.has("location")) svnUrl = svnRep.getString("location");
			else return null;
			boolean hasJava = false;
			if (jsonProject.has("programming-languages")) {
	        	JSONArray jsonlanguages = jsonProject.getJSONArray("programming-languages");
				if (jsonlanguages.isArray())
			        for (int i = 0; i < jsonlanguages.size(); i++)
			        	if (jsonlanguages.getString(i).trim().toLowerCase().equals("java"))
			        		return svnUrl;
	        }
			if (!hasJava) return null;
        }
        return null;
	}
	private boolean isFixingCommit(String commitLog) {
		boolean isFixing = false;
		Pattern p;
		if (commitLog != null) {
			String tmpLog = commitLog.toLowerCase();
			for (int i = 0; i< fixingPatterns.length; i++) {
				String patternStr = fixingPatterns[i];
				p = Pattern.compile(patternStr);
				Matcher m = p.matcher(tmpLog);
				isFixing = m.find();
				if (isFixing == true) break;
			}
		}
		return isFixing;
	}
}
