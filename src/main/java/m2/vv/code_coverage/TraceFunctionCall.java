package m2.vv.code_coverage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

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
		
		if( args.length != 1 )
		{
			System.out.println("Error we need a maeven projrct path as parameter");
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
		
		JUnitCore core = new JUnitCore();
		URL classUrl = new URL("file://"+ args[0]+"/target/classes/");
		URL testUrl = new URL("file://"+ args[0]+"/target/test-classes/");
		URL[] classUrls = { classUrl, testUrl };
		URLClassLoader ucl = new URLClassLoader(classUrls);

		ClassPool pool = ClassPool.getDefault();

		final String folder = "/home/jeremy/Programmation/java/stack/target/cl	asses/";
		final String testFolder = "/home/jeremy/Programmation/java/stack/target/test-classes/";
		
		try {
			pool.appendClassPath(folder);
			pool.appendClassPath(testFolder);
		} catch (NotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		CtClass testFunctions = null;
		CtClass functions = null;
		try {
			testFunctions = pool.get("samples.BoundedStackTest");
			functions = pool.get("samples.BoundedStack");
		} catch (NotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (CtMethod method : testFunctions.getDeclaredMethods()) {
			String instruction = String.format("{System.out.println(\"%s\");}",
					"*" + method.getName());
			try {
				method.insertBefore(instruction);
			} catch (CannotCompileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	

		for (CtMethod method : functions.getDeclaredMethods()) {
			
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
		
		Class<?> boundedStackTestClass = ucl.loadClass("samples.BoundedStackTest");
				
		Result result = core.run(boundedStackTestClass);
		for (Failure failure : result.getFailures()) {
			System.out.println("| FAILURE: " + failure.getTrace());
		}

		System.out.println("FINISHED");
		System.out.println(String.format("| IGNORED: %d", result.getIgnoreCount()));
		System.out.println(String.format("| FAILURES: %d", result.getFailureCount()));
		System.out.println(String.format("| RUN: %d", result.getRunCount()));

	}

}
