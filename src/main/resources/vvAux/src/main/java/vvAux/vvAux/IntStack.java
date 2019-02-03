package vvAux.vvAux;

import java.util.ArrayList;
import java.util.List;

public class IntStack {

	private int size;
	private List<Integer> values;
	private int index;
	
	
	public IntStack( int size ){
		this.size = size;
		values = new ArrayList<>();
		index = -1;
	}
	
	public void empiler( int val ) throws Exception
	{
		System.out.println("size: "+size+" values size: " +values.size());
		if( size > values.size( ) )
		{
			values.add(val);
			index++;
		}
		else
		{
			throw new Exception("Pile pleine");
		}
	}
	
	public int depiler() throws Exception{
		if( index >=  0 )
		{
			
			int res = values.remove(index);
			index --;
			return res;
		}
		else throw new Exception("Pile vide");
	}
	
	public int hautDePile() throws Exception{
		
		if( index >= 0 ){
			return values.get(index);
		}
		throw new Exception("pile vide");
	}
	
	public int getStackSize(){
		return index +1;
	}
	
}
