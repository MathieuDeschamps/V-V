package m2.ihm;
import java.io.File;
import java.net.MalformedURLException;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import m2.exceptions.CopyException;
import m2.exceptions.DeleteTmpException;
import m2.exceptions.WrongMaevenProjectException;
import m2.model.Model;
import m2.model.ProjectModel;
import m2.utils.TempUtils;
import m2.vv.code_coverage.TraceFunctionCall;

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
	@FXML
	TextArea trace;
	@FXML
	PieChart pie;
	private Stage stage;
	final DirectoryChooser dirChooser =  new DirectoryChooser();
	private TempUtils tempUtils;
	private ProjectModel projectModel;
	private TraceFunctionCall traceFunction;
	
	
	@FXML
	private void initialize(){
		
		projectModel = new ProjectModel();
		traceFunction = new TraceFunctionCall(projectModel);
		tempUtils = new TempUtils();
		okLabel.setText("");
		koLabel.setText("");
		ignoreLabel.setText("");
		loadButton.setText("Load");
		listPane.setContent(list);
		configureDirChooser( dirChooser );
		
		list.getSelectionModel().selectedItemProperty().addListener( obs -> {
			Model model = projectModel.getModel(list.getSelectionModel().selectedItemProperty().getValue());
			okLabel.setText( ""+model.getnOkTest());
			koLabel.setText( ""+model.getnKoTest() );
			ignoreLabel.setText(""+model.getnIgnoredTest());
			trace.setEditable(true);
			trace.setText( model.getExecutionTrace());
			trace.setEditable(false);
			System.out.println(list.getSelectionModel().selectedItemProperty().getValue());
			//pie = new PieChart();
			pie.getData().clear();
			pie.getData().add( new PieChart.Data("OK", model.getnOkTest()));
			pie.getData().add( new PieChart.Data("KO", model.getnKoTest()));
			pie.getData().add( new PieChart.Data("Ignored", model.getnIgnoredTest()));
			
			for(PieChart.Data data :pie.getData()) {
				switch (data.getName()) {
				case "OK":
					// set color green
					data.getNode().setStyle("-fx-pie-color: #08cf54;");
					break;
				case "KO":
					data.getNode().setStyle("-fx-pie-color: #1c4174;");
					// set color blue
					break;
				case "Ignored":
					// set color red
					data.getNode().setStyle("-fx-pie-color: #9f3f3f;");
					break;
				default:
					break;
				}
			}
		});
		
		loadButton.setOnAction( (event)->{			
						
			File dir = dirChooser.showDialog(stage);
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setHeight(800);
			alert.setWidth(600);
			boolean error = false;
			try {
				tempUtils.copyTarget( dir.getAbsolutePath( ) );
			} catch (WrongMaevenProjectException e) {
				
				alert.setAlertType( AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setContentText("Le dossier selectionné n'est pas un project maeven valide.\n"+e.getMessage( ));
				alert.showAndWait();
				error = true;

			} catch (CopyException e) {
				alert.setAlertType( AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setContentText("Une erreur est survenu lors du chargement du projet.\n"+e.getMessage( ));
				alert.showAndWait();
				error = true;
				
			}
			if( !error )
			{
				try {
					traceFunction.process( tempUtils.getCopyPath( ) );
					list.getItems().clear();
					list.getItems().setAll(projectModel.getclassTestNames());
				} catch (MalformedURLException | ClassNotFoundException e) {
					alert.setAlertType( AlertType.ERROR);
					alert.setContentText("Une erreur est survenue: "+e.getMessage( ) );
				}
			}
		});
	}


	private void configureDirChooser(DirectoryChooser dirChooser) 
	{
		dirChooser.setTitle(DIR_CHOOSER_TITLE);
		dirChooser.setInitialDirectory( new File( System.getProperty("user.home")));
		
	}
	
	public void clean( ) throws DeleteTmpException{
		tempUtils.deleteCopy();
	}

	public void setStage(Stage stage )
	{
		this.stage = stage;
	}
}
