package m2.vv.code_coverage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class TraceFunctionCall {

	public static void main(String[] args) throws MalformedURLException, ClassNotFoundException {
		args = new String [1];
		args[0] = "/home/boby/Bureau/testing-tutorial-master";
		// checks if the agument is well formed
		if( args.length != 1 )
		{
			System.out.println("Error we need a maeven project path as parameter");
			System.exit(-1);
		}
		String projectPath = args[0];
		File maevenDirectory = new File( args[0]);
		if( !(maevenDirectory.exists( ) && maevenDirectory.isDirectory( ) ) )
		{
			System.out.println("The path passed as argument is not valid");
			System.exit(-1);
			
		}
		String classTarget  = projectPath +"/target/classes";
		File classesDirectory = new File( classTarget );
		if( ! (classesDirectory.exists( ) && classesDirectory.isDirectory( ) ) ){
			
			System.out.println( "Target classes are missing");	
			System.exit( -1 );
		}
		
		// create the collections of class and test class
		Collection<File> filesClass = FileUtils.listFiles( new File(classTarget), null, true);
		List<String> classList = new ArrayList<>();
		System.out.println("classList: ");
		filesClass.forEach( file -> {
			System.out.println(file.getAbsolutePath( ).replaceAll(projectPath+"/target/classes/", "").replaceAll("/", ".").replaceAll(".class",""));
			classList.add(file.getAbsolutePath( ).replaceAll(projectPath+"/target/classes/", "").replaceAll("/", ".").replaceAll(".class",""));
		});
		
		System.out.println("testList: ");
		Collection<File> testFiles = FileUtils.listFiles( new File(projectPath+"/target/test-classes"), null, true);
		List<String> testList = new ArrayList<>();
		testFiles.forEach( file -> {
			System.out.println(file.getAbsolutePath().replaceAll( projectPath+"/target/test-classes/", "").replaceAll("/", ".").replaceAll(".class", ""));
			testList.add( file.getAbsolutePath().replaceAll( projectPath+"/target/test-classes/", "").replaceAll("/", ".").replaceAll(".class", ""));
		});
		
		
		//System.exit(0);
		JUnitCore core = new JUnitCore();
		URL classUrl = new URL("file://"+ args[0]+"/target/classes/");
		URL testUrl = new URL("file://"+ args[0]+"/target/test-classes/");
		URL[] classUrls = { classUrl, testUrl };
		URLClassLoader ucl = new URLClassLoader(classUrls);
		
		ClassPool pool = ClassPool.getDefault();
		
		final String folder = projectPath+"/target/classes/";
		final String testFolder = projectPath+"/target/test-classes/";
		
		try {
			pool.appendClassPath(folder);
			pool.appendClassPath(testFolder);
		} catch (NotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for( String classFile : classList)
		{
			if( testList.contains( classFile+"Test" ) )
			{
				trace(pool, folder, testFolder, classFile, classFile);
			}
		}
		
		for( String test: testList )
		{
			Class<?> testClass = ucl.loadClass(test);
			
			Result result = core.run(testClass);
			for (Failure failure : result.getFailures()) {
				System.out.println("| FAILURE: " + failure.getTrace());
			}

			System.out.println("FINISHED");
			System.out.println(String.format("| IGNORED: %d", result.getIgnoreCount()));
			System.out.println(String.format("| FAILURES: %d", result.getFailureCount()));
			System.out.println(String.format("| RUN: %d", result.getRunCount()));
		}
			
	}
	
	/**
	 * @param pool contains the tested class and the test class
	 * @param folder path folder to the tested class
	 * @param testFolder path folder to the test class
	 */
	public static  void trace(ClassPool pool, String folder, String testFolder, String packageClass, String packageTest) {
		
		CtClass testFunctions = null;
		CtClass functions = null;		
		
		try {
			testFunctions = pool.get(packageTest);
			functions = pool.get(packageClass);
		} catch (NotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String instruction1 = "";
		instruction1 += "{java.io.BufferedWriter writer;}";
		instruction1 += "{writer = new java.io.BufferedWriter(new java.io.FileWriter.FileWriter(\"/home/boby/VVTest.txt\"));}";
		instruction1 += "{writer.write(\"Coucou\");";
		instruction1 += "{writer.close();}";
		
		for (CtMethod method : testFunctions.getDeclaredMethods()) {
			String instruction = String.format("{System.out.println(\"%s\");}",
					"*" + method.getName());
			try {
				//method.insertBefore(instruction1);
				method.insertBefore(instruction);
			} catch (CannotCompileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		
		String instruction2 = "";
		for (CtMethod method : functions.getDeclaredMethods()) {

			try {
				CtClass[] types = method.getParameterTypes();
				System.out.print(method.getName() + " " + types.length + "\n");
				
				method.insertBefore(instruction2);
			} catch (NotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (CannotCompileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String instruction = String.format("{System.out.println(\"%s\");}",
					"-" + method.getName() + " ;");
			try {
				method.insertBefore(instruction);
			} catch (CannotCompileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		try {
			functions.writeFile(folder);
			testFunctions.writeFile(testFolder);
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
