package boa.debugger.value;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import boa.compiler.ast.types.AbstractType;

/**
 * @author nmtiwari
 *
 */
public class MapVal<T, V> implements Value {
	protected Map<T, V> map;
	protected AbstractType indexTyp;
	protected AbstractType valueTyp;
	
	public MapVal() {
		map = new HashMap<T, V>();
	}

	public MapVal(Map<T, V> m) {
		this.map = m;
	}

	public String tostring() {
		return "map tostring";
	}

	public void clearAll() {
		map = new HashMap<T, V>();
	}

	public long size() {
		return map.size();
	}

	@SuppressWarnings("unchecked")
	public T[] keySet() {
		Set<T> set = this.map.keySet();
		return (T[]) set.toArray();
	}

	public void remove(T index) {
		map.remove(index);
	}

	public Map<T, V> getMap() {
		return map;
	}

	public void put(T index, V val) {
		(this.map).put(index, val);
	}

	public boolean hasKey(Object index) {
		return map.containsKey(index);
	}

	@SuppressWarnings("unchecked")
	public V get(T index) {
		if (hasKey(index)) {
			Object result = (this.map).get(index);
			return (V) result;
		}
		return (V) UnitVal.v;
	}

	public boolean equals(Value v) {
		return false;
	}

	public boolean isLessThan(Value v) {
		return false;
	}

	public boolean isLessThanOrEqualTo(Value v) {
		return false;
	}

	@Override
	public Value compute(Value rhs, String op) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get() {
		return this.map;
	}

	@Override
	public int hashCode(){
		int result = 31;
		for (T ele : this.map.keySet()) {
			result = 37 * result + ele.hashCode();
		}
		return result;
	}
	
	public void setValueType(AbstractType typ){
		this.valueTyp = typ;
	}
	
	public AbstractType getIndexType(){return this.indexTyp;}
	
	public void setIndexType(AbstractType typ){
		this.indexTyp = typ;
	}
	
	public AbstractType getValueType(){return this.valueTyp;}
	
}
