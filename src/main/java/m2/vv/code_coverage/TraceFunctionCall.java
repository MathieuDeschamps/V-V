package m2.vv.code_coverage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
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
import javassist.bytecode.MethodInfo;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import m2.utils.ConsoleUtils;
import m2.model.Model;
import m2.model.ProjectModel;

public class TraceFunctionCall {

	private ProjectModel projectModel;
	public TraceFunctionCall( ProjectModel projectModel ){
		
		this.projectModel = projectModel;
	}
	
	public void process(String path) throws MalformedURLException, ClassNotFoundException {
		
		if( path == null )
		{
			System.out.println("Error we need a maeven project path as parameter");
			System.exit(-1);
		}
		String projectPath = path;
		File maevenDirectory = new File( projectPath );
		if( !(maevenDirectory.exists( ) && maevenDirectory.isDirectory( ) ) )
		{
			
			System.out.println("The path passed as argument is not valid");
			System.out.println("The invalid path is: " + path);
			System.exit(-1);
			
		}
		String classTarget  = projectPath +"/target/classes";
		/*File classesDirectory = new File( classTarget );
		System.out.println("tralala: "+classTarget);
		if( ! (classesDirectory.exists( ) && classesDirectory.isDirectory( ) ) ){
			
			System.out.println( "Target classes are missing");	
			System.exit( -1 );
		}*/
		
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
		URL classUrl = new URL("file://"+ projectPath+"/target/classes/");
		URL testUrl = new URL("file://"+ projectPath+"/target/test-classes/");
		URL[] classUrls = { classUrl, testUrl };
		URLClassLoader ucl = new URLClassLoader(classUrls);
		
		ClassPool pool = ClassPool.getDefault();
		
		final String folder = projectPath+"/target/classes/";
		final String testFolder = projectPath+"/target/test-classes/";
		final String outputFile = projectPath+"/TraceFunction.txt";
		final String traceFile = projectPath+"/Trace.txt";
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
				trace(pool, folder, classFile, "-", traceFile);
			}
		}
		
		// save the default print stream to restore it later
		PrintStream oldPrintStream = System.out;
		
		for( String test: testList )
		{
			trace(pool, testFolder, test, "*", traceFile);
			Class<?> testClass = ucl.loadClass(test);
			//ConsoleUtils.redirect(outputFile);
			Result result = core.run(testClass);
			//ConsoleUtils.redirect(oldPrintStream);
			for (Failure failure : result.getFailures()) {
				System.out.println("| FAILURE: " + failure.getTrace());
			}

			System.out.println("FINISHED" + test);
			System.out.println(String.format("| IGNORED: %d", result.getIgnoreCount()));
			System.out.println(String.format("| FAILURES: %d", result.getFailureCount()));
			System.out.println(String.format("| RUN: %d", result.getRunCount()));
			Model model = new Model(test, result.getRunCount(), result.getFailureCount(), result.getIgnoreCount(), "");
			this.projectModel.addModel(model);
		}
	}
	
	/**
	 * @param pool contains the class
	 * @param folder path folder to the class
	 * @param packageName name of the package class
	 * @param prefixe keyword
	 */
	public static  void trace(ClassPool pool, String folder, String packageName, String prefixe, String output) {
		CtClass functions = null;		
		
		try {
			functions = pool.get(packageName);
		} catch (NotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (CtMethod method : functions.getDeclaredMethods()) {
			try {
					method.instrument(new ExprEditor() {
						String tab = "";			
						public void edit(MethodCall m) throws CannotCompileException {
							//tab += "-";
							String trace = tab + m.getClassName() + "." + m.getMethodName() + " " + m.getSignature();
							PrintStream old = System.out;
							//ConsoleUtils.redirect(output);
							//System.out.println(trace);
							//ConsoleUtils.redirect(old);
							tab = tab.substring(0, tab.length());
						}
					});
			} catch (CannotCompileException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String instructionLogin = String.format("{System.out.println(\"%s\");}",
					prefixe +"Enter:" + method.getName() + " ;");
			
			String instructionLogout = String.format("{System.out.println(\"%s\");}", prefixe + "Exit:" + method.getName() +" ;");
			try {
				method.insertBefore(instructionLogin);
			} catch (CannotCompileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			functions.writeFile(folder);
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
