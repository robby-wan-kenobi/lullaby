package model;

import java.util.ArrayList;
import java.util.Collections;

public class SortedList<E> extends ArrayList {
	public SortedList(){
		super();
	}
	public boolean add(Object item){
		boolean addResult = super.add(item);
		Collections.sort(this);
		return addResult;
	}
	public boolean remove(Object item){
		boolean removeResult = super.remove(item);
		Collections.sort(this);
		return removeResult;
	}
}
