package boa.debugger;


import boa.debugger.value.StringVal;
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
	T get(Value search_var);
	int getLength();
	String print();
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

		@Override
		public T get(Value search_var) {
			throw new LookupException("No binding found for name: "
					+ search_var);
		}

		@Override
		public int getLength() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String print() {
			// TODO Auto-generated method stub
			return "";
		}
	}

	static public class ExtendEnv<T> implements Env<T>, Cloneable {
		private Env<T> _saved_env;
		private String _var;
		private T _val;
		int size = 0;

		public ExtendEnv(Env<T> saved_env, String var, T val) {
			_saved_env = saved_env;
			_var = var;
			_val = val;
			this.size++;
		}

		public ExtendEnv(Object saved_env, String var, T val) {
			_saved_env = (Env<T>) saved_env;
			_var = var;
			_val = val;
			this.size++;
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

		@Override
		public T get(Value search_var) {
			if(search_var instanceof StringVal){
				try{
					return this.get(search_var.toString());
				}catch(LookupException e){
					return (T) search_var;
				}
			}else{
				return (T) search_var;
			}
		}

		@Override
		public int getLength() {
			// TODO Auto-generated method stub
			return 1 + _saved_env.getLength();
		}

		@Override
		public String print() {
			// TODO Auto-generated method stub
			return _var + ", " + _saved_env.print() ;
		}
	}


}
