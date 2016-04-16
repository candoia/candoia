package boa.debugger.value;
import boa.compiler.ast.types.AbstractType;
import boa.types.BoaType;

/**
 * @author nmtiwari
 *
 */
public class BindingVal extends BoaType implements Value {

	String id;
	AbstractType type;
	Value initializer;

	public BindingVal(String id, AbstractType type, Value initializer) {
		this.id = id;
		this.type = type;
		this.initializer = initializer;
	}

	public BindingVal() {
		this.id = null;
		this.type = null;
		this.initializer = null;
	}

	public void setID(String id) {
		this.id = id;
	}

	public void setType(AbstractType type) {
		this.type = type;
	}

	public void setInit(Value val) {
		this.initializer = val;
	}

	public String getID() {
		return id;
	}

	public AbstractType getType() {
		return type;
	}

	public Value getInitializer() {
		return initializer;
	}

	@Override
	public boolean equals(Value v) {
		if (v instanceof BindingVal) {
			return ((BindingVal) v).getID().equals(this.getID());
		}
		throw new IllegalArgumentException("Compare operator has to have same types.");
	}

	@Override
	public boolean isLessThan(Value v) {
		if (v instanceof StringVal) {
			return ((StringVal) v).v().compareTo(this.getID()) <= -1;
		}
		throw new IllegalArgumentException("Compare operator has to have same types.");
	}

	@Override
	public boolean isLessThanOrEqualTo(Value v) {
		if (v instanceof StringVal) {
			return (((StringVal) v).v().compareTo(this.getID()) <= -1)
					|| (((StringVal) v).v().compareTo(this.getID()) == 0);
		}
		throw new IllegalArgumentException("Compare operator has to have same types.");
	}

	@Override
	public long size() {
		return 0;
	}

	@Override
	public String toString() {
		return "(" + id + ", " + type + ", " + initializer + ")"; 
	}

	@Override
	public Value compute(Value rhs, String op) {
		throw new UnsupportedOperationException();
	}


}
