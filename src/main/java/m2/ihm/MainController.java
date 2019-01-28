package m2.ihm;
import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import m2.exceptions.CopyException;
import m2.exceptions.WrongMaevenProjectException;
import m2.utils.TempUtils;

/**
 * 
 * @author DESCHAMPS Mathieu && ESNAULT Jeremie
 *
 */
public class MainController {
	
	private final String DIR_CHOOSER_TITLE = "Choose a maeven project directory";
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
	ScrollPane listPane;
	@FXML
	ListView<String> list;
	private Stage stage;
	final DirectoryChooser dirChooser =  new DirectoryChooser();
	private TempUtils tempUtils;
	
	
	@FXML
	private void initialize(){
		
		tempUtils = new TempUtils();
		okLabel.setText("");
		koLabel.setText("");
		ignoreLabel.setText("");
		loadButton.setText("Load");
		listPane.setContent(list);
		configureDirChooser( dirChooser );
		loadButton.setOnAction( (event)->{			
						
			File dir = dirChooser.showDialog(stage);
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setHeight(800);
			alert.setWidth(600);
			try {
				tempUtils.copyTarget( dir.getAbsolutePath( ) );
			} catch (WrongMaevenProjectException e) {
				
				alert.setAlertType( AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setContentText("Le dossier selectionné n'est pas un project maeven valide.\n"+e.getMessage( ));
				alert.showAndWait();

			} catch (CopyException e) {
				alert.setAlertType( AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setContentText("Une erreur est survenu lors du chargement du projet.\n"+e.getMessage( ));
				alert.showAndWait();
				
			}
		});
	}


	private void configureDirChooser(DirectoryChooser dirChooser) 
	{
		dirChooser.setTitle(DIR_CHOOSER_TITLE);
		dirChooser.setInitialDirectory( new File( System.getProperty("user.home")));
		
	}

	public void setStage(Stage stage )
	{
		this.stage = stage;
	}
}
