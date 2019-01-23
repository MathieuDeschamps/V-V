package m2.utils;

import java.io.File;
import java.io.IOException;

import m2.exceptions.CopyException;
import m2.exceptions.DeleteTmpException;


/**
 * 
 * @author DESCHAMPS Mathieu && ESNAULT Jeremie
 * This class has the role to provide method which will copy a maeven target class ans test class
 *
 */
public class TempUtils {
	
	
	private String copyPath = "";
	
	
	/**
	 * Copy the target directory of a 
	 * @param projectPath the maeven's path to copy
	 * @return the path of the copy
	 * @throws CopyException if copy command get wrong
	 */
	public String copyTarget( String projectPath ) throws CopyException{
	
		File maevenDirectory = new File( projectPath );
		if( !(maevenDirectory.exists( ) && maevenDirectory.isDirectory( ) ) )
		{
			System.out.println("The project path passed is not valid");
			System.exit(-1);
			
		}
		String classTarget  = projectPath +"/target/classes";
		File classesDirectory = new File( classTarget );
		if( ! (classesDirectory.exists( ) && classesDirectory.isDirectory( ) ) ){
			
			System.out.println( "Target classes are missing");
			System.exit( -1 );
		}
		 String testTarget = projectPath + "/target/test-classes";
		 File testClassesDirectory = new File( testTarget );
		 if( ! (testClassesDirectory.exists( ) && testClassesDirectory.isDirectory( ) ) ){
			 System.out.println("Target test-classes is not valid");
			 System.exit( -1 );
			 
		 }
		 
		 try {
			Runtime.getRuntime().exec("cp -R "+projectPath+"/target /tmp/"+projectPath);
		} catch (IOException e) {
			throw new CopyException( e.getMessage( ) );
			
		}
		
		
		this.copyPath = "/tmp/"+projectPath;
		return "/tmp/"+projectPath;
	}
	
	/**
	 * Delete the temporary target created 
	 * @throws DeleteTmpException if the rm command get wring
	 */
	public void deleteCopy( ) throws DeleteTmpException{
		
		try {
			Runtime.getRuntime().exec("rm -R "+this.copyPath );
		} catch (IOException e) {
			throw new DeleteTmpException( e.getMessage( ) );
		}
		
	}


	public String getCopyPath() {
		return copyPath;
	}
	

}
