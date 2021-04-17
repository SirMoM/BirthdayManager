package application;

import java.util.logging.Logger;

import application.controller.MainController;
import javafx.application.Application;
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
			Logger.getLogger(this.getClass().getName());
		}
	}
}
