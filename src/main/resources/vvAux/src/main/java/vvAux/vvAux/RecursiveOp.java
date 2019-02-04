package vvAux.vvAux;
/**
 * 
 * @author DESCHAMPS Mathieu && ESNAULT Jeremie
 *
 */
public class RecursiveOp {
	
	public int factorielle ( int nb )
	{
		return nb <= 1
			       ? 1
			       : nb * factorielle(nb - 1);
	}
}
