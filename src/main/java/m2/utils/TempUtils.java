package m2.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import m2.exceptions.CopyException;
import m2.exceptions.DeleteTmpException;
import m2.exceptions.WrongMaevenProjectException;


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
	 * @throws WrongMaevenProjectException 
	 */
	public String copyTarget( String projectPath ) throws CopyException, WrongMaevenProjectException{
	
		File maevenDirectory = new File( projectPath );
		if( !(maevenDirectory.exists( ) && maevenDirectory.isDirectory( ) ) )
		{
			throw new WrongMaevenProjectException("Directory does not exist");
			
		}
		String classTarget  = projectPath +"/target/classes";
		File classesDirectory = new File( classTarget );
		if( ! (classesDirectory.exists( ) && classesDirectory.isDirectory( ) ) ){
			
			throw new WrongMaevenProjectException( "Target classes directory is missing");
		
		}
		 String testTarget = projectPath + "/target/test-classes";
		 File testClassesDirectory = new File( testTarget );
		 if( ! (testClassesDirectory.exists( ) && testClassesDirectory.isDirectory( ) ) ){
			 
			 throw new WrongMaevenProjectException("Target test classes directory is missing");
			 
		 }
		 
		 String home = System.getProperty("user.home");
		 try {
			 
			 FileUtils.copyDirectory(new File(projectPath+"/target"), new File(home+"/tmpVV"+projectPath+"/target"));
			 System.out.println("cp -R "+projectPath+"/target "+home+"/tmp"+projectPath);
			//Runtime.getRuntime().exec("cp -R "+projectPath+"/target "+home+"/tmpVV"+projectPath);
		} catch (IOException e) {
			throw new CopyException( e.getMessage( ) );
			
		}
		
		
		this.copyPath = home+"/tmpVV"+projectPath;
		return home+"/tmpVV"+projectPath;
	}
	
	/**
	 * Delete the temporary target created 
	 * @throws DeleteTmpException if the rm command get wring
	 */
	public void deleteCopy( ) throws DeleteTmpException{
		
		try {
			System.out.println(copyPath);
			Runtime.getRuntime().exec("rm -R "+System.getProperty("user.home")+"/tmpVV" );
		} catch (IOException e) {
			throw new DeleteTmpException( e.getMessage( ) );
		}
		
	}


	public String getCopyPath() {
		return copyPath;
	}
	

}
