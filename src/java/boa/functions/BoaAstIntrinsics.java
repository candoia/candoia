package boa.functions;

import java.io.IOException;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper.Context;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.InvalidProtocolBufferException;

import boa.types.Ast.*;
import boa.types.Code.CodeRepository;
import boa.types.Code.Revision;
import boa.types.Diff.ChangedFile;
import boa.types.Shared.ChangeKind;
import boa.types.Toplevel.Project;

/**
 * Boa functions for working with ASTs.
 * 
 * @author rdyer
 */
public class BoaAstIntrinsics {
	@SuppressWarnings("rawtypes")
	private static Context context;
	private static MapFile.Reader map, commentsMap;

	public static enum AST_COUNTER {
		GETS_ATTEMPTED,
		GETS_SUCCEED,
		GETS_FAILED,
		GETS_FAIL_MISSING,
		GETS_FAIL_BADPROTOBUF,
		GETS_FAIL_BADLOC,
	};

	@FunctionSpec(name = "string", returnType = "string", formalParameters = { "ChangedFile" })
	public static String changedfileToString(final ChangedFile f) {
		return f.getKey() + "!!" + f.getName();
	}

	private static final ASTRoot emptyAst = ASTRoot.newBuilder().build();
	private static final CommentsRoot emptyComments = CommentsRoot.newBuilder().build();

	/**
	 * Given a ChangedFile, return the AST for that file at that revision.
	 * 
	 * @param f the ChangedFile to get a snapshot of the AST for
	 * @return the AST, or an empty AST on any sort of error
	 */
	@SuppressWarnings("unchecked")
	@FunctionSpec(name = "getast", returnType = "ASTRoot", formalParameters = { "ChangedFile" })
	public static ASTRoot getast(final ChangedFile f) {
		// since we know only certain kinds have ASTs, filter before looking up
		final ChangedFile.FileKind kind = f.getKind();
		if (kind != ChangedFile.FileKind.SOURCE_JAVA_ERROR
				&& kind != ChangedFile.FileKind.SOURCE_JAVA_JLS2
				&& kind != ChangedFile.FileKind.SOURCE_JAVA_JLS3
				&& kind != ChangedFile.FileKind.SOURCE_JAVA_JLS4)
			return emptyAst;

		context.getCounter(AST_COUNTER.GETS_ATTEMPTED).increment(1);

		final String rowName = f.getKey() + "!!" + f.getName();

		if (map == null)
			openMap();

		try {
			final BytesWritable value = new BytesWritable();
			if (map.get(new Text(rowName), value) == null) {
				context.getCounter(AST_COUNTER.GETS_FAIL_MISSING).increment(1);
			} else {
				final CodedInputStream _stream = CodedInputStream.newInstance(value.getBytes(), 0, value.getLength());
				// defaults to 64, really big ASTs require more
				_stream.setRecursionLimit(Integer.MAX_VALUE);
				final ASTRoot root = ASTRoot.parseFrom(_stream);
				context.getCounter(AST_COUNTER.GETS_SUCCEED).increment(1);
				return root;
			}
		} catch (final InvalidProtocolBufferException e) {
			e.printStackTrace();
			context.getCounter(AST_COUNTER.GETS_FAIL_BADPROTOBUF).increment(1);
		} catch (final IOException e) {
			e.printStackTrace();
			context.getCounter(AST_COUNTER.GETS_FAIL_MISSING).increment(1);
		} catch (final RuntimeException e) {
			e.printStackTrace();
			context.getCounter(AST_COUNTER.GETS_FAIL_MISSING).increment(1);
		} catch (final Error e) {
			e.printStackTrace();
			context.getCounter(AST_COUNTER.GETS_FAIL_BADPROTOBUF).increment(1);
		}

		System.err.println("error with ast: " + rowName);
		context.getCounter(AST_COUNTER.GETS_FAILED).increment(1);
		return emptyAst;
	}

	/**
	 * Given a ChangedFile, return the comments for that file at that revision.
	 * 
	 * @param f the ChangedFile to get a snapshot of the comments for
	 * @return the comments list, or an empty list on any sort of error
	 */
	@SuppressWarnings("unchecked")
	@FunctionSpec(name = "getcomments", returnType = "CommentsRoot", formalParameters = { "ChangedFile" })
	public static CommentsRoot getcomments(final ChangedFile f) {
		// since we know only certain kinds have comments, filter before looking up
		final ChangedFile.FileKind kind = f.getKind();
		if (kind != ChangedFile.FileKind.SOURCE_JAVA_ERROR
				&& kind != ChangedFile.FileKind.SOURCE_JAVA_JLS2
				&& kind != ChangedFile.FileKind.SOURCE_JAVA_JLS3
				&& kind != ChangedFile.FileKind.SOURCE_JAVA_JLS4)
			return emptyComments;

		final String rowName = f.getKey() + "!!" + f.getName();

		if (commentsMap == null)
			openCommentMap();

		try {
			final BytesWritable value = new BytesWritable();
			if (commentsMap.get(new Text(rowName), value) != null) {
				final CodedInputStream _stream = CodedInputStream.newInstance(value.getBytes(), 0, value.getLength());
				final CommentsRoot root = CommentsRoot.parseFrom(_stream);
				return root;
			}
		} catch (final InvalidProtocolBufferException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final RuntimeException e) {
			e.printStackTrace();
		} catch (final Error e) {
			e.printStackTrace();
		}

		System.err.println("error with comments: " + rowName);
		return emptyComments;
	}

	@SuppressWarnings("rawtypes")
	public static void setup(final Context context) {
		BoaAstIntrinsics.context = context;
	}

	private static void openMap() {
		final Configuration conf = new Configuration();
		try {
			final FileSystem fs = FileSystem.get(conf);
			final Path p = new Path("hdfs://boa-nn1/",
								new Path(context.getConfiguration().get("boa.ast.dir", context.getConfiguration().get("boa.input.dir", "repcache/live")),
								new Path("ast")));
			map = new MapFile.Reader(fs, p.toString(), conf);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private static void openCommentMap() {
		final Configuration conf = new Configuration();
		try {
			final FileSystem fs = FileSystem.get(conf);
			final Path p = new Path("hdfs://boa-nn1/",
								new Path(context.getConfiguration().get("boa.comments.dir", context.getConfiguration().get("boa.input.dir", "repcache/live")),
								new Path("comments")));
			commentsMap = new MapFile.Reader(fs, p.toString(), conf);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	public static void cleanup(final Context context) {
		closeMap();
		closeCommentMap();
	}

	private static void closeMap() {
		if (map != null)
			try {
				map.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		map = null;
	}

	private static void closeCommentMap() {
		if (commentsMap != null)
			try {
				commentsMap.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		commentsMap = null;
	}

	@FunctionSpec(name = "type_name", returnType = "string", formalParameters = { "string" })
	public static String type_name(final String s) {
		// first, normalize the string
		final String t = s.replaceAll("<\\s+", "<")
			.replaceAll(",\\s+", ", ")
			.replaceAll("\\s*>\\s*", ">")
			.replaceAll("\\s*&\\s*", " & ")
			.replaceAll("\\s*\\|\\s*", " | ");

		if (!t.contains("."))
			return t;

		/*
		 * Remove qualifiers from anywhere in the string...
		 * 
		 * SomeType                               =>  SomeType
		 * foo.SomeType                           =>  SomeType
		 * foo.bar.SomeType                       =>  SomeType
		 * SomeType<T>                            =>  SomeType<T>
		 * SomeType<T, S>                         =>  SomeType<T, S>
		 * SomeType<foo.bar.T, S>                 =>  SomeType<T, S>
		 * SomeType<T, foo.bar.S>                 =>  SomeType<T, S>
		 * foo.bar.SomeType<T, foo.bar.S<bar.Q>>  =>  SomeType<T, S<Q>>
		 * SomeType|foo.Bar                       =>  SomeType|Bar
		 * foo<T>.bar<T>                          =>  foo<T>.bar<T>
		 */
		return t.replaceAll("[^\\s,<>|]+\\.([^\\s\\[.,><|]+)", "$1");
	}

	/**
	 * A visitor that returns the total number of AST nodes.
	 */
	public final static BoaCountingVisitor lenVisitor = new BoaCountingVisitor() {
		/** {@inheritDoc} */
		@Override
		protected boolean defaultPreVisit() {
			count++;
			return true;
		}
		/** {@inheritDoc} */
		@Override
		protected boolean preVisit(final Project node) throws Exception {
			return true;
		}
		/** {@inheritDoc} */
		@Override
		protected boolean preVisit(final CodeRepository node) throws Exception {
			return true;
		}
		/** {@inheritDoc} */
		@Override
		protected boolean preVisit(final Revision node) throws Exception {
			return true;
		}
		/** {@inheritDoc} */
		@Override
		protected boolean preVisit(final ChangedFile node) throws Exception {
			return true;
		}
		/** {@inheritDoc} */
		@Override
		protected boolean preVisit(final ASTRoot node) throws Exception {
			return true;
		}
	};

	/**
	 * 
	 */
	public static class SnapshotVisitor extends BoaCollectingVisitor<String, ChangedFile> {
		private long timestamp;
		private String[] kinds;

		public SnapshotVisitor initialize(final long timestamp, final String... kinds) {
			initialize(new HashMap<String, ChangedFile>());
			this.timestamp = timestamp;
			this.kinds = kinds;
			return this;
		}

		/** {@inheritDoc} */
		@Override
		protected boolean preVisit(final Revision node) throws Exception {
			return node.getCommitDate() <= timestamp;
		}
		/** {@inheritDoc} */
		@Override
		protected boolean preVisit(final ChangedFile node) throws Exception {
			boolean filter = kinds.length > 0;

			final String kindName = node.getKind().name();
			for (final String kind : kinds)
				if (kindName.startsWith(kind)) {
					filter = false;
					break;
				}

			if (!filter) {
				if (node.getChange() == ChangeKind.DELETED)
					map.remove(node.getName());
				else
					map.put(node.getName(), node);
			}

			return false;
		}
	}

	public final static SnapshotVisitor snapshot = new SnapshotVisitor();

	@FunctionSpec(name = "getsnapshot", returnType = "array of ChangedFile", formalParameters = { "CodeRepository", "time", "string..." })
	public static ChangedFile[] getSnapshot(final CodeRepository cr, final long timestamp, final String... kinds) throws Exception {
		snapshot.initialize(timestamp, kinds).visit(cr);
		return snapshot.map.values().toArray(new ChangedFile[0]);
	}

	@FunctionSpec(name = "getsnapshot", returnType = "array of ChangedFile", formalParameters = { "CodeRepository", "string..." })
	public static ChangedFile[] getSnapshot(final CodeRepository cr, final String... kinds) throws Exception {
		return getSnapshot(cr, Long.MAX_VALUE, kinds);
	}

	@FunctionSpec(name = "getsnapshot", returnType = "array of ChangedFile", formalParameters = { "CodeRepository", "time" })
	public static ChangedFile[] getSnapshot(final CodeRepository cr, final long timestamp) throws Exception {
		return getSnapshot(cr, timestamp, new String[0]);
	}

	@FunctionSpec(name = "getsnapshot", returnType = "array of ChangedFile", formalParameters = { "CodeRepository" })
	public static ChangedFile[] getSnapshot(final CodeRepository cr) throws Exception {
		return getSnapshot(cr, Long.MAX_VALUE, new String[0]);
	}

	@FunctionSpec(name = "isliteral", returnType = "bool", formalParameters = { "Expression", "string" })
	public static boolean isLiteral(final Expression e, final String lit) throws Exception {
		return e.getKind() == Expression.ExpressionKind.LITERAL && e.hasLiteral() && e.getLiteral().equals(lit);
	}
}