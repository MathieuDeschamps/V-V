package m2.ihm;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * 
 * @author DESCHAMPS Mathieu && ESNAULT Jeremie
 *
 */
public class MainController {
	
	@FXML
	Button loadButton;
	@FXML
	Label okLabel;
	@FXML 
	Label koLabel;
	@FXML
	Label ignoreLabel;
	@FXML
	TextField path;
	
	
	@FXML
	private void initialize(){
		
		okLabel.setText("");
		koLabel.setText("");
		ignoreLabel.setText("");
		loadButton.setText("Load");
		
		loadButton.setOnAction( (event)->{			
						
		});
	}

}
