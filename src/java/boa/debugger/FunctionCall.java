package boa.debugger;

import boa.compiler.ast.Component;
import boa.compiler.ast.statements.Block;
import boa.compiler.ast.types.FunctionType;
import boa.debugger.Env.ExtendEnv;
import boa.debugger.Env.LookupException;
import boa.debugger.value.DynamicError;
import boa.debugger.value.FunVal;
import boa.debugger.value.NumVal;
import boa.debugger.value.Value;
import boa.types.Ast.Comment.CommentKind;
import boa.types.Ast.Expression.ExpressionKind;
import boa.types.Ast.Modifier.ModifierKind;
import boa.types.Ast.Modifier.Visibility;
import boa.types.Ast.Statement.StatementKind;
import boa.types.Ast.TypeKind;
import boa.types.Code.CodeRepository.RepositoryKind;
import boa.types.Diff.ChangedFile.FileKind;
import boa.types.Issues.Issue;
import boa.types.Issues.Issue.IssueKind;
import boa.types.Shared.ChangeKind;

import java.util.ArrayList;
import java.util.List;

public class FunctionCall {
		public static boolean isInBuiltFunction(String str) {
		for (functionList f : functionList.values()) {
			if (f.name().equals(str)) {
				return true;
			}
		}
		return false;
	};

		public static boolean isInBuiltEnum(String str) {
		for (InbuiltEnumList f : InbuiltEnumList.values()) {
			if (f.name().equals(str)) {
				return true;
			}
		}
		return false;
	};

	public static Value getInbuildEnumFieldValue(String enumName, String FieldName) {
		switch (InbuiltEnumList.valueOf(enumName)) {
		case TypeKind:
			try {
				int result = TypeKind.valueOf(FieldName).getNumber();
				return new NumVal(result);
			} catch (IllegalArgumentException ex) {
				return new DynamicError("There is no such field in" + enumName);
			}

		case StatementKind:
			try {
				int result = StatementKind.valueOf(FieldName).getNumber();
				return new NumVal(result);
			} catch (IllegalArgumentException ex) {
				return new DynamicError("There is no such field in" + enumName);
			}

		case ExpressionKind:
			try {
				int result = ExpressionKind.valueOf(FieldName).getNumber();
				return new NumVal(result);
			} catch (IllegalArgumentException ex) {
				return new DynamicError("There is no such field in" + enumName);
			}

		case ModifierKind:
			try {
				int result = ModifierKind.valueOf(FieldName).getNumber();
				return new NumVal(result);
			} catch (IllegalArgumentException ex) {
				return new DynamicError("There is no such field in" + enumName);
			}

		case Visibility:
			try {
				int result = Visibility.valueOf(FieldName).getNumber();
				return new NumVal(result);
			} catch (IllegalArgumentException ex) {
				return new DynamicError("There is no such field in" + enumName);
			}

		case CommentKind:
			try {
				int result = CommentKind.valueOf(FieldName).getNumber();
				return new NumVal(result);
			} catch (IllegalArgumentException ex) {
				return new DynamicError("There is no such field in" + enumName);
			}

		case RepositoryKind:
			try {
				int result = RepositoryKind.valueOf(FieldName).getNumber();
				return new NumVal(result);
			} catch (IllegalArgumentException ex) {
				return new DynamicError("There is no such field in" + enumName);
			}

		case FileKind:
			try {
				int result = FileKind.valueOf(FieldName).getNumber();
				return new NumVal(result);
			} catch (IllegalArgumentException ex) {
				return new DynamicError("There is no such field in" + enumName);
			}

		case IssueKind:
			try {
				int result = IssueKind.valueOf(FieldName).getNumber();
				return new NumVal(result);
			} catch (IllegalArgumentException ex) {
				return new DynamicError("There is no such field in" + enumName);
			}

		case ChangeKind:
			try {
				int result = ChangeKind.valueOf(FieldName).getNumber();
				return new NumVal(result);
			} catch (IllegalArgumentException ex) {
				return new DynamicError("There is no such field in" + enumName);
			}
		case Priority:
			try {
				int result = Issue.Priority.valueOf(FieldName).getNumber();
				return new NumVal(result);
			} catch (IllegalArgumentException ex) {
				return new DynamicError("There is no such field in" + enumName);
			}
		case Severity:
			try {
				int result = Issue.Severity.valueOf(FieldName).getNumber();
				return new NumVal(result);
			} catch (IllegalArgumentException ex) {
				return new DynamicError("There is no such field in" + enumName);
			}
		case State:
			try {
				int result = Issue.State.valueOf(FieldName).getNumber();
				return new NumVal(result);
			} catch (IllegalArgumentException ex) {
				return new DynamicError("There is no such field in" + enumName);
			}
		default:
			return new DynamicError("There is no such Enum as" + enumName);
		}

	}

	public static Value executeFunction(Value operand, ArrayList<Value> operation, Env<Value> env,
			Evaluator evaluator) {
		boolean isInbuiltOperation = isInBuiltFunction(operand.toString());
		if (isInbuiltOperation) {
			return executeInBuiltFunctions(operand.toString(), operation, env);
		}
		return executeUserDefinedFunctions((FunVal) operand, operation, env, evaluator);
	}

	public static boolean hasBeenOverloaded(String operand, Env<Value> env) {
		try {
			env.get(operand);
			return true;
		} catch (LookupException ex) {
			return false;
		}
	}

	public static Value executeInBuiltFunctions(String operand, ArrayList<Value> operation, Env<Value> env) {
		switch (functionList.valueOf(operand)) {
		case abs:
			return InterpreterBoaFunctionMapping.callCompilerAbs(operation, env);
		case add:
			return InterpreterBoaFunctionMapping.callCompilerAdd(operation, env);

		case acos:
			return InterpreterBoaFunctionMapping.callCompilerAcos(operation, env);
		case acosh:
			return InterpreterBoaFunctionMapping.callCompilerAcosh(operation, env);
		case asin:
			return InterpreterBoaFunctionMapping.callCompilerAsin(operation, env);
		case asinh:
			return InterpreterBoaFunctionMapping.callCompilerAsinh(operation, env);
		case atan:
			return InterpreterBoaFunctionMapping.callCompilerAtan(operation, env);
		case atan2:
			return InterpreterBoaFunctionMapping.callCompilerAtan2(operation, env);
		case atanh:
			return InterpreterBoaFunctionMapping.callCompilerAtanh(operation, env);
		case clear:
			return InterpreterBoaFunctionMapping.callCompilerClear(operation, env);
		case formattime:
			return InterpreterBoaFunctionMapping.callCompilerFormattime(operation, env);
		case cos:
			return InterpreterBoaFunctionMapping.callCompilerCos(operation, env);
		case now:
			return InterpreterBoaFunctionMapping.callCompilerNow(operation, env);
		case dayofyear:
			return InterpreterBoaFunctionMapping.callCompilerDayofyear(operation, env);

		case cosh:
			return InterpreterBoaFunctionMapping.callCompilerCosh(operation, env);

		case strcontains:
			return InterpreterBoaFunctionMapping.callCompilerStrContains(operation, env);

		case contains:
			return InterpreterBoaFunctionMapping.callCompilerContains(operation, env);
		// case lookup:
		// return InterpreterBoaFunctionMapping.callCompilerLookup(operation,
		// env);

		case ceil:
			return InterpreterBoaFunctionMapping.callCompilerCeil(operation, env);
		case exp:
			return InterpreterBoaFunctionMapping.callCompilerExp(operation, env);
		case floor:
			return InterpreterBoaFunctionMapping.callCompilerFloor(operation, env);
		case sin:
			return InterpreterBoaFunctionMapping.callCompilerSin(operation, env);
		case sinh:
			return InterpreterBoaFunctionMapping.callCompilerSinh(operation, env);
		case tan:
			return InterpreterBoaFunctionMapping.callCompilerTan(operation, env);
		case tanh:
			return InterpreterBoaFunctionMapping.callCompilerTanh(operation, env);
		case def:
			return InterpreterBoaFunctionMapping.callCompilerDef(operation, env);
		case format:
			return InterpreterBoaFunctionMapping.callCompilerFormat(operation, env);
		case getast:
			return InterpreterBoaFunctionMapping.callCompilerGetAst(operation, env);
		case get_annotation:
			return InterpreterBoaFunctionMapping.callCompilerGetAnnotation(operation, env);
		case getcomments:
			return InterpreterBoaFunctionMapping.callCompilerGetComments(operation, env);
		case getsnapshot:
			try {
				return InterpreterBoaFunctionMapping.callCompilerGetSnapShot(operation, env);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		case has_annotation:
			return InterpreterBoaFunctionMapping.callCompilerHasAnnotation(operation, env);
		case hasfiletype:
			return InterpreterBoaFunctionMapping.callCompilerHasFileType(operation, env);
		case haskey:
			return InterpreterBoaFunctionMapping.callCompilerHasKey(operation, env);

		case keys:
			return InterpreterBoaFunctionMapping.callCompilerKeys(operation, env);
		case has_modifier:
			return InterpreterBoaFunctionMapping.callCompilerHasModifier(operation, env);
		case has_modifier_final:
			return InterpreterBoaFunctionMapping.callCompilerHasModifierFinal(operation, env);
		case has_modifier_namespace:
			return InterpreterBoaFunctionMapping.callCompilerHasModifierNamespace(operation, env);
		case has_modifier_private:
			return InterpreterBoaFunctionMapping.callCompilerHasModifierPrivate(operation, env);
		case has_modifier_protected:
			return InterpreterBoaFunctionMapping.callCompilerHasModifierProtected(operation, env);
		case has_modifier_public:
			return InterpreterBoaFunctionMapping.callCompilerHasModifierPublic(operation, env);
		case has_modifier_static:
			return InterpreterBoaFunctionMapping.callCompilerHasModifierStatic(operation, env);
		case has_modifier_synchronized:
			return InterpreterBoaFunctionMapping.callCompilerHasModifierSynchronized(operation, env);
		case isfixingrevision:
			return InterpreterBoaFunctionMapping.callCompilerIsFixingRevision(operation, env);
		case iskind:
			return InterpreterBoaFunctionMapping.callCompilerIsKind(operation, env);
		case has_visibility:
			return InterpreterBoaFunctionMapping.callCompilerHasVisibility(operation, env);
		case isliteral:
			return InterpreterBoaFunctionMapping.callCompilerisLiteral(operation, env);
		case isfinte:
			return InterpreterBoaFunctionMapping.callCompilerIsFinte(operation, env);
		case isinfinte:
			return InterpreterBoaFunctionMapping.callCompilerIsInIfinte(operation, env);

		case isnan:
			return InterpreterBoaFunctionMapping.callCompilerIsNaN(operation, env);
		case isnormal:
			return InterpreterBoaFunctionMapping.callCompilerIsNormal(operation, env);
		case len:
			return InterpreterBoaFunctionMapping.callCompilerLen(operation, env);
		case log:
			return InterpreterBoaFunctionMapping.callCompilerLog(operation, env);

		case log10:
			return InterpreterBoaFunctionMapping.callCompilerLog10(operation, env);
		case lowercase:
			return InterpreterBoaFunctionMapping.callCompilerLowerCase(operation, env);
		case match:
			return InterpreterBoaFunctionMapping.callCompilerMatch(operation, env);
		case matchposns:
			return InterpreterBoaFunctionMapping.callCompilerMatchPosns(operation, env);
		case matchstrs:
			return InterpreterBoaFunctionMapping.callCompilerMatchStrs(operation, env);
		case get_metric_ca:
			return InterpreterBoaFunctionMapping.callCompilerGetMetricCA(operation, env);
		case get_metric_cbc:
			return InterpreterBoaFunctionMapping.callCompilerGetMetricCBC(operation, env);
		case get_metric_dit:
			return InterpreterBoaFunctionMapping.callCompilerGetMetricDIT(operation, env);
		case get_metric_lcoo:
			return InterpreterBoaFunctionMapping.callCompilerGetMetricLCOO(operation, env);
		case get_metric_noa:
			return InterpreterBoaFunctionMapping.callCompilerGetMetricNOA(operation, env);
		case get_metric_noc:
			return InterpreterBoaFunctionMapping.callCompilerGetMetricNOC(operation, env);
		case get_metric_noo:
			return InterpreterBoaFunctionMapping.callCompilerGetMetricNOO(operation, env);
		case get_metric_npm:
			return InterpreterBoaFunctionMapping.callCompilerGetMetricNPM(operation, env);
		case get_metric_rfc:
			return InterpreterBoaFunctionMapping.callCompilerGetMetricRFC(operation, env);
		case max:
			return InterpreterBoaFunctionMapping.callCompilerMax(operation, env);
		case min:
			return InterpreterBoaFunctionMapping.callCompilerMin(operation, env);
		case nrand:
			return InterpreterBoaFunctionMapping.callCompilerNRand(operation, env);
		case pop:
			return InterpreterBoaFunctionMapping.callCompilerPop(operation, env);
		case pow:
			return InterpreterBoaFunctionMapping.callCompilerPow(operation, env);
		case push:
			return InterpreterBoaFunctionMapping.callCompilerPush(operation, env);
		case rand:
			return InterpreterBoaFunctionMapping.callCompilerRand(operation, env);
		case remove:
			return InterpreterBoaFunctionMapping.callCompilerRemove(operation, env);
		case round:
			return InterpreterBoaFunctionMapping.callCompilerRound(operation, env);
		case substring:
			return InterpreterBoaFunctionMapping.callCompilerSubString(operation, env);
		case split:
			return InterpreterBoaFunctionMapping.callCompilerSplit(operation, env);
		case splitall:
			return InterpreterBoaFunctionMapping.callCompilerSplitAll(operation, env);
		case splitn:
			return InterpreterBoaFunctionMapping.callCompilerSplitN(operation, env);
		case strfind:
			return InterpreterBoaFunctionMapping.callCompilerStrFind(operation, env);
		case string:
			return InterpreterBoaFunctionMapping.callCompilerString(operation, env);

		case strreplace:
			return InterpreterBoaFunctionMapping.callCompilerStringReplace(operation, env);
		case sqrt:
			return InterpreterBoaFunctionMapping.callCompilerSqrt(operation, env);

		case trim:
			return InterpreterBoaFunctionMapping.callCompilerTrim(operation, env);
		case trunc:
			return InterpreterBoaFunctionMapping.callCompilerTrunc(operation, env);
		case uppercase:
			return InterpreterBoaFunctionMapping.callCompilerUppercase(operation, env);
		case visit:
			return InterpreterBoaFunctionMapping.callCompilerVisit(operation, env);
		case yearof:
			return InterpreterBoaFunctionMapping.callCompilerYearOf(operation, env);
        case getAsList:
			return InterpreterBoaFunctionMapping.callCompileGetAsList(operation, env);
		default:
			throw new UnsupportedOperationException();
		}

	}

	public static Value executeUserDefinedFunctions(FunVal operand, ArrayList<Value> actuals, Env<Value> env,
			Evaluator evaluator) {
		FunVal function = operand;
		FunctionType type = function.type();
		List<Component> _formals = type.getArgs();
		long numOfArgs = _formals.size();
		Env<Value> temp = env;
		for (int i = 0; i < numOfArgs; i++) {
			// TODO: Add support for dynamic type checking for arguments
			// AbstractType typeOfArg = _formals.get(i).getType();
			String id = _formals.get(i).getIdentifier().getToken();
			Value actual = actuals.get(i);
			temp = new ExtendEnv<Value>(temp, id, actual);
		}
		Block body = function.body();
		Value result = body.accept(evaluator, temp);
		System.out.println("Executor function returns:" + result.get());
		return result;
	}

protected enum functionList {
		abs, acos, acosh, add, asin, asinh, atan, atan2, atanh, ceil, clear, cos, cosh, dayofyear, strcontains, now, exp, floor, sin, sinh, tan, formattime, tanh, def, format, getast, get_annotation, lookup, getcomments, get_metric_ca, get_metric_cbc, get_metric_dit, getLOC, get_metric_lcoo, get_metric_noa, get_metric_noc, get_metric_noo, get_metric_npm, get_metric_rfc, getsnapshot, has_annotation, hasfiletype, keys, contains, haskey, has_modifier, has_modifier_final, has_modifier_namespace, has_modifier_private, has_modifier_protected, has_modifier_public, has_modifier_static, has_modifier_synchronized, has_visibility, isfinte, isinfinte, isnormal, isnan, isfixingrevision, iskind, isliteral, len, log, log10, lowercase, max, min, match, matchposns, matchstrs, nrand, pop, pow, push, rand, remove, round, substring, split, splitall, splitn, strfind, string, strreplace, sqrt, trim, trunc, uppercase, visit, yearof, getAsList
	}

protected enum InbuiltEnumList {
		TypeKind, StatementKind, ExpressionKind, ModifierKind, Visibility, CommentKind, RepositoryKind, FileKind, IssueKind, ChangeKind, Priority, Severity, State
	}
}
