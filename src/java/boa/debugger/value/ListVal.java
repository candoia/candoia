package boa.debugger.value;

import boa.types.BoaType;
import boa.types.Diff.ChangedFile;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author nmtiwari
 *
 */
public class ListVal<T> extends BoaType implements Value {
	ArrayList<T> values;

	public ListVal() {
		values = new ArrayList<>();
	}

	public ListVal(T[] list) {
		values = new ArrayList<T>(Arrays.asList(list));
	}

	public long size() {
		return values.size();
	}

	public ArrayList<T> v() {
		return values;
	}

	public boolean isEmpty() {
		return values.isEmpty();
	}

	public Object get(long index) {
		if (index >= 0 && (index <= values.size() - 1))
			return values.get((int) index);
		else
			throw new IllegalArgumentException();
	}

	public void add(T v) {
		values.add(v);
	}

	@Override
	public boolean equals(Value v) {
		if (v instanceof ListVal) {
			for (int i = 0; i < values.size() - 1; i++) {
				if (!((ListVal<?>) v).get(i).equals(this.get(i))) {
					return false;
				}
			}
			return true;
		} else {
			throw new IllegalArgumentException();
		}

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
	public String toString() {
		String str = "";
		for (Object obj : values) {
			str += obj.toString();
		}
		return str;
	}

	@Override
	public Value compute(Value rhs, String op) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get() {
		return this.values;
	}

	@Override
	public int hashCode() {
		int result = 31;
		for (T ele : this.values) {
			result = 37 * result + ele.hashCode();
		}
		return result;
	}
}
