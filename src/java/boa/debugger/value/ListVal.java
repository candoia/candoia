package boa.debugger.value;

import boa.types.BoaType;

import java.util.ArrayList;

/**
 * @author nmtiwari
 *
 */
public class ListVal extends BoaType implements Value {
	ArrayList<Object> values;

	public ListVal() {
		values = new ArrayList<Object>();
	}

	public long size() {
		return values.size();
	}

	public ArrayList<Object> v() {
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
	
	public void add(Value v) {
		values.add(v);
	}

	@Override
	public boolean equals(Value v) {
		if (v instanceof ListVal) {
			for (int i = 0; i < values.size() - 1; i++) {
				if (!((ListVal) v).get(i).equals(this.get(i))) {
					return false;
				}
			}
			return true;
		} else {
			return false;
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

}
