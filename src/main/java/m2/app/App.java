package m2.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import m2.exceptions.DeleteTmpException;
import m2.ihm.MainController;

public class App  extends Application{

	MainController controller;
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../ihm/MainIhm.fxml"));
			Parent root = (Parent) loader.load();
			Scene scene = new Scene(root,800,600);
			controller = (MainController)loader.getController( );
			controller.setStage(primaryStage);
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.setTitle("vv Project");
			primaryStage.show();		
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop(){
		try {
			controller.clean();
		} catch (DeleteTmpException e) {
			// TODO Auto-generated catch block
			System.out.println("Problème lors du clean");
		}
	}
	public static void main(String[] args) {
		launch(args);
	}
}
