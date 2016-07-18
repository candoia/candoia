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
	protected Set<V> set;
	protected T prevIndex;

	public SetVal() {
		set = new HashSet<V>();
	}

	public SetVal(Set<V> set2) {
		this.set = set2;
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
		set.remove(index);
	}

	public boolean contains(T obj) {
		return set.contains(obj);
	}

	public Set<V> getMap() {
		return set;
	}

	public void add(V val) {
		(this.set).add(val);
	}

	public boolean hasKey(Value index) {
		return set.contains(index);
	}

	public T getPrevIndex() {
		return this.prevIndex;
	}

	public void setPrevIndex(T index) {
		this.prevIndex = index;
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

	@Override
	public int hashCode() {
		int result = 31;
		for (Object ele : this.set.toArray()) {
			result = 37 * result + ele.hashCode();
		}
		return result;
	}
}
