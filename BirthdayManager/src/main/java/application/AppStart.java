package application;

import application.controller.MainController;
import javafx.application.Application;
import javafx.stage.Stage;

public class AppStart extends Application{

	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception{
		MainController mainController = new MainController(stage);
		mainController.start();
	}
}
