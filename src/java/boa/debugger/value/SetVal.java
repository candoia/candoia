package boa.debugger.value;

import boa.types.BoaType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author nmtiwari
 * @param <V>
 * @param <T>
 *
 */
public class SetVal<V, T> extends BoaType implements Value {
	protected HashSet<V> set;
	protected T prevIndex;

	public SetVal() {
		set = new HashSet<V>();
	}

	public SetVal(HashSet<V> m) {
		this.set = m;
	}

	public String tostring() {
		return "stack tostring";
	}

	public void clearAll() {
		set = new HashSet<V>();
	}

	public long size() {
		return set.size();
	}

	public T[] keySet() {
		return (T[]) set.toArray();
	}

	public void remove(T index) {
		if (boa.debugger.Evaluator.DEBUG) {
			System.out.println("Request to delete:" + index + "from :" + set.size() + "elements");
		}
		set.remove(index);
	}

	public boolean contains(T obj) {
		if (boa.debugger.Evaluator.DEBUG)
			System.out.println("set contains returns:" + set.contains(obj));
		return set.contains(obj);
	}

	public Set<V> getMap() {
		return set;
	}

	public void add(V val) {
		if (boa.debugger.Evaluator.DEBUG) {
			System.out.println("Request to add:" + "from :" + set.size() + "elements");
		}
		(this.set).add(val);
		if (boa.debugger.Evaluator.DEBUG) {
			System.out.println("Currently size of array:" + set.size());
		}
	}

	public void setPrevIndex(T index) {
		this.prevIndex = index;
	}

	public boolean hasKey(Value index) {
		return set.contains(index);
	}

	public T getPrevIndex() {
		return this.prevIndex;
	}

	public boolean equals(Value v) {
		throw new UnsupportedOperationException();
	}

	public boolean isLessThan(Value v) {
		return false;
	}

	public boolean isLessThanOrEqualTo(Value v) {
		return false;
	}

	@Override
	public String toString() {
		Object[] elements = this.keySet();
		String str = "";
		for (Object ele : elements) {
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
		return this.set;
	}

}
