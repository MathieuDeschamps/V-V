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
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import m2.utils.ConsoleUtils;
import m2.model.Edge;
import m2.model.Graph;
import m2.model.Model;
import m2.model.Node;
import m2.model.ProjectModel;

public class TraceFunctionCall {

	private ProjectModel projectModel;
	private List<Graph> graphs;
	private String trace;
	public TraceFunctionCall( ProjectModel projectModel ){
		this.graphs = new ArrayList<Graph>();
		this.projectModel = projectModel;
		this.trace = "";
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
				try {
					Graph graph = new Graph();
					graph.setName(classFile);
					CtMethod[] classMethods;
					classMethods = pool.get(classFile).getDeclaredMethods();
					buildGraph(graph, classMethods);
					graphs.add(graph);
					// trace is call after build graph otherwise return error
					trace(pool, classFile, folder, "-", traceFile);
				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		// save the default print stream to restore it later
		PrintStream oldPrintStream = System.out;
		
		for( String test: testList )
		{
			CtMethod[] testMethods;
			try {
				testMethods = pool.get(test).getDeclaredMethods();
				Graph graph = new Graph();
				graph.setName(test);
				buildGraph(graph, testMethods);
				graphs.add(graph);
				// trace is call after build graph otherwise return error
				trace(pool, test, testFolder, "*", traceFile);
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Class<?> testClass = ucl.loadClass(test);
			ConsoleUtils.redirect(outputFile);
			Result result = core.run(testClass);
			ConsoleUtils.redirect(oldPrintStream);
			for (Failure failure : result.getFailures()) {
				System.out.println("| FAILURE: " + failure.getTrace());
			}

			System.out.println("FINISHED" + test);
			System.out.println(String.format("| IGNORED: %d", result.getIgnoreCount()));
			System.out.println(String.format("| FAILURES: %d", result.getFailureCount()));
			System.out.println(String.format("| RUN: %d", result.getRunCount()));
			String toDot;
			System.out.println("Graph:\n");	
			for(Graph graph: graphs) {
				toDot = graph.toDot();
				System.out.println(toDot + '\n');
				this.trace += toDot;
			}
			Model model = new Model(test, result.getRunCount(), result.getFailureCount(), result.getIgnoreCount(), this.trace);
			this.projectModel.addModel(model);
		}
	}
	
	/**
	 * @param pool contains the class
	 * @param folder path folder to the class
	 * @param packageName name of the package class
	 * @param prefixe keyword
	 */
	public static void trace(ClassPool pool, String packageName, String folder, String prefixe, String output) {
		CtClass functions = null;
		try {
			functions = pool.get(packageName);
		} catch (NotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		CtMethod[] methods = functions.getDeclaredMethods();
		for (CtMethod method : methods) {
			String instructionLogin = String.format("{System.out.println(\"%s\");}",
					prefixe + method.getName() + " ;");
			
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

	/**
	 * Build a graph form methods
	 * @param graph
	 * @param mehtods
	 * @return
	 */
	public static  Graph buildGraph(Graph graph, CtMethod[] methods) {
		if(graph == null) {
			return new Graph();
		}
		for (CtMethod method : methods) {
			try {
					method.instrument(new ExprEditor() {
						String tab = "";			
						public void edit(MethodCall m) throws CannotCompileException {
							addToGraph(graph, method, m);							
						}
					});
			} catch (CannotCompileException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}		
			
		}
		return graph;
	}
	
	public static void addToGraph(Graph graph, CtMethod method, MethodCall methodCall) {
		Node firstNode = new Node(method.getName());
		Node secondNode = new Node(methodCall.getMethodName());
		Edge edge = new Edge(firstNode, secondNode, true);
		graph.addEdge(edge);
	}


}
