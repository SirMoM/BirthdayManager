/**
 * 
 */
package application.controller;

import application.model.SessionInfos;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Noah Ruben
 *
 */
public class MainController{

	private Stage stage;
	private final SessionInfos sessionInfos;

	public MainController(Stage stage){
		this.stage = stage;
		this.sessionInfos = new SessionInfos();
	}

	public SessionInfos getSessionInfos(){
		return this.sessionInfos;
	}

	public Stage getStage(){
		return this.stage;
	}

	public void gotoNextScene(String fxmlPath, Initializable controller){
		try{
			this.replaceSceneContent(fxmlPath, controller);
		} catch (Exception ex){
			System.out.println(ex.getMessage());
		}
	}

	public void setStage(Stage stage){
		this.stage = stage;
	}

	public void start(){
		try{
			this.replaceSceneContent("/application/view/BirthdaysOverview.fxml", new BirthdaysOverviewController(this));

		} catch (Exception ex){
			System.out.println("Login: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private void replaceSceneContent(String fxmlPath, Initializable controller) throws Exception{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource(fxmlPath));
		loader.setController(controller);
		Parent root = loader.load();
		Scene scene = new Scene(root);
		this.stage.setScene(scene);

		// Show the GUI
		this.stage.show();
	}

}
