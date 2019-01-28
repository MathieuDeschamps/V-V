package m2.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import m2.ihm.MainController;

public class App  extends Application{

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../ihm/MainIhm.fxml"));
			Parent root = (Parent) loader.load();
			Scene scene = new Scene(root,800,600);
			MainController controller = (MainController)loader.getController( );
			controller.setStage(primaryStage);
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.setTitle("vv Project");
			primaryStage.show();		
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
