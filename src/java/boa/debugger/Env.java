package boa.debugger;

import boa.debugger.value.Value;

/**
 * Representation of an environment, which maps variables to values.
 * 
 * @author hridesh
 *
 */
@SuppressWarnings("unchecked")
public interface Env<T> extends Cloneable{
	T get(String search_var);
	boolean isEmpty();
	Object clone();
	void updateValue(String search_var,T v);

	@SuppressWarnings("serial")
	static public class LookupException extends RuntimeException {
		LookupException(String message) {
			super(message);
		}
	}

	static public class EmptyEnv<T> implements Env<T>, Cloneable {
		public T get(String search_var) {
			throw new LookupException("No binding found for name: "
					+ search_var);
		}

		public boolean isEmpty() {
			return true;
		}
		
		@Override
		public Object clone(){
			Env<Value>  e = new EmptyEnv();
			return e;
		}
		
		public void updateValue(String search_var,T val){
			throw new LookupException("No binding found for name: "+ search_var);
		}
	}

	static public class ExtendEnv<T> implements Env<T>, Cloneable {
		private Env<T> _saved_env;
		private String _var;
		private T _val;

		public ExtendEnv(Env<T> saved_env, String var, T val) {
			_saved_env = saved_env;
			_var = var;
			_val = val;
		}

		public ExtendEnv(Object saved_env, String var, T val) {
			_saved_env = (Env<T>) saved_env;
			_var = var;
			_val = val;
		}
		
		public void updateValue(String search_var, T val){
			if(search_var.equals(_var)){
				_val=val;
			}
			else{
				_saved_env.updateValue(search_var, val);
			}
		}

		public synchronized T get(String search_var) {
			if (search_var.equals(_var))
				return _val;
			return _saved_env.get(search_var);
		}

		public boolean isEmpty() {
			return false;
		}

		public Env<T> saved_env() {
			return _saved_env;
		}

		public String var() {
			return _var;
		}

		public T val() {
			return _val;
		}
		
		@Override
		public Object clone(){
			Env<Value>  e = new ExtendEnv(this._saved_env, this._var, this._val);
			return e;
		}
	}


}
