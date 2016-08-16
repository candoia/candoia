package boa.debugger.value;

import boa.debugger.Evaluator;
import boa.types.BoaType;

import java.util.Stack;

/**
 * @author nmtiwari
 * @param <T>
 *
 */
public class StackVal<T> extends BoaType implements Value {
	protected Stack<T> stack;

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
	}

	public T pop() {
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

	@Override
	public Value compute(Value rhs, String op) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get() {
		return this.stack;
	}
	
	@Override
	public int hashCode(){
		return this.get().hashCode();
	}

}
