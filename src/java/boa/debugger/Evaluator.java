/**
 * 
 */
package boa.debugger;

import boa.compiler.ast.*;
import boa.compiler.ast.expressions.*;
import boa.compiler.ast.literals.*;
import boa.compiler.ast.statements.*;
import boa.compiler.ast.types.*;
import boa.compiler.visitors.AbstractVisitor;
import boa.debugger.Env.EmptyEnv;
import boa.debugger.Env.ExtendEnv;
import boa.debugger.Env.LookupException;
import boa.debugger.value.*;
import boa.debugger.value.aggregators.AggregatorVal;
import boa.debugger.value.aggregators.IntSumAggregatorVal;
import boa.debugger.value.aggregators.TopAggregatorVal;
import boa.types.BoaType;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

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
				for (Statement s : n.getStatements()) {
					if (s instanceof VarDeclStatement && ((VarDeclStatement) s).getType() instanceof OutputType) {
						continue; // aggregators have already been declared
					}
					value = s.accept(this, env);
					if (value instanceof BindingVal) {
						BindingVal declaration = (BindingVal) value;
						env = new ExtendEnv<Value>(env, declaration.getID(), declaration.getInitializer());
					}
				}
				env = baseEnv;
			}
		} catch (IOException e) {
			e.printStackTrace();
			this.close(sequenceFileReader);
			return new DynamicError("Error occured in parsing project");
		}

		for (String name : aggregators) {
			AggregatorVal v = (AggregatorVal) ((BindingVal) baseEnv.get(name)).getInitializer();
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
		Value operand = n.getOperand().accept(this, env);
		for (Node o : n.getOps()) {
			Value op = o.accept(this, env);
			if (o instanceof Selector) { // tuple val
				if (operand instanceof TupleVal) {
					operand = ((TupleVal) operand).get(op.toString());
				}
			} else if (o instanceof Index) { // must be map or array
				if (operand instanceof MapVal) {
					operand = (Value) ((MapVal) operand).get(op.get());
				} else if (operand instanceof ListVal) {
					operand = (Value) ((ListVal) operand).get((long) op.get());
				}
			} else { // this must be call
				operand = FunctionCall.executeFunction(operand.toString(), (ListVal) op, env, this);
			}
		}
		return operand;
	}

	public Value visit(final Identifier n, Env<Value> env) {
		if ("true".equalsIgnoreCase(n.getToken()) || "false".equalsIgnoreCase(n.getToken())) {
			throw new UnsupportedOperationException();
		}
		try {
			return env.get(n.getToken());
		} catch (LookupException e) {
			return new StringVal(n.getToken());
		}

	}

	public Value visit(final Index n, Env<Value> env) {
		Value begin = n.getStart().accept(this, env);
		if (n.hasEnd()) { // TODO : should not be this case for array index?
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
		Factor op = n.getLhs();
		String name = op.getOperand().toString();
		Value operand = env.get(name);
		Value rhs = n.getRhs().accept(this, env);
		if (operand instanceof MapVal) {
			Node ind = op.getOp(0);
			Value index = ind.accept(this, env);
			((MapVal<Object, Value>) operand).put(index.get(), rhs);
		} else {
			env.updateValue(name, rhs);
		}
		return UnitVal.v;
	}

	public Value visit(final Block n, Env<Value> env) {
		Value value = null;
		for (Statement s : n.getStatements()) {
			value = s.accept(this, env);
			if (s instanceof ReturnStatement) {
				return s.accept(this, env);
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
		BindingVal b = (BindingVal) n.getId().accept(this, env);
		AggregatorVal ag = (AggregatorVal) b.getInitializer();
		String value = n.getValue().accept(this, env).toString();
		String weight = "";
		if (n.hasWeight()) {
			weight = n.getWeight().accept(this, env).toString();
		}
		String key = " ";
		for (Expression ind : n.getIndices()) {
			key += ind.accept(this, env).toString();
		}
		ag.aggregate(weight.toString(), key, value.toString(), n.getId().getToken());
		return UnitVal.v;
	}

	public Value visit(final ExistsStatement n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final ExprStatement n, Env<Value> env) {
		return n.getExpr().accept(this, env);
	}

	public Value visit(final ForeachStatement n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final ForStatement n, Env<Value> env) {
		BindingVal init = (BindingVal) n.getInit().accept(this, env);
		Env<Value> localEnv = env;
		localEnv = new ExtendEnv<Value>(env, init.getID(), init.getInitializer());
		BoolVal cond = (BoolVal) n.getCondition().accept(this, localEnv);
		try {
			while (cond.v()) {
				for (Statement stmt : n.getBody().getStatements()) {
					if (stmt instanceof ContinueStatement) {
						continue;
					} else if (stmt instanceof BreakStatement) {
						break;
					} else if (stmt instanceof ReturnStatement) {
						return stmt.accept(this, localEnv);
					} else {
						stmt.accept(this, localEnv);
					}
				}
				n.getUpdate().accept(this, localEnv);
			}
		} catch (IllegalArgumentException e) {
			return UnitVal.v;
		}
		return UnitVal.v;
	}

	public Value visit(final IfAllStatement n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final IfStatement n, Env<Value> env) {
		BoolVal cond = (BoolVal) n.getCondition().accept(this, env);
		if (cond.v()) {
			return n.getBody().accept(this, env);
		} else {
			if (n.hasElse()) {
				return n.getElse().accept(this, env);
			}
		}
		return UnitVal.v;
	}

	public Value visit(final PostfixStatement n, Env<Value> env) {
		String name = ((Identifier) n.getExpr().getLhs().getLhs().getLhs().getLhs().getLhs().getOperand()).getToken();
		String op = n.getOp();
		Value val = env.get(name);
		env.updateValue(name, val.compute(null, op));
		return val;
	}

	public Value visit(final ReturnStatement n, Env<Value> env) {
		return n.getExpr().accept(this, env);
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
		if (n.hasType()) {
			type = n.getType();
			if (type instanceof OutputType) {
				intial = n.getType().accept(this, env);
			}
		}
		if (n.hasInitializer()) {
			intial = n.getInitializer().accept(this, env);
		}
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
		Value lhs = n.getLhs().accept(this, env);
		int i = 0;
		for (String op : n.getOps()) {
			Value rhs = n.getRhs(i).accept(this, env);
			lhs = lhs.compute(rhs, op);
		}
		return lhs;
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
		Component ind = n.getIndex();
		Component val = n.getValue();
		return new MapVal<>();
	}

	public Value visit(final OutputType n, Env<Value> env) {
		ArrayList<PairVal> index = new ArrayList<PairVal>();
		StringVal name = (StringVal) n.getId().accept(this, env);
		Value typeVal = n.getType().accept(this, env);
		String type = ((PairVal) typeVal).snd().toString();
		Value weight = null;
		ArrayList<Value> args = new ArrayList<>();
		if (n.hasWeight()) {
			weight = n.getWeight().accept(this, env);
		}
		for (Expression e : n.getArgs()) {
			args.add(e.accept(this, env));
		}
		for (Component i : n.getIndices()) {
			index.add((PairVal) i.accept(this, env));
		}

		switch (name.toString()) {
		case "sum": {
			switch (type) {
			case "int":
				return new IntSumAggregatorVal();
			default:
				throw new UnsupportedOperationException();
			}
		}
		case "top": {
			switch (type) {
			default:
				return new TopAggregatorVal(((NumVal) args.get(0)).v());
			}
		}
		case "max": {
			switch (type) {
			case "int":
				throw new UnsupportedOperationException();
			default:
				throw new UnsupportedOperationException();
			}
		}
		case "min": {
			switch (type) {
			case "int":
				throw new UnsupportedOperationException();
			default:
				throw new UnsupportedOperationException();
			}
		}
		case "mean": {
			switch (type) {
			case "int":
				throw new UnsupportedOperationException();
			default:
				throw new UnsupportedOperationException();
			}
		}
		case "collection": {
			switch (type) {
			case "int":
				throw new UnsupportedOperationException();
			default:
				throw new UnsupportedOperationException();
			}
		}
		default:
			throw new UnsupportedOperationException();
		}

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
