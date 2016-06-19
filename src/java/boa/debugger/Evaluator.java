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

import boa.compiler.ast.Call;
import boa.compiler.ast.Comparison;
import boa.compiler.ast.Component;
import boa.compiler.ast.Composite;
import boa.compiler.ast.Conjunction;
import boa.compiler.ast.Factor;
import boa.compiler.ast.Identifier;
import boa.compiler.ast.Index;
import boa.compiler.ast.Node;
import boa.compiler.ast.Pair;
import boa.compiler.ast.Program;
import boa.compiler.ast.Selector;
import boa.compiler.ast.Start;
import boa.compiler.ast.Term;
import boa.compiler.ast.UnaryFactor;
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
import boa.debugger.Env.LookupException;
import boa.debugger.value.AnyVal;
import boa.debugger.value.BindingVal;
import boa.debugger.value.BoolVal;
import boa.debugger.value.BreakVal;
import boa.debugger.value.CharVal;
import boa.debugger.value.DynamicError;
import boa.debugger.value.ListVal;
import boa.debugger.value.MapVal;
import boa.debugger.value.NumVal;
import boa.debugger.value.PairVal;
import boa.debugger.value.ReturnVal;
import boa.debugger.value.StackVal;
import boa.debugger.value.StringVal;
import boa.debugger.value.TupleVal;
import boa.debugger.value.UnitVal;
import boa.debugger.value.Value;
import boa.debugger.value.VisitorVal;
import boa.debugger.value.aggregators.AggregatorVal;
import boa.debugger.value.aggregators.IntSumAggregatorVal;
import boa.debugger.value.aggregators.MaxAggregatorVal;
import boa.debugger.value.aggregators.MinAggregatorVal;
import boa.debugger.value.aggregators.TopAggregatorVal;

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
	public static String visitorVar = "_$declaredVisitor$";

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

	// @SuppressWarnings("unchecked")
	public Value visit(Program n, Env<Value> env) {
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
		ListVal<Object> args = new ListVal<>();
		for (final Expression e : n.getArgs()) {
			v = e.accept(this, env);
			// args.add(v.get());
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

	@SuppressWarnings("unchecked")
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
					operand = (Value) ((MapVal<Object, ?>) operand).get(op.get());
				} else if (operand instanceof ListVal) {
					PairVal opIndex = (PairVal) op;
					if (opIndex.snd() == null) {
						long ind = (long) opIndex.fst().get();
						operand = (Value) ((ListVal<Value>) operand).get(ind);
					}
				}
			} else { // this must be call
				operand = FunctionCall.executeFunction(operand.toString(), (ArrayList<Value>) op.get(), env, this);
			}
		}
		return operand;
	}

	public Value visit(final Identifier n, Env<Value> env) {
		if ("true".equalsIgnoreCase(n.getToken())) {
			return new BoolVal(true);
		} else if ("false".equalsIgnoreCase(n.getToken())) {
			return new BoolVal(false);
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
		return new PairVal(begin, null);
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
		return factor.compute(factor, n.getOp());
	}

	//
	// statements
	//
	@SuppressWarnings("unchecked")
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
			if (s instanceof ReturnStatement || s instanceof ContinueStatement || s instanceof BreakStatement
					|| s instanceof StopStatement) {
				// return s.accept(this, env);
				return value;
			} else if (s instanceof VarDeclStatement) {
				BindingVal val = (BindingVal) value;
				env = new ExtendEnv<Value>(env, val.getID(), val.getInitializer());
			} else if (value instanceof ReturnVal) {
				return (Value) value.get();
			}
		}
		return UnitVal.v;
	}

	public Value visit(final BreakStatement n, Env<Value> env) {
		return new BreakVal();
	}

	public Value visit(final ContinueStatement n, Env<Value> env) {
		return UnitVal.v; // it does not matter what you return here as Block
							// will not execute any further statements
		// throw new UnsupportedOperationException();
	}

	public Value visit(final DoStatement n, Env<Value> env) {
		Env<Value> localEnv = env;
		Value result = null;
		do {
			result = n.getBody().accept(this, localEnv);
			if (result instanceof BreakVal) {
				break;
			} else if (result instanceof ReturnVal) {
				return ((ReturnVal) result).getVal();
			}
		} while (((BoolVal) n.getCondition().accept(this, localEnv)).v());
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
		ArrayList<BoolVal> range = advncdLoopCondEvaluator(n.getVar(), n.getCondition(), env);
		Value result = UnitVal.v;
		for (BoolVal bool : range) {
			if (bool.v()) {
				result = n.getBody().accept(this, env);
				break;
			}
		}
		return result;
	}

	public Value visit(final ExprStatement n, Env<Value> env) {
		return n.getExpr().accept(this, env);
	}

	public Value visit(final ForeachStatement n, Env<Value> env) {
		ArrayList<BoolVal> range = advncdLoopCondEvaluator(n.getVar(), n.getCondition(), env);
		Env<Value> lmtdScope = env;
		switch (n.getVar().getType().toString()) {
		case "int":
			int i = -1;
			String var = n.getVar().getIdentifier().toString();
			for (BoolVal bool : range) {
				lmtdScope = new ExtendEnv<Value>(lmtdScope, var, new NumVal(++i));
				if (bool.v()) {
					n.getBody().accept(this, lmtdScope);
				}
			}
			break;
		default:
			break;
		}
		return UnitVal.v;
	}

	// public Value visit(final ForStatement n, Env<Value> env) {
	// BindingVal init = (BindingVal) n.getInit().accept(this, env);
	// Env<Value> localEnv = env;
	// localEnv = new ExtendEnv<Value>(env, init.getID(),
	// init.getInitializer());
	// BoolVal cond = (BoolVal) n.getCondition().accept(this, localEnv);
	// try {
	// while (cond.v()) {
	// for (Statement stmt : n.getBody().getStatements()) {
	// if (stmt instanceof ContinueStatement) {
	// continue;
	// } else if (stmt instanceof BreakStatement) {
	// break;
	// } else if (stmt instanceof ReturnStatement) {
	// return stmt.accept(this, localEnv);
	// } else {
	// stmt.accept(this, localEnv);
	// }
	// }
	// n.getUpdate().accept(this, localEnv);
	// }
	// } catch (IllegalArgumentException e) {
	// return UnitVal.v;
	// }
	// return UnitVal.v;
	// }

	public Value visit(final ForStatement n, Env<Value> env) {
		BindingVal init = (BindingVal) n.getInit().accept(this, env);
		Env<Value> localEnv = env;
		localEnv = new ExtendEnv<Value>(env, init.getID(), init.getInitializer());
		BoolVal cond = (BoolVal) n.getCondition().accept(this, localEnv);
		Value result = null;
		try {
			while (cond.v()) {
				result = n.getBody().accept(this, localEnv);
				if (result instanceof BreakVal) {
					// return UnitVal.v;
					break;
				} else if (result instanceof ReturnVal) {
					return ((ReturnVal) result).getVal();
				}
				n.getUpdate().accept(this, localEnv);
			}
		} catch (IllegalArgumentException e) {
			return UnitVal.v;
		}
		return UnitVal.v;
	}

	public Value visit(final IfAllStatement n, Env<Value> env) {
		ArrayList<BoolVal> range = advncdLoopCondEvaluator(n.getVar(), n.getCondition(), env);
		Value result = UnitVal.v;
		boolean executeBody = true;
		for (BoolVal bool : range) {
			executeBody = executeBody && bool.v();
		}
		if (executeBody) {
			result = n.getBody().accept(this, env);
			// if (result instanceof BreakVal) {
			// // return UnitVal.v;
			// break;
			// }
			if (result instanceof ReturnVal) {
				return ((ReturnVal) result).getVal();
			}
		}
		return result;
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
		return new ReturnVal(n.getExpr().accept(this, env));
	}

	public Value visit(final StopStatement n, Env<Value> env) {
		// return null;
		return new ReturnVal(new ReturnVal(new ReturnVal(UnitVal.v)));
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
			if (type instanceof OutputType || type instanceof MapType || type instanceof SetType || type instanceof StackType) {
				intial = n.getType().accept(this, env);
			}
		}
		if (n.hasInitializer()) {
			intial = n.getInitializer().accept(this, env);
		}
		return new BindingVal(name, type, intial);
	}

	public Value visit(final VisitStatement n, Env<Value> env) {
		return visit(n.getBody(), env);
	}

	public Value visit(final WhileStatement n, Env<Value> env) {
		BoolVal cond = (BoolVal) n.getCondition().accept(this, env);
		Env<Value> temp = env;
		Value result = UnitVal.v;
		while (cond.v()) {
			result = n.getBody().accept(this, temp);
			if (result instanceof BreakVal) {
				// return UnitVal.v;
				break;
			} else if (result instanceof ReturnVal) {
				return ((ReturnVal) result).getVal();
			}
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
		VisitorType type = n.getType();
		if (type instanceof VisitorType) {
			return new VisitorVal(n.getBody(), env, this);
		}
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
		String str = n.getLiteral();
//		return new StringVal(n.getLiteral());
		return new StringVal(str.subSequence(1, str.length()-1).toString());
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
		// return new MapVal<>(ind.getType(), val.getType());
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
				return new MaxAggregatorVal(((NumVal) args.get(0)).v());
			default:
				throw new UnsupportedOperationException();
			}
		}
		case "min": {
			switch (type) {
			case "int":
				return new MinAggregatorVal(((NumVal) args.get(0)).v());
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
		return new StackVal<>();
	}

	public Value visit(final SetType n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final TupleType n, Env<Value> env) {
		throw new UnsupportedOperationException();
	}

	public Value visit(final VisitorType n, Env<Value> env) {
		System.out.println("visitro reached");
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

	private ArrayList<BoolVal> advncdLoopCondEvaluator(Component id, Expression e, Env<Value> env) {
		ArrayList<BoolVal> results = new ArrayList<>();
		String var = id.getIdentifier().toString();
		AbstractType type = id.getType();
		switch (type.toString()) {
		case "int":
			int i = -1;
			Env<Value> lmtdScopEnv = env;
			BoolVal result = null;
			try {
				while (true) {
					lmtdScopEnv = new ExtendEnv<Value>(env, var, new NumVal(++i));
					result = (BoolVal) e.accept(this, lmtdScopEnv);
					results.add(result);
				}
			} catch (ArrayIndexOutOfBoundsException ex) {
				// do nothing as this is accessing more elements
				// return results;
				return results;
			} catch (IllegalArgumentException ex) {
				// do nothing as this is accessing more elements
				// return results;
				return results;
				// } catch (java.lang.UnsupportedOperationException ex) {
				// // do nothing
				// // return results;
				// return results;
			} catch (Exception ex) {
				ex.printStackTrace();
				return results;
			}
		default:
			break;
		}

		return results;
	}

}
