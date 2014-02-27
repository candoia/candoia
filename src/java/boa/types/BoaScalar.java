package boa.types;

/**
 * A {@link BoaType} representing any other scalar value type.
 * 
 * @author anthonyu
 * 
 */
public class BoaScalar extends BoaType {
	/** {@inheritDoc} */
	@Override
	public boolean assigns(final BoaType that) {
		// if that is a function, check the return type
		if (that instanceof BoaFunction)
			return this.assigns(((BoaFunction) that).getType());

		// otherwise, if it's not a scalar, forget it
		if (!(that instanceof BoaScalar))
			return false;

		// check that the classes match
		return this.getClass().equals(that.getClass());
	}

	/** {@inheritDoc} */
	@Override
	public boolean accepts(final BoaType that) {
		// if that is a function, check the return type
		if (that instanceof BoaFunction)
			return this.assigns(((BoaFunction) that).getType());

		// otherwise, if it's not a scalar, forget it
		if (!(that instanceof BoaScalar))
			return false;

		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean compares(final BoaType that) {
		// if that is a function, check the return type
		if (that instanceof BoaFunction)
			return this.compares(((BoaFunction) that).getType());

		// otherwise, check if the types are equivalent one way or the other
		if (this.assigns(that) || that.assigns(this))
			return true;

		// forget it
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object obj) {
		if (obj == null)
			return false;

		return this.getClass().equals(obj.getClass());
	}

	@Override
	public String toString() {
		return "scalar";
	}
}