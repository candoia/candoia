package boa.debugger.value;

import boa.types.BoaType;

/**
 * @author nmtiwari
 *
 */
public class DynamicError extends BoaType implements Value {
	private String message = "Unknown dynamic error.";

	public DynamicError() {
	}

	public DynamicError(String message) {
		this.message = message;
	}

	public String tostring() {
		return "" + message;
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
		return message;
	}

	@Override
	public Value compute(Value rhs, String op) {
		throw new UnsupportedOperationException();
	}

}
