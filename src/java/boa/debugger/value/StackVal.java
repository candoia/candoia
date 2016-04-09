package boa.debugger.value;

import java.util.Stack;

import boa.debugger.Evaluator;
import boa.types.BoaType;

/**
 * @author nmtiwari
 * @param <T>
 *
 */
public class StackVal<T> extends BoaType implements Value {
	protected Stack<T> stack;
	public static long count = 0;

	public StackVal() {
		stack = new Stack<T>();
	}

	public String tostring() {
		return "stack tostring";
	}

	public void clearAll() {
		stack = new Stack<T>();
	}

	public void push(T item) {
		stack.push(item);
		count++;
		if (Evaluator.DEBUG)
			System.out.println("stack has" + count);
	}

	public T pop() {
		// if((stack.empty()))
		// return (T)new DynamicError("pop without single push");
		if (Evaluator.DEBUG)
			System.out.println("stack has pop" + count);
		count--;
		return (T) stack.pop();
	}

	@Override
	public boolean equals(Value v) {
		return false;
	}

	@Override
	public boolean isLessThan(Value v) {
		return false;
	}

	@Override
	public boolean isLessThanOrEqualTo(Value v) {
		return false;
	}

	@Override
	public long size() {
		return 0;
	}

	@Override
	public String toString() {
		String str ="";
		for(Object ele : stack){
			str += ele.toString();
		}
		return str;
	}

}
