/**
 * 
 */
package boa.debugger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.log4j.Logger;
import boa.compiler.ast.*;
import boa.compiler.ast.expressions.Expression;
import boa.compiler.ast.expressions.FunctionExpression;
import boa.compiler.ast.expressions.ParenExpression;
import boa.compiler.ast.expressions.SimpleExpr;
import boa.compiler.ast.expressions.VisitorExpression;
import boa.compiler.ast.literals.CharLiteral;
import boa.compiler.ast.literals.FloatLiteral;
import boa.compiler.ast.literals.IntegerLiteral;
import boa.compiler.ast.literals.StringLiteral;
import boa.compiler.ast.literals.TimeLiteral;
import boa.compiler.ast.statements.AssignmentStatement;
import boa.compiler.ast.statements.Block;
import boa.compiler.ast.statements.BreakStatement;
import boa.compiler.ast.statements.ContinueStatement;
import boa.compiler.ast.statements.DoStatement;
import boa.compiler.ast.statements.EmitStatement;
import boa.compiler.ast.statements.ExistsStatement;
import boa.compiler.ast.statements.ExprStatement;
import boa.compiler.ast.statements.ForStatement;
import boa.compiler.ast.statements.ForeachStatement;
import boa.compiler.ast.statements.IfAllStatement;
import boa.compiler.ast.statements.IfStatement;
import boa.compiler.ast.statements.PostfixStatement;
import boa.compiler.ast.statements.ReturnStatement;
import boa.compiler.ast.statements.Statement;
import boa.compiler.ast.statements.StopStatement;
import boa.compiler.ast.statements.SwitchCase;
import boa.compiler.ast.statements.SwitchStatement;
import boa.compiler.ast.statements.TypeDecl;
import boa.compiler.ast.statements.VarDeclStatement;
import boa.compiler.ast.statements.VisitStatement;
import boa.compiler.ast.statements.WhileStatement;
import boa.compiler.ast.types.AbstractType;
import boa.compiler.ast.types.ArrayType;
import boa.compiler.ast.types.FunctionType;
import boa.compiler.ast.types.MapType;
import boa.compiler.ast.types.OutputType;
import boa.compiler.ast.types.SetType;
import boa.compiler.ast.types.StackType;
import boa.compiler.ast.types.TupleType;
import boa.compiler.ast.types.VisitorType;
import boa.compiler.visitors.AbstractVisitor;
import boa.debugger.Env.EmptyEnv;
import boa.debugger.Env.ExtendEnv;
import boa.debugger.value.aggregators.AggregatorVal;
import boa.debugger.value.*;

/**
 * @author nmtiwari
 *
 */
public class Evaluator extends AbstractVisitor<Value, Env<Value>> {
	public static ArrayList<String> pathToDataSet = new ArrayList<>();
	public static boolean DEBUG = true;
	public static Logger LOG = Logger.getLogger(Evaluator.class);
	ByteArrayOutputStream op = new ByteArrayOutputStream();
	PrintStream ps = new PrintStream(op);
	PrintStream old = System.out;
	ArrayList<String> aggregators = new ArrayList<>();

	public Evaluator() {
		// TODO Auto-generated constructor stub
	}

	protected Env<Value> initEnv() {
		Env<Value> initEnv = new EmptyEnv<Value>();
		return initEnv;
	}

	protected Env<Value> initEnv(Program p) {
		Env<Value> initEnv = new EmptyEnv<Value>();
		for (Statement stmt : p.getStatements()) {
			if (stmt instanceof VarDeclStatement) {
				if (((VarDeclStatement) stmt).getType() instanceof OutputType) {
					String var = ((VarDeclStatement) stmt).getId().getToken();
					Value value = stmt.accept(this, initEnv);
					initEnv = new ExtendEnv<Value>(initEnv, var, value);
					aggregators.add(var);
				}
			}
		}
		return initEnv;
	}

	@Override
	public Value start(Node n, Env<Value> env) {
		LOG.info("Starting...");
		env = initEnv();
		return n.accept(this, env);
	}

	@Override
	public Value visit(Start n, Env<Value> env) {
		if (DEBUG)
			System.out.println("start()");
		return n.getProgram().accept(this, env);
	}

	@SuppressWarnings("unchecked")
	public Value visit(Program n, Env<Value> env) {
		int iteration = 0;
		Configuration conf = new Configuration();

		SequenceFile.Reader sequenceFileReader = this.open(pathToDataSet.get(0) + "/projects.seq", conf);
		org.apache.hadoop.io.Text key = (org.apache.hadoop.io.Text) ReflectionUtils
				.newInstance(sequenceFileReader.getKeyClass(), conf);
		org.apache.hadoop.io.BytesWritable keyValue = (org.apache.hadoop.io.BytesWritable) ReflectionUtils
				.newInstance(sequenceFileReader.getValueClass(), conf);

		Env<Value> baseEnv = initEnv(n);
		env = baseEnv;
		Value value = UnitVal.v;
		boa.types.Toplevel.Project _input = null;

		if (!DEBUG)
			System.setOut(ps);
		if (!DEBUG)
			System.setErr(old);

		try {
			while (sequenceFileReader.next(key, keyValue)) {
				_input = boa.types.Toplevel.Project.parseFrom(
						com.google.protobuf.CodedInputStream.newInstance(keyValue.getBytes(), 0, keyValue.getLength()));
				env = new ExtendEnv<Value>(env, "input", new AnyVal(_input));
				for (int i = 0; i < n.getStatementsSize(); i++) {
					value = n.getStatement(i).accept(this, env);
					// some variable is declared which is not of output type
					// then extend the environment else skip this definition
					if (value instanceof BindingVal
							&& (!(((BindingVal) value).getInitializer() instanceof AggregatorVal))) {
						BindingVal declaration = (BindingVal) value;
						env = new ExtendEnv<Value>(env, declaration.getID(), declaration.getInitializer());
					}
					env = (Env<Value>) baseEnv.clone();
					iteration++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			this.close(sequenceFileReader);
			return new DynamicError("Error occured in parsing project");
		}
		
		
		for(String name : aggregators){
			AggregatorVal v = (AggregatorVal) baseEnv.get(name);
			v.finish();
		}

		System.out.flush();
		if (!DEBUG)
			System.setOut(old);
		if (!DEBUG)
			System.setErr(old);

		value = new StringVal(op.toString());
		return value;
	}

	public Value visit(final Call n, Env<Value> env) {
		Value v = UnitVal.v;
		ListVal args = new ListVal();
		for (final Expression e : n.getArgs()) {
			v = e.accept(this, env);
			args.add(v);
		}
		return args;
	}

	public Value visit(final Comparison n, Env<Value> env) {
		Value lhs = n.getLhs().accept(this, env);
		Value rhs = UnitVal.v;
		if (n.hasRhs()) {
			rhs = n.getRhs().accept(this, env);
			String op = n.getOp();
			return lhs.compute(rhs, op);
		}
		return lhs;
	}

	public Value visit(final Component n, Env<Value> env) {
		StringVal id = null;
		if (n.hasIdentifier()) {
			id = (StringVal) n.getIdentifier().accept(this, env);
		}
		Value type = n.getType().accept(this, env);
		return new PairVal(id, type);
	}

	public Value visit(final Composite n, Env<Value> env) {
		throw new UnsupportedOperationException();
		// for (final Pair p : n.getPairs())
		// p.accept(this, env);
		// for (final Expression e : n.getExprs())
		// e.accept(this, env);
		// return null;
	}

	public Value visit(final Conjunction n, Env<Value> env) {
		Value lhs = n.getLhs().accept(this, env);
		int i = 0;
		for (String str : n.getOps()) {
			Value rhs = n.getRhs(i).accept(this, env);
			if ("&&".equals(str) || "and".equals(str)) {
				lhs = lhs.compute(rhs, "&&");
			}
		}
		return lhs;
	}

	public Value visit(final Factor n, Env<Value> env) {
		return n.getOperand().accept(this, env);
	}

	public Value visit(final Identifier n, Env<Value> env) {
		if ("true".equalsIgnoreCase(n.getToken()) || "false".equalsIgnoreCase(n.getToken())) {
			throw new UnsupportedOperationException();
		}
		return new StringVal(n.getToken());
	}

	public Value visit(final Index n, Env<Value> env) {
		NumVal begin = (NumVal) n.getStart().accept(this, env);
		if (n.hasEnd()) {
			NumVal end = (NumVal) n.getEnd().accept(this, env);
			return new PairVal(begin, end);
		}
		return begin;
	}

	public Value visit(final Pair n, Env<Value> env) {
		Value fst = n.getExpr1().accept(this, env);
		Value snd = n.getExpr2().accept(this, env);
		return new PairVal(fst, snd);
	}

	public Value visit(final Selector n, Env<Value> env) {
		return n.getId().accept(this, env);
	}

	public Value visit(final Term n, Env<Value> env) {
		Value lhs = n.getLhs().accept(this, env);
		Value rhs = UnitVal.v;
		int i = 0;
		for (Factor r : n.getRhs()) {
			rhs = r.accept(this, env);
			String op = n.getOp(i);
			lhs = lhs.compute(rhs, op);
		}
		return lhs;
	}

	public Value visit(final UnaryFactor n, Env<Value> env) {
		Value factor = n.getFactor().accept(this, env);
		return factor.compute(factor, "");
	}

	//
	// statements
	//
	public Value visit(final AssignmentStatement n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final Block n, Env<Value> env) {
		Value value = null;
		for (Statement s : n.getStatements()) {
			value = s.accept(this, env);
			if (s instanceof ReturnStatement) {
				break;
			}
		}
		return value;
	}

	public Value visit(final BreakStatement n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final ContinueStatement n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final DoStatement n, Env<Value> env) {
		Env<Value> temp = env;
		do {
			n.getBody().accept(this, env);
		} while (((BoolVal) n.getCondition().accept(this, temp)).v());
		return UnitVal.v;
	}

	public Value visit(final EmitStatement n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final ExistsStatement n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final ExprStatement n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final ForeachStatement n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final ForStatement n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final IfAllStatement n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final IfStatement n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final PostfixStatement n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final ReturnStatement n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final StopStatement n, Env<Value> env) {
		return null;
	}

	public Value visit(final SwitchCase n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final SwitchStatement n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final VarDeclStatement n, Env<Value> env) {
		String name = ((StringVal) n.getId().accept(this, env)).v();
		AbstractType type = null;
		Value intial = null;
		if (n.hasType())
			type = n.getType();
		if (n.hasInitializer())
			intial = n.getInitializer().accept(this, env);
		return new BindingVal(name, type, intial);
	}

	public Value visit(final VisitStatement n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final WhileStatement n, Env<Value> env) {
		BoolVal cond = (BoolVal) n.getCondition().accept(this, env);
		Env<Value> temp = env;
		Value result = UnitVal.v;
		while (cond.v()) {
			result = n.getBody().accept(this, temp);
			cond = (BoolVal) n.getCondition().accept(this, temp);
		}
		return result;
	}

	//
	// expressions
	//
	public Value visit(final Expression n, Env<Value> env) {
		Value lhs = n.getLhs().accept(this, env);
		for (final Conjunction c : n.getRhs()) {
			lhs = lhs.compute(c.accept(this, env), "||");
		}
		return lhs;
	}

	public Value visit(final FunctionExpression n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final ParenExpression n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final SimpleExpr n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final VisitorExpression n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	//
	// literals
	//
	public Value visit(final CharLiteral n, Env<Value> env) {
		return new CharVal(n.getLiteral());
	}

	public Value visit(final FloatLiteral n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final IntegerLiteral n, Env<Value> env) {
		return new NumVal(Integer.parseInt(n.getLiteral()));
	}

	public Value visit(final StringLiteral n, Env<Value> env) {
		return new StringVal(n.getLiteral());
	}

	public Value visit(final TimeLiteral n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	//
	// types
	//
	public Value visit(final TypeDecl n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final ArrayType n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final FunctionType n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final MapType n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final OutputType n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final StackType n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final SetType n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final TupleType n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final VisitorType n, Env<Value> env) {
		return null;
	}

	private SequenceFile.Reader open(String seqPath, Configuration conf) {
		Path path = new Path(pathToDataSet.get(0) + "/projects.seq");
		SequenceFile.Reader sequenceFileReader = null;
		FileSystem fs;
		try {
			fs = FileSystem.get(conf);
			sequenceFileReader = new SequenceFile.Reader(fs, path, conf);
			return sequenceFileReader;
		} catch (IOException e) {
			System.out.println("Exception occured in Program node while creating FileSystem or Reader");
			e.printStackTrace();
			return null;
		}
	}

	private boolean close(SequenceFile.Reader seqFileReader) {
		try {
			seqFileReader.close();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

}
