/*
 * Copyright 2015, Anthony Urso, Hridesh Rajan, Robert Dyer,
 *                 and Iowa State University of Science and Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package boa.compiler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.log4j.Logger;
import org.scannotation.ClasspathUrlFinder;

import boa.compiler.ast.Program;
import boa.compiler.ast.Start;
import boa.compiler.listeners.BoaErrorListener;
import boa.compiler.listeners.LexerErrorListener;
import boa.compiler.listeners.ParserErrorListener;
import boa.compiler.transforms.LocalAggregationTransformer;
import boa.compiler.transforms.VisitorOptimizingTransformer;
import boa.compiler.visitors.CodeGeneratingVisitor;
import boa.compiler.visitors.TaskClassifyingVisitor;
import boa.compiler.visitors.TypeCheckingVisitor;
import boa.datagen.BoaGenerator;
import boa.datagen.DefaultProperties;
import boa.datagen.candoia.CandoiaConfiguration;
import boa.debugger.Env.EmptyEnv;
import boa.debugger.Evaluator;
import boa.debugger.value.Value;
import boa.parser.BoaLexer;
import boa.parser.BoaParser;

/**
 * The main entry point for the Boa compiler.
 *
 * @author anthonyu
 * @author rdyer
 */
public class BoaCompiler {

	private static Logger LOG = Logger.getLogger(BoaCompiler.class);

	public static void main(final String[] args) throws IOException {
		CommandLine cl = processCommandLineOptions(args);
		if (cl == null)
			return;
		final ArrayList<File> inputFiles = BoaCompiler.inputFiles;
		String[] localRepos = new String[0];
		String[] cloneRepos = new String[0];
		ArrayList<String> actualCloning = new ArrayList<String>();

		// get the name of the generated class
		final String className = getGeneratedClass(cl);

		// get the filename of the jar we will be writing
		final String jarName;
		if (cl.hasOption('o'))
			jarName = cl.getOptionValue('o');
		else
			jarName = className + ".jar";

		// make the output directory
		final File outputRoot = new File(new File(System.getProperty("java.io.tmpdir")), UUID.randomUUID().toString());
		final File outputSrcDir = new File(outputRoot, "boa");
		if (!outputSrcDir.mkdirs())
			throw new IOException("unable to mkdir " + outputSrcDir);

		// find custom libs to load
		final List<URL> libs = new ArrayList<URL>();
		if (cl.hasOption('l'))
			for (final String lib : cl.getOptionValues('l'))
				libs.add(new File(lib).toURI().toURL());

		final File outputFile = new File(outputSrcDir, className + ".java");
		final BufferedOutputStream o = new BufferedOutputStream(new FileOutputStream(outputFile));

		if (cl.hasOption("output")) {
			DefaultProperties.GH_GIT_PATH = cl.getOptionValue("output").split(",")[0]
					+ DefaultProperties.CLONE_DIR_NAME;
			DefaultProperties.GH_JSON_PATH = cl.getOptionValue("output").split(",")[0]
					+ DefaultProperties.JSON_DIR_NAME;
			DefaultProperties.GH_JSON_CACHE_PATH = cl.getOptionValue("output").split(",")[0];
			DefaultProperties.GH_TICKETS_PATH = DefaultProperties.GH_JSON_PATH;
			// Evaluator.pathToDataSet.add(DefaultProperties.GH_JSON_CACHE_PATH);
		} else {
			System.err.println("Output directory is not provided");
			o.close();
			return;
		}

		if (cl.hasOption("repo")) {
			localRepos = cl.getOptionValue("repo").split(",");
		}
		if (cl.hasOption("clone")) {
			cloneRepos = cl.getOptionValue("clone").split(",");
			DefaultProperties.NUM_THREADS = Integer.toString(cloneRepos.length);
		}

		try {
			SymbolTable.initialize(libs);
			for (int i = 0; i < inputFiles.size(); i++) {
				final File f = inputFiles.get(i);
				Start p = null;
				try {
					final BoaLexer lexer = new BoaLexer(new ANTLRFileStream(f.getAbsolutePath()));
					lexer.removeErrorListeners();
					lexer.addErrorListener(new LexerErrorListener());

					final CommonTokenStream tokens = new CommonTokenStream(lexer);
					final BoaParser parser = new BoaParser(tokens);
					parser.removeErrorListeners();
					parser.addErrorListener(new BaseErrorListener() {
						@Override
						public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
								int charPositionInLine, String msg, RecognitionException e)
								throws ParseCancellationException {
							throw new ParseCancellationException(e);
						}
					});

					final BoaErrorListener parserErrorListener = new ParserErrorListener();
					p = parse(tokens, parser, parserErrorListener);
					try {
						if (!parserErrorListener.hasError) {
							new TypeCheckingVisitor().start(p, new SymbolTable());
						}
					} catch (final TypeCheckException e) {
						parserErrorListener.error("typecheck", lexer, null, e.n.beginLine, e.n.beginColumn,
								e.n2.endColumn - e.n.beginColumn + 1, e.getMessage(), e);
					}
				} catch (final Exception e) {
					System.err.print(f.getName() + ": compilation failed: ");
					e.printStackTrace();
				}
				BoaGenerator generator = new BoaGenerator();

				boolean feshDatGen = false;
				for (String name : cloneRepos) {
					feshDatGen = needDataGen(DefaultProperties.GH_JSON_CACHE_PATH,
							name.substring(name.lastIndexOf('@') + 1)); 
					if (feshDatGen) {
						actualCloning.add(name);
					}
				}
				if(feshDatGen){
					generator.generate(actualCloning.toArray(new String[actualCloning.size()]), localRepos);
				}
				Evaluator.pathToDataSet.add(DefaultProperties.GH_JSON_CACHE_PATH);
				Evaluator evaluator = new Evaluator();
				(evaluator).start(p.getProgram(), new EmptyEnv<Value>());
			}
		} finally {
			o.close();
		}
	}

	private static boolean needDataGen(String path, String projName) {
		int countForAllThreeFiles = 0;
		File directory = new File(path);
		boolean needDatagen = true;
		ArrayList<String> repos = new ArrayList<>();
		if (directory.exists()) {
			for (String fileName : directory.list()) {
				if ((fileName.equals("data")) || fileName.equals("index") || fileName.equals("projects.seq")) {
					countForAllThreeFiles++;
				} else if ((fileName.equals(CandoiaConfiguration.getCachefilename()))) {
					FileReader fr = null;
					BufferedReader cacheReader = null;
					String nextRepo = null;
					try {
						fr = new FileReader(path + "/" + CandoiaConfiguration.getCachefilename());
						cacheReader = new BufferedReader(fr);
						while ((nextRepo = cacheReader.readLine()) != null) {
							repos.add(nextRepo);
						}
						fr.close();
						cacheReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (countForAllThreeFiles == 3) {
			for (String name : repos) {
				if (name.equals(projName)) {
					needDatagen = false;
				}
			}
			if (needDatagen) {
				try {
					delete(new File(directory.getAbsolutePath() + "/data"));
					delete(new File(directory.getAbsolutePath() + "/index"));
					delete(new File(directory.getAbsolutePath() + "/projects.seq"));
					delete(new File(directory.getAbsolutePath() + "/" +CandoiaConfiguration.getCachefilename()));
					return needDatagen;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return needDatagen;
	}

	public static void parseOnly(final String[] args) throws IOException {
		CommandLine cl = processParseCommandLineOptions(args);
		if (cl == null)
			return;
		final ArrayList<File> inputFiles = BoaCompiler.inputFiles;

		// find custom libs to load
		final List<URL> libs = new ArrayList<URL>();
		if (cl.hasOption('l'))
			for (final String lib : cl.getOptionValues('l'))
				libs.add(new File(lib).toURI().toURL());

		final List<String> jobnames = new ArrayList<String>();
		final List<String> jobs = new ArrayList<String>();
		boolean isSimple = true;

		final List<Program> visitorPrograms = new ArrayList<Program>();

		SymbolTable.initialize(libs);

		for (int i = 0; i < inputFiles.size(); i++) {
			final File f = inputFiles.get(i);
			try {
				final BoaLexer lexer = new BoaLexer(new ANTLRFileStream(f.getAbsolutePath()));
				lexer.removeErrorListeners();
				lexer.addErrorListener(new LexerErrorListener());
				final CommonTokenStream tokens = new CommonTokenStream(lexer);
				final BoaParser parser = new BoaParser(tokens);
				parser.removeErrorListeners();
				parser.addErrorListener(new BaseErrorListener() {
					@Override
					public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
							int charPositionInLine, String msg, RecognitionException e)
							throws ParseCancellationException {
						throw new ParseCancellationException(e);
					}
				});

				final BoaErrorListener parserErrorListener = new ParserErrorListener();
				Start p = parse(tokens, parser, parserErrorListener);

				final String jobName = "" + i;

				try {
					if (!parserErrorListener.hasError) {
						new TypeCheckingVisitor().start(p, new SymbolTable());

						final TaskClassifyingVisitor simpleVisitor = new TaskClassifyingVisitor();
						simpleVisitor.start(p);

						LOG.info(f.getName() + ": task complexity: "
								+ (!simpleVisitor.isComplex() ? "simple" : "complex"));
						isSimple &= !simpleVisitor.isComplex();
						new LocalAggregationTransformer().start(p);
						if (!simpleVisitor.isComplex() || cl.hasOption("nv") || inputFiles.size() == 1) {
							new VisitorOptimizingTransformer().start(p);
							final CodeGeneratingVisitor cg = new CodeGeneratingVisitor(jobName);
							cg.start(p);
							jobs.add(cg.getCode());
							jobnames.add(jobName);
						} else {
							p.getProgram().jobName = jobName;
							visitorPrograms.add(p.getProgram());
						}
					}
				} catch (final TypeCheckException e) {
					parserErrorListener.error("typecheck", lexer, null, e.n.beginLine, e.n.beginColumn,
							e.n2.endColumn - e.n.beginColumn + 1, e.getMessage(), e);
				}
			} catch (final Exception e) {
				System.err.print(f.getName() + ": parsing failed: ");
				e.printStackTrace();
			}
		}
	}

	private static Start parse(final CommonTokenStream tokens, final BoaParser parser,
			final BoaErrorListener parserErrorListener) {

		parser.setBuildParseTree(false);
		parser.getInterpreter().setPredictionMode(PredictionMode.SLL);

		try {
			return parser.start().ast;
		} catch (final ParseCancellationException e) {
			// fall-back to LL mode parsing if SLL fails
			tokens.reset();
			parser.reset();

			parser.removeErrorListeners();
			parser.addErrorListener(parserErrorListener);
			parser.getInterpreter().setPredictionMode(PredictionMode.LL);

			return parser.start().ast;
		}
	}

	private static void compileGeneratedSrc(final CommandLine cl, final String jarName, final File outputRoot,
			final File outputFile) throws RuntimeException, IOException, FileNotFoundException {
		// compile the generated .java file
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null)
			throw new RuntimeException("Could not get javac - are you running the Boa compiler with a JDK or a JRE?");
		LOG.info("compiling: " + outputFile);
		LOG.info("classpath: " + System.getProperty("java.class.path"));
		if (compiler.run(null, null, null, "-source", "5", "-target", "5", "-cp", System.getProperty("java.class.path"),
				outputFile.toString()) != 0)
			throw new RuntimeException("compile failed");

		// find the location of the jar this class is in
		final String path = ClasspathUrlFinder.findClassBase(BoaCompiler.class).getPath();
		// find the location of the compiler distribution
		final File root = new File(path.substring(path.indexOf(':') + 1, path.indexOf('!'))).getParentFile();

		final List<File> libJars = new ArrayList<File>();
		libJars.add(new File(root, "boa-runtime.jar"));
		if (cl.hasOption('l'))
			for (final String s : Arrays.asList(cl.getOptionValues('l')))
				libJars.add(new File(s));

		generateJar(jarName, outputRoot, libJars);

		delete(outputRoot);
	}

	static ArrayList<File> inputFiles = null;

	private static CommandLine processCommandLineOptions(final String[] args) {
		// parse the command line options
		final Options options = new Options();
		options.addOption("l", "libs", true, "extra jars (functions/aggregators) to be compiled in");
		options.addOption("i", "in", true, "file(s) to be compiled (comma-separated list)");
		options.addOption("o", "output", true, "the name of the resulting jar");
		options.addOption("nv", "no-visitor-fusion", false, "disable visitor fusion");
		options.addOption("v", "visitors-fused", true, "number of visitors to fuse");
		options.addOption("n", "name", true, "the name of the generated main class");
		options.addOption("out", "path", true, "path of the output and cached data generated as result of analysis");
		options.addOption("repo", "path", true, "path of local repositories");
		options.addOption("clone", "username@url", true, "username@url for cloning");

		final CommandLine cl;
		try {
			cl = new PosixParser().parse(options, args);
		} catch (final org.apache.commons.cli.ParseException e) {
			System.err.println(e.getMessage());
			new HelpFormatter().printHelp("Boa Compiler", options);
			return null;
		}

		// get the filename of the program we will be compiling
		inputFiles = new ArrayList<File>();
		if (cl.hasOption('i')) {
			final String[] inputPaths = cl.getOptionValue('i').split(",");

			for (final String s : inputPaths) {
				final File f = new File(s);
				if (!f.exists())
					System.err.println("File '" + s + "' does not exist, skipping");
				else
					inputFiles.add(new File(s));
			}
		}

		if (inputFiles.size() == 0) {
			System.err.println("no valid input files found - did you use the --in option?");
			// new HelpFormatter().printHelp("BoaCompiler", options);
			new HelpFormatter().printHelp("Boa Compiler", options);
			return null;
		}

		return cl;
	}

	private static CommandLine processParseCommandLineOptions(final String[] args) {
		// parse the command line options
		final Options options = new Options();
		options.addOption("l", "libs", true, "extra jars (functions/aggregators) to be compiled in");
		options.addOption("i", "in", true, "file(s) to be parsed (comma-separated list)");

		final CommandLine cl;
		try {
			cl = new PosixParser().parse(options, args);
		} catch (final org.apache.commons.cli.ParseException e) {
			System.err.println(e.getMessage());
			new HelpFormatter().printHelp("Boa Parser", options);
			return null;
		}

		// get the filename of the program we will be compiling
		inputFiles = new ArrayList<File>();
		if (cl.hasOption('i')) {
			final String[] inputPaths = cl.getOptionValue('i').split(",");

			for (final String s : inputPaths) {
				final File f = new File(s);
				if (!f.exists())
					System.err.println("File '" + s + "' does not exist, skipping");
				else
					inputFiles.add(new File(s));
			}
		}

		if (inputFiles.size() == 0) {
			System.err.println("no valid input files found - did you use the --in option?");
			// new HelpFormatter().printHelp("BoaCompiler", options);
			new HelpFormatter().printHelp("Boa Parser", options);
			return null;
		}

		return cl;
	}

	private static final String getGeneratedClass(final CommandLine cl) {
		// get the name of the generated class
		final String className;
		if (cl.hasOption('n')) {
			className = cl.getOptionValue('n');
		} else {
			String s = "";
			for (final File f : inputFiles) {
				if (s.length() != 0)
					s += "_";
				if (f.getName().indexOf('.') != -1)
					s += f.getName().substring(0, f.getName().lastIndexOf('.'));
				else
					s += f.getName();
			}
			className = pascalCase(s);
		}
		return className;
	}

	private static final void delete(final File f) throws IOException {
		if (f.isDirectory())
			for (final File g : f.listFiles())
				delete(g);
		FileDeleteStrategy.FORCE.delete(f);
	}

	private static void generateJar(final String jarName, final File dir, final List<File> libJars)
			throws IOException, FileNotFoundException {
		final int offset = dir.toString().length() + 1;

		final JarOutputStream jar = new JarOutputStream(
				new BufferedOutputStream(new FileOutputStream(new File(jarName))));
		try {
			for (final File f : findFiles(dir, new ArrayList<File>()))
				putJarEntry(jar, f, f.getPath().substring(offset));

			for (final File f : libJars)
				putJarEntry(jar, f, "lib" + File.separatorChar + f.getName());
		} finally {
			jar.close();
		}
	}

	private static final List<File> findFiles(final File f, final List<File> l) {
		if (f.isDirectory())
			for (final File g : f.listFiles())
				findFiles(g, l);
		else
			l.add(f);

		return l;
	}

	private static void putJarEntry(final JarOutputStream jar, final File f, final String path) throws IOException {
		jar.putNextEntry(new ZipEntry(path));

		final InputStream in = new BufferedInputStream(new FileInputStream(f));
		try {
			final byte[] b = new byte[4096];
			int len;
			while ((len = in.read(b)) > 0)
				jar.write(b, 0, len);
		} finally {
			in.close();
		}

		jar.closeEntry();
	}

	private static String pascalCase(final String string) {
		final StringBuilder pascalized = new StringBuilder();

		boolean upper = true;
		for (final char c : string.toCharArray())
			if (Character.isDigit(c) || c == '_') {
				pascalized.append(c);
				upper = true;
			} else if (!Character.isDigit(c) && !Character.isLetter(c)) {
				upper = true;
			} else if (Character.isLetter(c)) {
				pascalized.append(upper ? Character.toUpperCase(c) : c);
				upper = false;
			}

		return pascalized.toString();
	}
}
