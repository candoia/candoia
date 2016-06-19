package boa.debugger;

import java.util.ArrayList;
import java.util.HashSet;

import com.google.protobuf.InvalidProtocolBufferException;

import boa.debugger.Env.LookupException;
import boa.debugger.value.AnyVal;
import boa.debugger.value.BoolVal;
import boa.debugger.value.DoubleVal;
import boa.debugger.value.DynamicError;
import boa.debugger.value.ListVal;
import boa.debugger.value.MapVal;
import boa.debugger.value.NumVal;
import boa.debugger.value.PairVal;
import boa.debugger.value.SetVal;
import boa.debugger.value.StackVal;
import boa.debugger.value.StringVal;
import boa.debugger.value.UnitVal;
import boa.debugger.value.Value;
import boa.debugger.value.VisitorVal;
import boa.types.Ast.ASTRoot;
import boa.types.Ast.Declaration;
import boa.types.Ast.Method;
import boa.types.Ast.Modifier.ModifierKind;
import boa.types.Ast.Modifier.Visibility;
import boa.types.Ast.Namespace;
import boa.types.Ast.Variable;
import boa.types.Code.CodeRepository;
import boa.types.Code.Revision;
import boa.types.Diff.ChangedFile;

public class InterpreterBoaFunctionMapping {

	public InterpreterBoaFunctionMapping() {
		// TODO Auto-generated constructor stub
	}

	public static Value callCompilerLen(ArrayList<Value> operands, Env<Value> env) {
		Value argument = operands.get(0);
		return new NumVal(argument.size());
	}

	public static Value callCompilerYearOf(ArrayList<Value> operands, Env<Value> env) {
		NumVal argument = (NumVal) operands.get(0);
		long year = boa.functions.BoaTimeIntrinsics.yearOf((argument).v());
		return new NumVal((int) year);

	}

	public static Value callCompilerLowerCase(ArrayList<Value> operands, Env<Value> env) {
		StringVal argument = (StringVal) operands.get(0);
		return new StringVal(argument.toString().toLowerCase());

	}

	// public static Value callCompilerisLiteral(ListVal operation, Env<Value>
	// env) {
	//
	// Object firstArgument = operands.get(0);
	// Object secondArgument = operands.get(1);
	// if (firstArgument instanceof AnyVal) {
	// if (boa.debugger.Evaluator.DEBUG)
	// System.out.println("IsLiteralFunc" +
	// boa.debugger.Evaluator.getInfo((Value) firstArgument)
	// + boa.debugger.Evaluator.getInfo((Value) secondArgument));
	// boa.types.Ast.Expression firstArg = (boa.types.Ast.Expression)
	// ((AnyVal) firstArgument).getObject();
	// if ((Value) secondArgument instanceof StringVal) {
	// String SecondArg = ((StringVal) secondArgument).v();
	// if (boa.debugger.Evaluator.DEBUG)
	// System.out.println("options\t" + firstArg + "\t" + SecondArg + "f=" +
	// firstArg.getClass() + "s="
	// + SecondArg.getClass());
	// try {
	// SecondArg = SecondArg.substring(1, SecondArg.length() - 1);
	// boolean result = boa.functions.BoaAstIntrinsics.isLiteral(firstArg,
	// SecondArg);
	// if (boa.debugger.Evaluator.DEBUG)
	// System.out.println("IsLiteralFunc function is returning " + result);
	// return new BoolVal(result);
	// } catch (Exception e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	//
	// } else {
	// return new DynamicError("MatchFunction Expects String as Second
	// argument");
	//
	// }
	//
	// }
	// return new DynamicError("MatchFunction Expects boa.types.Ast.Expression
	// as First argument");
	//
	// }

	public static Value callCompilerGetAst(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);

		if (firstArgument instanceof AnyVal) {
			ASTRoot test = boa.functions.BoaAstIntrinsics.getast((ChangedFile) (((AnyVal) firstArgument).getObject()));
			return new AnyVal(test);
		} else {
			return new DynamicError("GetAstFunc Expects ChangedFile as First argument");
		}

	}

	public static Value callCompilerGetSnapShot(ArrayList<Value> operands, Env<Value> env) throws Exception {
		CodeRepository cr = (CodeRepository) ((AnyVal) operands.get(0)).get();
		switch (operands.size()) {
		case 1:
			ChangedFile[] changeList_1 = boa.functions.BoaAstIntrinsics.getSnapshot(cr, Long.MAX_VALUE, new String[0]);
			ListVal<AnyVal> list_1 = new ListVal<AnyVal>();
			for (ChangedFile c : changeList_1) {
				list_1.add(new AnyVal(c));
			}
			return list_1;
		case 2:
			ChangedFile[] changeList_2 = null;
			if (operands.get(1) instanceof StringVal) {
				String arg = ((StringVal) operands.get(1)).v();
				changeList_2 = boa.functions.BoaAstIntrinsics.getSnapshot(cr, arg);
			} else if (operands.get(1) instanceof NumVal) {
				changeList_2 = boa.functions.BoaAstIntrinsics.getSnapshot(cr, ((NumVal) operands.get(1)).v(),
						new String[0]);
			}

			ListVal<AnyVal> list_2 = new ListVal<AnyVal>();
			for (ChangedFile c : changeList_2) {
				list_2.add(new AnyVal(c));
			}
			return list_2;
		default:
			String[] otherArgs = new String[operands.size() - 2];
			for (int i = 2; i < operands.size(); i++)
				otherArgs[i - 2] = ((StringVal) operands.get(0)).v();
			ChangedFile[] changeListDefault = boa.functions.BoaAstIntrinsics.getSnapshot(cr,
					((NumVal) operands.get(1)).v(), otherArgs);
			ListVal<AnyVal> listDefault = new ListVal<AnyVal>();
			for (ChangedFile c : changeListDefault) {
				listDefault.add(new AnyVal(c));
			}
			return listDefault;
		}
	}

	public static Value callCompilerHasKey(ArrayList<Value> operands, Env<Value> env) {
		Value firstArgument = operands.get(0);
		Value secondArgument = operands.get(1);

		if (firstArgument instanceof MapVal) {
			MapVal map = (MapVal) firstArgument;
			if ((Value) secondArgument instanceof StringVal) {
				if (secondArgument instanceof StringVal) {
					String SecondArg = ((StringVal) secondArgument).v();
					boolean result = map.hasKey(SecondArg);
					if (boa.debugger.Evaluator.DEBUG)
						System.out.println("has key returns" + result);
					return new BoolVal(result);
				} else {
					throw new UnsupportedOperationException();
				}

			} else {
				return new DynamicError("haskey Expects String as Second argument");

			}

		}
		return new DynamicError("haskey Expects Map as First argument");
	}

	public static Value callCompilerHasModifierPublic(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		if (firstArgument instanceof AnyVal) {
			Object fst = ((AnyVal) firstArgument).getObject();
			if (fst instanceof Declaration) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierPublic((Declaration) fst));
			} else if (fst instanceof Method) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierPublic((Method) fst));
			} else if (fst instanceof Namespace) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierPublic((Namespace) fst));
			} else if (fst instanceof Variable) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierPublic((Variable) fst));
			} else {
				return new DynamicError("class of first argument does not meet the criteria of has_modifier");
			}
		} else {
			return new DynamicError("Argument to has_modifier_final does not meet the requirment");
		}

	}

	public static Value callCompilerVisit(ArrayList<Value> operands, Env<Value> env) {
		AnyVal node = (AnyVal) operands.get(0);
		VisitorVal visitor = null;
		if (operands.size() < 2) {
			// when no specific visitor val is provided then you have to fetch
			// the visitor from env
			visitor = (VisitorVal) env.get(boa.debugger.Evaluator.visitorVar);
		} else { // explicit visitor is provided with the call itself
			visitor = (VisitorVal) operands.get(1);
		}

		try {
			visitor.initialize(node.get());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// throw new UnsupportedOperationException();
		return UnitVal.v;
	}

	public static Value callCompilerIsFixingRevision(ArrayList<Value> operands, Env<Value> env) {
		StringVal argument = (StringVal) operands.get(0);
		boolean result = boa.functions.BoaIntrinsics.isfixingrevision(argument.toString());
		return new BoolVal(result);
	}

	public static Value callCompilerFormattime(ArrayList<Value> operands, Env<Value> env) {
		long numberOfArgs = operands.size();
		if (numberOfArgs == 2) {
			StringVal fst = (StringVal) operands.get(0);
			Value snd = operands.get(1);
			if (snd instanceof NumVal) {
				String result = boa.functions.BoaTimeIntrinsics.formatTime(fst.v(), ((NumVal) snd).v());
				return new StringVal(result);
			} else if (snd instanceof DynamicError) {
				System.out.println("this date is not found");
				String result = boa.functions.BoaTimeIntrinsics.formatTime(fst.v(), 0000);
				return new StringVal(result);
			} else
				return new DynamicError("wrong argument");
		} else if (numberOfArgs == 3) {
			StringVal fst = (StringVal) operands.get(0);
			NumVal snd = (NumVal) operands.get(1);
			Object thrd = operands.get(2);
			if (thrd instanceof StringVal) {
				return new StringVal(
						boa.functions.BoaTimeIntrinsics.formatTime(fst.v(), snd.v(), ((StringVal) thrd).v()));
			} else {
				return new DynamicError("No argument matched for formatDate function");
			}
		} else {
			return new DynamicError("number of arguments to formattime does not meet any overladed version");
		}
	}

	public static Value callCompilerMatch(ArrayList<Value> operands, Env<Value> env) {

		Object firstArgument = operands.get(0);
		Object secondArgument = operands.get(1);
		if (firstArgument instanceof StringVal) {

			try {
				firstArgument = env.get((((StringVal) firstArgument).v()));
			} catch (LookupException ex) {
				firstArgument = operands.get(0);
			}

			String firstArg = ((StringVal) firstArgument).v();
			if ((Value) secondArgument instanceof StringVal) {
				try {
					secondArgument = env.get((((StringVal) secondArgument).v()));
				} catch (LookupException ex) {
					secondArgument = operands.get(1);
				}

				String SecondArg = ((StringVal) secondArgument).v();
				if (boa.debugger.Evaluator.DEBUG)
					System.out.println("options\t" + firstArg + "\t" + SecondArg);
				boolean result = boa.functions.BoaStringIntrinsics.match(firstArg, SecondArg);
				if (boa.debugger.Evaluator.DEBUG)
					System.out.println("match function is returning " + result);
				return new BoolVal(result);
			} else {
				return new DynamicError("MatchFunction Expects String as Second argument");

			}

		}
		return new DynamicError("MatchFunction Expects String as First argument");

	}

	public static Value callCompilerPop(ArrayList<Value> operands, Env<Value> env) {
		StackVal<Value> stack = (StackVal<Value>) operands.get(0);
		return stack.pop();
	}

	public static Value callCompilerClear(ArrayList<Value> operands, Env<Value> env) {
		Value stack = operands.get(0);
		if (stack instanceof StackVal) {
			((StackVal<?>) stack).clearAll();
			return UnitVal.v;
		} else if (stack instanceof MapVal) {
			((MapVal<?, ?>) stack).clearAll();
			return UnitVal.v;
		} else if (stack instanceof SetVal) {
			((SetVal<?, ?>) stack).clearAll();
			return UnitVal.v;
		}
		return new DynamicError("clear Expects either String or Stack/Map as First argument");
	}

	public static Value callCompilerPush(ArrayList<Value> operands, Env<Value> env) {
		StackVal<Value> stack = (StackVal<Value>) operands.get(0);
		Value secondArgument = operands.get(1);
		stack.push(secondArgument);
		return UnitVal.v;
	}

	public static Value callCompilerContains(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		Value secondArgument = operands.get(1);

		if (firstArgument instanceof SetVal) {
			SetVal map = (SetVal) firstArgument;
			String SecondArg = ((StringVal) secondArgument).v();
			System.out.println("Searching:" + SecondArg + " in " + ((StringVal) firstArgument).v());
			boolean result = map.contains(SecondArg);
			return new BoolVal(result);
		} else if (firstArgument instanceof StringVal) {
			String SecondArg = (secondArgument).toString();
			System.out.println("Searching:" + SecondArg + " in " + ((StringVal) firstArgument).v());
			return new BoolVal(((StringVal) firstArgument).v().contains(SecondArg));
		} else
			return new DynamicError("RemoveFunc Expects Map as First argument");

	}

	public static Value callCompilerRemove(ArrayList<Value> operands, Env<Value> env) {

		if (boa.debugger.Evaluator.DEBUG)
			System.out.println("Remove reached");
		Object firstArgument = operands.get(0);
		Object secondArgument = operands.get(1);
		if (firstArgument instanceof StringVal)
			firstArgument = env.get(((StringVal) firstArgument).v());
		if (firstArgument instanceof MapVal) {
			MapVal map = (MapVal) firstArgument;
			Value temp = (Value) secondArgument;
			if ((Value) secondArgument instanceof StringVal) {
				try {
					secondArgument = env.get(((StringVal) secondArgument).v());
				} catch (LookupException ex) {
					secondArgument = temp;
				}

				if (secondArgument instanceof StringVal) {
					String SecondArg = ((StringVal) secondArgument).v();
					map.remove(SecondArg);
					if (boa.debugger.Evaluator.DEBUG)
						System.out.println("returning unitVal from here");
				}
				return UnitVal.v;
			} else {
				return new DynamicError("RemoveFunc Expects String as Second argument");

			}

		}

		else if (firstArgument instanceof SetVal) {
			SetVal map = (SetVal) firstArgument;
			if ((Value) secondArgument instanceof StringVal) {
				String SecondArg = ((StringVal) secondArgument).v();
				map.remove(SecondArg);
				if (boa.debugger.Evaluator.DEBUG)
					System.out.println("returning unitVal from here");
				return UnitVal.v;
			} else {
				return new DynamicError("RemoveFunc Expects String as Second argument");

			}

		}

		else
			return new DynamicError("RemoveFunc Expects Map as First argument");

	}

	public static Value callCompilerStrFind(ArrayList<Value> operands, Env<Value> env) {
		String firstArg = ((StringVal) operands.get(0)).v();
		String SecondArg = ((StringVal) operands.get(1)).v();
		long result = boa.functions.BoaStringIntrinsics.indexOf(firstArg, SecondArg);
		return new NumVal(result);
	}

	public static Value callCompilerStrContains(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		if ((firstArgument instanceof StringVal)) {
			String first = "";
			try {
				first = ((StringVal) (env.get(((StringVal) firstArgument).v()))).v();
			} catch (LookupException ex) {
				first = ((StringVal) firstArgument).v();
			}
			Object secondArgument = operands.get(1);
			if ((secondArgument instanceof StringVal)) {
				String second = "";
				try {
					second = ((StringVal) (env.get(((StringVal) secondArgument).v()))).v();
				} catch (LookupException ex) {
					second = ((StringVal) secondArgument).v();
				}
				if (boa.debugger.Evaluator.DEBUG)
					System.out.println("checking if:" + second + "\tcontains" + first);
				if (boa.debugger.Evaluator.DEBUG)
					System.out.println("Returning boolean" + second.contains(first));
				return new BoolVal(second.contains(first));
			} else
				return new DynamicError("Argument does not match the required Class");

		} else {
			return new DynamicError("Argument does not match the required Class");
		}
	}

	public static Value callCompilerString(ArrayList<Value> operands, Env<Value> env) {

		Object firstArgument = operands.get(0);
		if (boa.debugger.Evaluator.DEBUG)
			System.out.println("string func has received" + firstArgument.getClass());
		if (firstArgument instanceof AnyVal) {
			AnyVal arg = ((AnyVal) firstArgument);
			Object actual = arg.getObject();
			String result = boa.functions.BoaAstIntrinsics.changedfileToString((ChangedFile) actual);
			if (boa.debugger.Evaluator.DEBUG)
				System.out.println("String function has got the result" + result);
			return new StringVal(result);
		} else if (firstArgument instanceof StringVal) {

			AnyVal arg = (AnyVal) env.get(((StringVal) firstArgument).v());
			Object actual = arg.getObject();
			if (actual instanceof ChangedFile) {
				String result = boa.functions.BoaAstIntrinsics.changedfileToString((ChangedFile) actual);
				if (boa.debugger.Evaluator.DEBUG)
					System.out.println("String function has got the result" + result);
				return new StringVal(result);
			}
		}
		return new DynamicError("pop Expects either String or Stack as First argument");

	}

	public static Value callCompilerGetAnnotation(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		StringVal secondArgument = (StringVal) operands.get(0);
		if ((secondArgument instanceof StringVal) && (firstArgument instanceof AnyVal)) {
			Object fst = ((AnyVal) firstArgument).getObject();
			String typeOfAnnotation = secondArgument.v();
			Object result = null;
			if (fst instanceof boa.types.Ast.Method)
				result = boa.functions.BoaModifierIntrinsics.getAnnotation((Method) fst, typeOfAnnotation);

			else if (fst instanceof boa.types.Ast.Declaration)
				result = boa.functions.BoaModifierIntrinsics.getAnnotation((boa.types.Ast.Declaration) fst,
						typeOfAnnotation);

			else if (fst instanceof boa.types.Ast.Namespace)
				result = boa.functions.BoaModifierIntrinsics.getAnnotation((boa.types.Ast.Namespace) fst,
						typeOfAnnotation);

			else if (fst instanceof boa.types.Ast.Variable)
				result = boa.functions.BoaModifierIntrinsics.getAnnotation((boa.types.Ast.Variable) fst,
						typeOfAnnotation);

			if (result == null)
				return null;
			else
				return new AnyVal(result);
		} else {
			return new DynamicError("One of the arguments does not match the requirment");
		}
	}

	public static Value callCompilerHasAnnotation(ArrayList<Value> operands, Env<Value> env) {
		int numberOfArguments = (int) operands.size();
		Object firstArgument = operands.get(0);
		boolean result = false;
		if (!(firstArgument instanceof AnyVal)) {
			Object fst = ((AnyVal) firstArgument).getObject();
			switch (numberOfArguments) {
			case 1:
				if (fst instanceof Declaration)
					return new BoolVal(boa.functions.BoaModifierIntrinsics.hasAnnotation((Declaration) fst));

				else if (fst instanceof Method)
					return new BoolVal(boa.functions.BoaModifierIntrinsics.hasAnnotation((Method) fst));

				else if (fst instanceof Namespace)
					return new BoolVal(boa.functions.BoaModifierIntrinsics.hasAnnotation((Namespace) fst));

				else if (fst instanceof Variable)
					return new BoolVal(boa.functions.BoaModifierIntrinsics.hasAnnotation((Variable) fst));
				else
					return new DynamicError("first argument does not match the requirment");

			case 2:
				StringVal secondArgument = (StringVal) operands.get(1);
				String str = secondArgument.v();
				if (fst instanceof Declaration)
					return new BoolVal(boa.functions.BoaModifierIntrinsics.hasAnnotation((Declaration) fst, str));

				else if (fst instanceof Method)
					return new BoolVal(boa.functions.BoaModifierIntrinsics.hasAnnotation((Method) fst, str));

				else if (fst instanceof Namespace)
					return new BoolVal(boa.functions.BoaModifierIntrinsics.hasAnnotation((Namespace) fst, str));

				else if (fst instanceof Variable)
					return new BoolVal(boa.functions.BoaModifierIntrinsics.hasAnnotation((Variable) fst, str));
				else
					return new DynamicError("first argument does not match the requirment");

			default:
				return new DynamicError("Nunmber of argumetns does not mactch the required");
			}

		}
		return new DynamicError("first argument does not meet the requirment");
	}

	public static Value callCompilerHasModifier(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		NumVal secondArgument = (NumVal) operands.get(1);
		long kind = secondArgument.v();
		if ((firstArgument instanceof AnyVal) && (secondArgument instanceof NumVal)) {
			Object fst = ((AnyVal) firstArgument).getObject();
			if (fst instanceof Declaration) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifier((Declaration) fst,
						ModifierKind.valueOf((int) kind)));
			} else if (fst instanceof Method) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifier((Method) fst,
						ModifierKind.valueOf((int) kind)));
			} else if (fst instanceof Namespace) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifier((Namespace) fst,
						ModifierKind.valueOf((int) kind)));
			} else if (fst instanceof Variable) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifier((Variable) fst,
						ModifierKind.valueOf((int) kind)));
			} else {
				return new DynamicError("class of first argument does not meet the criteria of has_modifier");
			}
		} else {
			return new DynamicError("Either of the arguments to has_modifier does not meet the requirment");
		}
	}

	public static Value callCompilerHasModifierFinal(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		if (firstArgument instanceof AnyVal) {
			Object fst = ((AnyVal) firstArgument).getObject();
			if (fst instanceof Declaration) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierFinal((Declaration) fst));
			} else if (fst instanceof Method) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierFinal((Method) fst));
			} else if (fst instanceof Namespace) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierFinal((Namespace) fst));
			} else if (fst instanceof Variable) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierFinal((Variable) fst));
			} else {
				return new DynamicError("class of first argument does not meet the criteria of has_modifier");
			}
		} else {
			return new DynamicError("Argument to has_modifier_final does not meet the requirment");
		}

	}

	public static Value callCompilerHasModifierNamespace(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		if (firstArgument instanceof AnyVal) {
			Object fst = ((AnyVal) firstArgument).getObject();
			if (fst instanceof Declaration) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierNamespace((Declaration) fst));
			} else if (fst instanceof Method) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierNamespace((Method) fst));
			} else if (fst instanceof Namespace) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierNamespace((Namespace) fst));
			} else if (fst instanceof Variable) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierNamespace((Variable) fst));
			} else {
				return new DynamicError("class of first argument does not meet the criteria of has_modifier");
			}
		} else {
			return new DynamicError("Argument to has_modifier_final does not meet the requirment");
		}

	}

	public static Value callCompilerHasModifierPrivate(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		if (firstArgument instanceof AnyVal) {
			Object fst = ((AnyVal) firstArgument).getObject();
			if (fst instanceof Declaration) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierPrivate((Declaration) fst));
			} else if (fst instanceof Method) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierPrivate((Method) fst));
			} else if (fst instanceof Namespace) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierPrivate((Namespace) fst));
			} else if (fst instanceof Variable) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierPrivate((Variable) fst));
			} else {
				return new DynamicError("class of first argument does not meet the criteria of has_modifier");
			}
		} else {
			return new DynamicError("Argument to has_modifier_final does not meet the requirment");
		}

	}

	public static Value callCompilerHasModifierProtected(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		if (firstArgument instanceof AnyVal) {
			Object fst = ((AnyVal) firstArgument).getObject();
			if (fst instanceof Declaration) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierProtected((Declaration) fst));
			} else if (fst instanceof Method) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierProtected((Method) fst));
			} else if (fst instanceof Namespace) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierProtected((Namespace) fst));
			} else if (fst instanceof Variable) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierProtected((Variable) fst));
			} else {
				return new DynamicError("class of first argument does not meet the criteria of has_modifier");
			}
		} else {
			return new DynamicError("Argument to has_modifier_final does not meet the requirment");
		}

	}

	public static Value callCompilerHasModifierStatic(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		if (firstArgument instanceof AnyVal) {
			Object fst = ((AnyVal) firstArgument).getObject();
			if (fst instanceof Declaration) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierStatic((Declaration) fst));
			} else if (fst instanceof Method) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierStatic((Method) fst));
			} else if (fst instanceof Namespace) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierStatic((Namespace) fst));
			} else if (fst instanceof Variable) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierStatic((Variable) fst));
			} else {
				return new DynamicError("class of first argument does not meet the criteria of has_modifier");
			}
		} else {
			return new DynamicError("Argument to has_modifier_final does not meet the requirment");
		}

	}

	public static Value callCompilerHasModifierSynchronized(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		if (firstArgument instanceof AnyVal) {
			Object fst = ((AnyVal) firstArgument).getObject();
			if (fst instanceof Declaration) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierSynchronized((Declaration) fst));
			} else if (fst instanceof Method) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierSynchronized((Method) fst));
			} else if (fst instanceof Namespace) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierSynchronized((Namespace) fst));
			} else if (fst instanceof Variable) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasModifierSynchronized((Variable) fst));
			} else {
				return new DynamicError("class of first argument does not meet the criteria of has_modifier");
			}
		} else {
			return new DynamicError("Argument to has_modifier_final does not meet the requirment");
		}

	}

	public static Value callCompilerHasVisibility(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		NumVal secondArgument = (NumVal) operands.get(1);
		long kind = secondArgument.v();
		if ((firstArgument instanceof AnyVal) && (secondArgument instanceof NumVal)) {
			Object fst = ((AnyVal) firstArgument).getObject();
			if (fst instanceof Declaration) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasVisibility((Declaration) fst,
						Visibility.valueOf((int) kind)));
			} else if (fst instanceof Method) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasVisibility((Method) fst,
						Visibility.valueOf((int) kind)));
			} else if (fst instanceof Namespace) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasVisibility((Namespace) fst,
						Visibility.valueOf((int) kind)));
			} else if (fst instanceof Variable) {
				return new BoolVal(boa.functions.BoaModifierIntrinsics.hasVisibility((Variable) fst,
						Visibility.valueOf((int) kind)));
			} else {
				return new DynamicError("class of first argument does not meet the criteria of has_modifier");
			}
		} else {
			return new DynamicError("Either of the arguments to has_modifier does not meet the requirment");
		}
	}

	public static Value callCompilerGetComments(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		if (firstArgument instanceof AnyVal) {
			Object changedFile = ((AnyVal) firstArgument).getObject();
			if (changedFile instanceof ChangedFile) {
				return new AnyVal(boa.functions.BoaAstIntrinsics.getcomments((ChangedFile) changedFile));
			} else {
				return new DynamicError("get_comments expects ChangedFile");
			}
		} else {
			return new DynamicError("Argument does not match the required Class");
		}
	}

	public static Value callCompilerHasFileType(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		Object secondArgument = operands.get(1);
		if ((firstArgument instanceof AnyVal) && (secondArgument instanceof StringVal)) {
			Object revision = ((AnyVal) firstArgument).getObject();
			String str = ((StringVal) revision).v();
			if (revision instanceof Revision) {
				return new BoolVal(boa.functions.BoaIntrinsics.hasfile((Revision) revision, str));
			} else {
				return new DynamicError("get_comments expects ChangedFile");
			}
		} else {
			return new DynamicError("Argument does not match the required Class");
		}
	}

	public static Value callCompilerIsKind(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		Object secondArgument = operands.get(1);
		if ((firstArgument instanceof StringVal) && (secondArgument instanceof NumVal)) {
			String revision = ((StringVal) firstArgument).v();
			long kind = ((NumVal) secondArgument).v();
			return new BoolVal(boa.functions.BoaIntrinsics.iskind(revision, ChangedFile.FileKind.valueOf((int) kind)));
		} else {
			return new DynamicError("Argument does not match the required Class");
		}
	}

	public static Value callCompilerNRand(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		if ((firstArgument instanceof NumVal)) {
			long number = ((NumVal) firstArgument).v();
			return new NumVal(boa.functions.BoaMathIntrinsics.nRand(number));
		} else {
			return new DynamicError("Argument does not match the required Class");
		}
	}

	public static Value callCompilerGetMetricCA(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		if ((firstArgument instanceof AnyVal)) {
			Object fst = ((AnyVal) firstArgument).getObject();
			try {
				return new NumVal(boa.functions.BoaMetricIntrinsics.getMetricCA(((Declaration) fst)));
			} catch (Exception e) {
				return new DynamicError("Argument does not match the required Class");
			}
		} else {
			return new DynamicError("Argument does not match the required Class");
		}
	}

	public static Value callCompilerGetMetricCBC(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		if ((firstArgument instanceof AnyVal)) {
			Object fst = ((AnyVal) firstArgument).getObject();
			try {
				return new NumVal(boa.functions.BoaMetricIntrinsics.getMetricCBC(((Declaration) fst)));
			} catch (Exception e) {
				return new DynamicError("Argument does not match the required Class");
			}
		} else {
			return new DynamicError("Argument does not match the required Class");
		}
	}

	public static Value callCompilerGetMetricDIT(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		if ((firstArgument instanceof AnyVal)) {
			Object fst = ((AnyVal) firstArgument).getObject();
			try {
				return new NumVal(boa.functions.BoaMetricIntrinsics.getMetricCA(((Declaration) fst)));
			} catch (Exception e) {
				return new DynamicError("Argument does not match the required Class");
			}
		} else {
			return new DynamicError("Argument does not match the required Class");
		}
	}

	public static Value callCompilerGetMetricLCOO(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		if ((firstArgument instanceof AnyVal)) {
			Object fst = ((AnyVal) firstArgument).getObject();
			try {
				return new NumVal(boa.functions.BoaMetricIntrinsics.getMetricLCOO(((Declaration) fst)));
			} catch (Exception e) {
				return new DynamicError("Argument does not match the required Class");
			}
		} else {
			return new DynamicError("Argument does not match the required Class");
		}
	}

	public static Value callCompilerGetMetricNOA(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		if ((firstArgument instanceof AnyVal)) {
			Object fst = ((AnyVal) firstArgument).getObject();
			try {
				return new NumVal(boa.functions.BoaMetricIntrinsics.getMetricNOA(((Declaration) fst)));
			} catch (Exception e) {
				return new DynamicError("Argument does not match the required Class");
			}
		} else {
			return new DynamicError("Argument does not match the required Class");
		}
	}

	public static Value callCompilerGetMetricNOC(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		if ((firstArgument instanceof AnyVal)) {
			Object fst = ((AnyVal) firstArgument).getObject();
			try {
				return new AnyVal(boa.functions.BoaMetricIntrinsics.getMetricNOC(((ASTRoot) fst)));
			} catch (Exception e) {
				return new DynamicError("Argument does not match the required Class");
			}
		} else {
			return new DynamicError("Argument does not match the required Class");
		}
	}

	public static Value callCompilerGetMetricNOO(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		if ((firstArgument instanceof AnyVal)) {
			Object fst = ((AnyVal) firstArgument).getObject();
			try {
				return new NumVal(boa.functions.BoaMetricIntrinsics.getMetricNOO(((Declaration) fst)));
			} catch (Exception e) {
				return new DynamicError("Argument does not match the required Class");
			}
		} else {
			return new DynamicError("Argument does not match the required Class");
		}
	}

	public static Value callCompilerGetMetricNPM(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		if ((firstArgument instanceof AnyVal)) {
			Object fst = ((AnyVal) firstArgument).getObject();
			try {
				return new NumVal(boa.functions.BoaMetricIntrinsics.getMetricNPM(((Declaration) fst)));
			} catch (Exception e) {
				return new DynamicError("Argument does not match the required Class");
			}
		} else {
			return new DynamicError("Argument does not match the required Class");
		}
	}

	public static Value callCompilerGetMetricRFC(ArrayList<Value> operands, Env<Value> env) {
		Object firstArgument = operands.get(0);
		if ((firstArgument instanceof AnyVal)) {
			Object fst = ((AnyVal) firstArgument).getObject();
			try {
				return new NumVal(boa.functions.BoaMetricIntrinsics.getMetricRFC(((Declaration) fst)));
			} catch (Exception e) {
				return new DynamicError("Argument does not match the required Class");
			}
		} else {
			return new DynamicError("Argument does not match the required Class");
		}
	}

	public static Value callCompilerFormat(ArrayList<Value> operands, Env<Value> env) {
		int numberofargs = (int) operands.size();
		Object[] argumentList = new Object[numberofargs - 1];
		Object realObject = null;
		if (numberofargs < 2)
			return new DynamicError("Wrong number of arguments in format function");

		StringVal firstArgument = (StringVal) operands.get(0);
		String format = (firstArgument).v();

		for (int i = 1; i < (operands.size() - 1); i++) {
			Value arg = operands.get(i);
			if (arg instanceof StringVal) {
				try {
					arg = env.get(((StringVal) arg).v());
				} catch (LookupException ex) {
					arg = operands.get(1);
				}
			}
			if (arg instanceof AnyVal) {
				realObject = ((AnyVal) arg).getObject();
				argumentList[i - 1] = realObject;
			} else if (arg instanceof StringVal) {
				realObject = ((StringVal) arg).v();
				argumentList[i - 1] = realObject;
			}
		}

		return new StringVal(boa.functions.BoaStringIntrinsics.format(format, argumentList));
	}

	public static Value callCompilerMatchPosns(ArrayList<Value> operands, Env<Value> env) {
		StringVal first = (StringVal) operands.get(0);
		StringVal second = (StringVal) operands.get(1);
		String fst = first.v();
		String snd = second.v();
		long[] list = boa.functions.BoaStringIntrinsics.matchPositions(fst, snd);
		ListVal<NumVal> result = new ListVal<NumVal>();
		for (long x : list) {
			result.add(new NumVal(x));
		}
		return result;
	}

	public static Value callCompilerMatchStrs(ArrayList<Value> operands, Env<Value> env) {
		StringVal first = (StringVal) operands.get(0);
		StringVal second = (StringVal) operands.get(1);
		String fst = first.v();
		String snd = second.v();
		String[] list = boa.functions.BoaStringIntrinsics.matchStrings(fst, snd);
		ListVal<StringVal> result = new ListVal<StringVal>();
		for (String x : list) {
			result.add(new StringVal(x));
		}
		return result;
	}

	public static Value callCompilerSplit(ArrayList<Value> operands, Env<Value> env) {
		StringVal first = (StringVal) operands.get(0);
		StringVal second = (StringVal) operands.get(1);
		String fst = first.v();
		String snd = second.v();
		String[] list = boa.functions.BoaStringIntrinsics.split(fst, snd);
		ListVal<StringVal> result = new ListVal<StringVal>();
		for (String x : list) {
			result.add(new StringVal(x));
		}
		return result;
	}

	public static Value callCompilerSplitAll(ArrayList<Value> operands, Env<Value> env) {

		Value fst = operands.get(0);
		if (fst instanceof StringVal) {
			StringVal first = (StringVal) operands.get(0);
			StringVal second = (StringVal) operands.get(1);

			String frst = first.v();
			String snd = second.v();
			String[] list = boa.functions.BoaStringIntrinsics.splitall(frst, snd);
			ListVal<StringVal> result = new ListVal<StringVal>();
			for (String x : list) {
				result.add(new StringVal(x));
			}
			return result;
		} else if (fst instanceof PairVal) {
			PairVal frst = (PairVal) fst;
			MapVal map = (MapVal) frst.fst();
			Value index = frst.snd();
			if (index instanceof PairVal) {
				Value indexFst = ((PairVal) index).fst();
				String indexString = ((StringVal) indexFst).v();

				try {
					indexString = ((StringVal) env.get(indexString)).v();
				} catch (LookupException ex) {
					indexString = ((StringVal) indexFst).v();
				}
				if (boa.debugger.Evaluator.DEBUG)
					System.out.println("map request to get:" + indexString);

				Value actualAgumentToSplit = (Value) map.get(indexString);
				if (actualAgumentToSplit instanceof UnitVal)
					return new ListVal();
				StringVal second = (StringVal) operands.get(1);
				String snd = second.v();
				String first = ((StringVal) actualAgumentToSplit).v();
				String[] list = boa.functions.BoaStringIntrinsics.splitall(first, snd);
				ListVal<StringVal> result = new ListVal<StringVal>();
				for (String x : list) {
					result.add(new StringVal(x));
				}
				return result;
			}
		}
		throw new UnsupportedOperationException();
	}

	public static Value callCompilerSplitN(ArrayList<Value> operands, Env<Value> env) {
		StringVal first = (StringVal) operands.get(0);
		StringVal second = (StringVal) operands.get(1);
		NumVal third = (NumVal) operands.get(2);
		String fst = first.v();
		String snd = second.v();
		String[] list = boa.functions.BoaStringIntrinsics.splitn(fst, snd, third.v());
		ListVal<StringVal> result = new ListVal<StringVal>();
		for (String x : list) {
			result.add(new StringVal(x));
		}
		return result;
	}

	public static Value callCompilerSubString(ArrayList<Value> operands, Env<Value> env) {
		int numberOfArgs = (int) operands.size();
		switch (numberOfArgs) {
		case 2:
			StringVal string = (StringVal) operands.get(0);
			NumVal second = (NumVal) operands.get(1);
			String result = boa.functions.BoaStringIntrinsics.substring(string.v(), second.v());
			return new StringVal(result);

		case 3:
			StringVal string3 = (StringVal) operands.get(0);
			NumVal second3 = (NumVal) operands.get(1);
			NumVal third3 = (NumVal) operands.get(2);
			String result3 = boa.functions.BoaStringIntrinsics.substring(string3.v(), second3.v(), third3.v());
			return new StringVal(result3);
		}
		return new DynamicError("NO case match in substring function");
	}

	public static Value callCompilerTrim(ArrayList<Value> operands, Env<Value> env) {
		StringVal first = (StringVal) operands.get(0);
		String fst = first.v();
		String result = boa.functions.BoaStringIntrinsics.trim(fst);
		return new StringVal(result);
	}

	public static Value callCompilerSqrt(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = Math.sqrt(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerRound(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = Math.round(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerPow(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		DoubleVal second = (DoubleVal) operands.get(1);
		double fst = first.v();
		double snd = second.v();
		double result = Math.pow(fst, snd);
		return new DoubleVal(result);
	}

	public static Value callCompilerMax(ArrayList<Value> operands, Env<Value> env) {
		Value first = operands.get(0);
		Value second = operands.get(1);
		if ((first instanceof DoubleVal) && (second instanceof DoubleVal)) {
			double fst = ((DoubleVal) first).v();
			double snd = ((DoubleVal) second).v();
			double result = Math.max(fst, snd);
			return new DoubleVal(result);
		} else if ((first instanceof NumVal) && (second instanceof NumVal)) {
			long fst = ((NumVal) first).v();
			long snd = ((NumVal) second).v();
			long result = Math.max(fst, snd);
			return new NumVal(result);
		}
		return new DynamicError("Arguments type are neither double nor long for max function");
	}

	public static Value callCompilerMin(ArrayList<Value> operands, Env<Value> env) {
		Value first = operands.get(0);
		Value second = operands.get(1);
		if ((first instanceof DoubleVal) && (second instanceof DoubleVal)) {
			double fst = ((DoubleVal) first).v();
			double snd = ((DoubleVal) second).v();
			double result = Math.min(fst, snd);
			return new DoubleVal(result);
		} else if ((first instanceof NumVal) && (second instanceof NumVal)) {
			long fst = ((NumVal) first).v();
			long snd = ((NumVal) second).v();
			long result = Math.min(fst, snd);
			return new NumVal(result);
		}
		return new DynamicError("Arguments type are neither double nor long for max function");
	}

	public static Value callCompilerUppercase(ArrayList<Value> operands, Env<Value> env) {
		StringVal first = (StringVal) operands.get(0);
		String fst = first.v();
		String result = boa.functions.BoaStringIntrinsics.upperCase(fst);
		return new StringVal(result);
	}

	public static Value callCompilerStringReplace(ArrayList<Value> operands, Env<Value> env) {
		StringVal first = (StringVal) operands.get(0);
		StringVal second = (StringVal) operands.get(1);
		StringVal third = (StringVal) operands.get(2);
		BoolVal fourth = (BoolVal) operands.get(3);
		String fst = first.v();
		String snd = second.v();
		String thd = third.v();
		boolean frth = fourth.v();
		String result = boa.functions.BoaStringIntrinsics.stringReplace(fst, snd, thd, frth);
		return new StringVal(result);
	}

	public static Value callCompilerAdd(ArrayList<Value> operands, Env<Value> env) {
		Value fst = operands.get(0);
		Value snd = operands.get(1);
		if (fst instanceof SetVal) {
			HashSet set = (HashSet) ((SetVal) fst).getMap();
			((SetVal) fst).add(snd.toString());
			return UnitVal.v;
		}
		throw new UnsupportedOperationException();
	}

	public static Value callCompilerAbs(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = Math.abs(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerDef(ArrayList<Value> operands, Env<Value> env) {
		return new BoolVal(operands != null && !(operands.get(0) instanceof DynamicError)
				&& !(operands.get(0) instanceof UnitVal));
	}

	public static Value callCompilerAcos(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = Math.acos(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerAcosh(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = boa.functions.BoaMathIntrinsics.acosh(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerAsin(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = Math.asin(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerAsinh(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = boa.functions.BoaMathIntrinsics.asinh(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerAtan(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = Math.tan(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerAtanh(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = boa.functions.BoaMathIntrinsics.atanh(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerAtan2(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double snd = first.v();
		double result = Math.atan2(fst, snd);
		return new DoubleVal(result);
	}

	public static Value callCompilerCos(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = Math.cos(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerNow(ArrayList<Value> operands, Env<Value> env) {
		if (boa.debugger.Evaluator.DEBUG)
			System.out.println("Returning current time");
		return new NumVal(System.currentTimeMillis());
	}

	public static Value callCompilerDayofyear(ArrayList<Value> operands, Env<Value> env) {
		if (boa.debugger.Evaluator.DEBUG)
			System.out.println("Reday of year");
		if (operands.get(0) instanceof NumVal) {
			long inputTime = (((NumVal) operands.get(0)).v());
			return new NumVal(boa.functions.BoaTimeIntrinsics.dayOfYear(inputTime));
		} else if (operands.get(0) instanceof StringVal) {
			Value operator = env.get((((StringVal) operands.get(0)).v()));
			if (operator instanceof NumVal) {
				long inputTime = (((NumVal) operator).v());
				if (boa.debugger.Evaluator.DEBUG)
					System.out.println(
							"day of function is returning" + boa.functions.BoaTimeIntrinsics.dayOfYear(inputTime));
				return new NumVal(boa.functions.BoaTimeIntrinsics.dayOfYear(inputTime));
			}
		}
		return new DynamicError("input was expected to be number");
	}

	public static Value callCompilerCosh(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = Math.cosh(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerSin(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = Math.sin(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerSinh(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = Math.sinh(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerTan(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = Math.tan(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerLog(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = Math.log(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerTrunc(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = boa.functions.BoaMathIntrinsics.trunc(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerRand(ArrayList<Value> operands, Env<Value> env) {
		double result = boa.functions.BoaMathIntrinsics.rand();
		return new DoubleVal(result);
	}

	public static Value callCompilerLog10(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = Math.log10(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerTanh(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = Math.tanh(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerCeil(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = Math.ceil(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerFloor(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = Math.floor(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerExp(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		double result = Math.exp(fst);
		return new DoubleVal(result);
	}

	public static Value callCompilerIsFinte(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		boolean result = boa.functions.BoaMathIntrinsics.isFinite(fst);
		return new BoolVal(result);
	}

	public static Value callCompilerIsInIfinte(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		boolean result = boa.functions.BoaMathIntrinsics.isInfinite(fst);
		return new BoolVal(result);
	}

	public static Value callCompilerIsNaN(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		boolean result = boa.functions.BoaMathIntrinsics.isNaN(fst);
		return new BoolVal(result);
	}

	public static Value callCompilerIsNormal(ArrayList<Value> operands, Env<Value> env) {
		DoubleVal first = (DoubleVal) operands.get(0);
		double fst = first.v();
		boolean result = boa.functions.BoaMathIntrinsics.isNormal(fst);
		return new BoolVal(result);
	}
}
