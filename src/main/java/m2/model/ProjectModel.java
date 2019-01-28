package m2.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author DESCHAMPS Mathieu && ESNAULT Jeremie
 *
 */
public class ProjectModel {
	
	 private Map<String, Model> classTestMap = new HashMap<>();
	 private List<String> classes = new ArrayList<>();
	 
	 public void addModel( Model model )
	 {
		 classTestMap.put( model.getFileName( ), model);
	 }
	 
	 public Model getModel( String fileName )
	 {
		 return classTestMap.get( fileName );
	 }
	

}
