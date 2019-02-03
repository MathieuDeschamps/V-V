package vvAux.vvAux;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class RecursiveTest {
	
	RecursiveOp rec;


	@Before
	public void init(){
		
		rec = new RecursiveOp();
		
	}
	
	
	
	@Test
	public void negativFact(){
		
		int res = rec.factorielle(-1);
		
		assertEquals(1, res);
		
	}
	
	@Test
	public void positivFact(){
		
		int res = rec.factorielle( 3 );
		assertEquals( 6, res);
		
	}
}
