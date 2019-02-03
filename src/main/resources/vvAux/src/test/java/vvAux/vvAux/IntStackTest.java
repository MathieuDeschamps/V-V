package vvAux.vvAux;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author DESCHAMPS Mathieu && ESNAULT Jeremie
 *
 */
public class IntStackTest {
	
	IntStack stack;
	
	@Before
	public void init()
	{
		stack = new IntStack(10);
	}
	
	@Test(expected=Exception.class)
	public void exceptionPileVide() throws Exception{
		
		stack.depiler();
	}
	
	@Test
	public void empileDepileSameValue() throws Exception{
		stack.empiler(1);
		int val = stack.depiler();
		
		assertEquals( 1, val);
	}
	
	@Test 
	public void testSize() throws Exception{
		assertEquals( 0, stack.getStackSize( ));
		
		stack.empiler(1);
		assertEquals(1, stack.getStackSize( ));
	}
	
	@Test(expected=Exception.class)
	public void testPilePleine() throws Exception{
		stack.empiler(1);
		stack.empiler(1);
		stack.empiler(1);
		stack.empiler(1);
		stack.empiler(1);
		stack.empiler(1);
		stack.empiler(1);
		stack.empiler(1);
		stack.empiler(1);
		stack.empiler(1);
		
		//la ça plante
		stack.empiler(1);
		
	}

}
