package application;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import application.controller.MainController;
import application.model.PersonManager;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AppStart extends Application{

	public static void main(final String[] args){
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception{
		try{
			final MainController mainController = new MainController(stage);
			mainController.start();
		} catch (final Exception e){
			LogManager.getLogger(PersonManager.class).catching(Level.FATAL, e);
			final Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("ERROR");
			alert.setHeaderText("Someting went wrong! \n Consider sending me the log.");

			final StringBuilder stringBuilder = new StringBuilder("Stacktrace: \n");
			for(int i = 0; i < e.getStackTrace().length; i++){
				stringBuilder.append(e.getStackTrace()[i] + "\n");
			}
			final TextArea textArea = new TextArea(stringBuilder.toString());
			textArea.setEditable(false);
			textArea.setWrapText(true);
			final GridPane gridPane = new GridPane();
			gridPane.setMaxWidth(Double.MAX_VALUE);
			gridPane.add(textArea, 0, 0);
			alert.getDialogPane().setContent(gridPane);
			alert.showAndWait();
		}
	}
}
