package m2.code_coverage;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.xalan.trace.TraceManager;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import m2.utils.ConsoleUtils;
import m2.model.Edge;
import m2.model.Graph;
import m2.model.MethodModel;
import m2.model.Model;
import m2.model.Node;
import m2.model.ProjectModel;

public class TraceFunctionCall {
	
	public final static char SEP_SECTION = '#';
	public final static char PRE_TEST = '*';
	public final static char PRE_CLASS = '-';
	public final static char PRE_PARAM = '(';
	public static final char SEP_TYPE_VALUE = ':';
	public static final char SEP_PARAM = ',';
	public static final char END_PARAM = ')';
	// Separator between package name and class name
	public final static char SEP_PACK_CLASS = '.';
	// Separator between class name and method name
	public final static char SEP_CLASS_METHOD = '+';
	// Separator between method call
	public final static char END_METHOD_CALL = '\n';
	
	public final static char PRE_EXIT_METHOD = '<';

	private ProjectModel projectModel;
	private List<Edge<MethodModel>> allEdges;
	private String trace;

	public TraceFunctionCall(ProjectModel projectModel) {
		this.allEdges = new ArrayList<>();
		this.projectModel = projectModel;
		this.trace = "";
	}

	public void process(String path) throws MalformedURLException, ClassNotFoundException {

		if (path == null) {
			System.out.println("Error we need a maeven project path as parameter");
			System.exit(-1);
		}
		String projectPath = path;
		File maevenDirectory = new File(projectPath);
		if (!(maevenDirectory.exists() && maevenDirectory.isDirectory())) {

			System.out.println("The path passed as argument is not valid");
			System.out.println("The invalid path is: " + path);
			System.exit(-1);

		}
		String classTarget = projectPath + "/target/classes";
		File classesDirectory = new File(classTarget);
		System.out.println("exist: "+classesDirectory.exists()+" "+classTarget);
		System.out.println("dir: "+ classesDirectory.isDirectory());
		if (!(classesDirectory.exists() && classesDirectory.isDirectory())) {

			System.out.println("Target classes are missing");
			System.exit(-1);
		}

		// create the collections of class and test class
		Collection<File> filesClass = FileUtils.listFiles(new File(classTarget), null, true);
		List<String> classList = new ArrayList<>();
		System.out.println("classList: ");
		filesClass.forEach(file -> {
			System.out.println(file.getAbsolutePath().replaceAll(projectPath + "/target/classes/", "")
					.replaceAll("/", ".").replaceAll(".class", ""));
			classList.add(file.getAbsolutePath().replaceAll(projectPath + "/target/classes/", "").replaceAll("/", ".")
					.replaceAll(".class", ""));
		});

		System.out.println("testList: ");
		Collection<File> testFiles = FileUtils.listFiles(new File(projectPath + "/target/test-classes"), null, true);
		List<String> testList = new ArrayList<>();
		testFiles.forEach(file -> {
			System.out.println(file.getAbsolutePath().replaceAll(projectPath + "/target/test-classes/", "")
					.replaceAll("/", ".").replaceAll(".class", ""));
			testList.add(file.getAbsolutePath().replaceAll(projectPath + "/target/test-classes/", "")
					.replaceAll("/", ".").replaceAll(".class", ""));
		});

		// System.exit(0);
		JUnitCore core = new JUnitCore();
		URL classUrl = new URL("file://" + projectPath + "/target/classes/");
		URL testUrl = new URL("file://" + projectPath + "/target/test-classes/");
		URL[] classUrls = { classUrl, testUrl };
		URLClassLoader ucl = new URLClassLoader(classUrls);

		ClassPool pool = ClassPool.getDefault();

		final String folder = projectPath + "/target/classes/";
		final String testFolder = projectPath + "/target/test-classes/";
		final String outputFile = projectPath + "/TraceFunction.txt";
		final String traceFile = projectPath + "/Trace.txt";
		try {
			pool.appendClassPath(folder);
			pool.appendClassPath(testFolder);
		} catch (NotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (String classFile : classList) {
			try {
				CtMethod[] classMethods;
				CtClass currentClass = pool.get(classFile);
				classMethods = currentClass.getDeclaredMethods();

				allEdges.addAll(findEdges(classMethods));
				// trace is call after build graph otherwise return error
				trace(currentClass, folder, PRE_CLASS + "");
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// save the default print stream to restore it later
		PrintStream oldPrintStream = System.out;

		for (String test : testList) {
			CtMethod[] testMethods;
			try {
				CtClass currentTest = pool.get(test);
				testMethods = currentTest.getDeclaredMethods();
				allEdges.addAll(findEdges(testMethods));
				// trace is call after build graph otherwise return error
				trace(currentTest, testFolder, SEP_SECTION + "" + PRE_TEST);
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Class<?> testClass = ucl.loadClass(test);
			ConsoleUtils.redirect(outputFile);
			Result result = core.run(testClass);
			ConsoleUtils.redirect(oldPrintStream);

			trace = readTrace(outputFile);
			//System.out.println(trace);
			ParseFunctionCall parser = new ParseFunctionCall(allEdges, trace);
			List<Graph<MethodModel>> graphedTrace = parser.process();

			System.out.println("FINISHED" + test);
			System.out.println(String.format("| IGNORED: %d", result.getIgnoreCount()));
			System.out.println(String.format("| FAILURES: %d", result.getFailureCount()));
			System.out.println(String.format("| RUN: %d", result.getRunCount()));
			Map<String, BufferedImage> imageTrace = new HashMap<>();
			String toDot = "";
			for (Graph<MethodModel> graph : graphedTrace) {
				toDot += graph.toDot();
				imageTrace.put(graph.getName(), graph.toImage());
			}
			System.out.println("GRAPH:\n" + toDot);
			Model model = new Model(test, result.getRunCount(), result.getFailureCount(), result.getIgnoreCount(),
					toDot, imageTrace);
			this.projectModel.addModel(model);
		}
	}

	/**
	 * Add lines in the code of the class to print the name of called class
	 * 
	 * @param aClass
	 * @param folder
	 *            path folder to the class
	 * @param prefixe
	 *            helped to parse the trace before
	 */
	private static void trace(CtClass aClass, String folder, String prefixe) {

		CtMethod[] methods = aClass.getDeclaredMethods();
		for (CtMethod method : methods) {
			String instructionBefore = String.format("{System.out.println(\"%s\");}",
					prefixe + aClass.getName() + SEP_CLASS_METHOD + method.getName());
			
			instructionBefore += traceParam(method);
			
			String instructionAfter = String.format("{System.out.println(\"%s\");}", ""+PRE_EXIT_METHOD);
			try {
				
				method.insertBefore(instructionBefore);
				method.insertAfter(instructionAfter);
			} catch (CannotCompileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			aClass.writeFile(folder);
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Add to the string the data from the method about the parameter
	 * @param method
	 * @return
	 */
	private static String traceParam(CtMethod method) {
		String result = "";
		CtClass[] types = new CtClass[0];
		try {
			types = method.getParameterTypes();
		} catch (NotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		int i = 0;
		result +=  String.format("{System.out.print(\"%s\");}", PRE_PARAM) ;
		for(CtClass type: types) {
			 if ( types.length == 1 && types[0].isPrimitive()) {
				 if(i == 0) {
					 result += String.format("{System.out.print(\"%s\"+ $1 );}", type.getName().toString() + SEP_TYPE_VALUE) ;
				 }else {
					 result += String.format("{System.out.print(\"%s\"+ $1);}", SEP_PARAM + type.getName().toString() + SEP_TYPE_VALUE) ;
				 }
             } else {
            	 if(i == 0) {
					 result += String.format("{System.out.print(\"%s\");}", type.getName().toString() + SEP_TYPE_VALUE) ;
				 }else {
					 result += String.format("{System.out.print(\"%s\");}", SEP_PARAM + type.getName().toString() + SEP_TYPE_VALUE) ;
				 }
            	 //instructionBefore += "{System.out.println( System.identityHashCode( $args[" + i + "] ) ) ;}" ;
             }
			 i++;
		}
		result +=  String.format("{System.out.print(\"%s\");}", END_PARAM + "\\" + END_METHOD_CALL) ;
		
		return result;
	}

	/**
	 * Find all called methods from methods and made edges
	 * @param methods
	 * @return
	 */
	private static List<Edge<MethodModel>> findEdges(CtMethod[] methods) {

		List<Edge<MethodModel>> edges = new ArrayList<>();
		for (CtMethod method : methods) {
			try {
				method.instrument(new ExprEditor() {
					public void edit(MethodCall m) throws CannotCompileException {
						edges.add(makeEdge(method, m));
					}
				});
			} catch (CannotCompileException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		return edges;
	}

	/**
	 * make a oriented edge
	 * @param method which will be the first node of the edge
	 * @param methodCall which will be the second node of the edge
	 * @return a oriented edge
	 */
	private static Edge<MethodModel> makeEdge(CtMethod method, MethodCall methodCall) {
		Node<MethodModel> firstNode = new Node<>(MethodModel.parseMethodModel(method));
		Node<MethodModel> secondNode = new Node<>(MethodModel.parseMethodModel(methodCall));
		Edge<MethodModel> edge = new Edge<>(firstNode, secondNode, true);
		return edge;
	}

	/**
	 * Read a trace file
	 * @param outputFile where the trace file is saved
	 * @return the content of the trace file
	 */
	private String readTrace(String outputFile) {
		StringBuilder builder = new StringBuilder();
		String line = "";

		try {
			BufferedReader reader = new BufferedReader(new FileReader(outputFile));
			try {
				while ((line = reader.readLine()) != null) {
					builder.append(line + '\n');
				}
			} catch (IOException e) {
				return builder.toString();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String content = builder.toString();
		return content;
	}

	public void setProjectModel(ProjectModel projectModel) {
		this.projectModel = projectModel;
	}
	
	
}
