package boa.debugger.value;

/**
 * @author nmtiwari
 *
 */
public interface Value {

	public boolean equals(Value v);

	public boolean isLessThan(Value v);

	public boolean isLessThanOrEqualTo(Value v);

	public long size();

	public Value compute(Value rhs, String op);
}
