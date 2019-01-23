package m2.vv.code_coverage;

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
		JUnitCore core = new JUnitCore();
		URL classUrl = new URL("file:///home/jeremy/Programmation/java/stack/target/classes/");
		URL testUrl = new URL("file:///home/jeremy/Programmation/java/stack/target/test-classes/");
		URL[] classUrls = { classUrl, testUrl };
		URLClassLoader ucl = new URLClassLoader(classUrls);
		
		ClassPool pool = ClassPool.getDefault();

		final String folder = "/home/jeremy/Programmation/java/stack/target/classes/";
		final String testFolder = "/home/jeremy/Programmation/java/stack/target/test-classes/";
		
		try {
			pool.appendClassPath(folder);
			pool.appendClassPath(testFolder);
		} catch (NotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		TraceFunctionCall.trace(pool, folder, testFolder, "samples.BoundedStack", "samples.BoundedStackTest");
		
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
		instruction1 += "{writer = new java.io.BufferedWriter(new java.io.FileWriter.FileWriter(\"/home/jeremy/VVTest.txt\"));}";
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
				int i = 0;
//				for(CtClass type: types) {
					 if ( types.length == 1 && types[0].isPrimitive()) {
						 instruction2 += "{System.out.println(\"ici\"+ $1 );}" ;
                     } else {
                         instruction2 += "{System.out.println( System.identityHashCode( $args[" + i + "] ) ) ;}" ;
                     }
//					 i++;
//				}
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
