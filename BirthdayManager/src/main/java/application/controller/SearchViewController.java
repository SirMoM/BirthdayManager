/**
 * 
 */
package application.controller;

import java.net.URL;
import java.util.ResourceBundle;

import application.model.Person;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

/**
 * @author Noah Ruben
 *
 *
 * @created 10.01.2020
 */
public class SearchViewController extends Controller {

	
	@FXML
    private Label search_Label;

    @FXML
    private TextField searchText_TextField;

    @FXML
    private Button search_Button;

    @FXML
    private TitledPane advancedSettings_Accordion;

    @FXML
    private ListView<Person> searchResults_ListView;

	
	/**
	 * 
	 */
	public SearchViewController(MainController mainController) {
		super(mainController);
		
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.assertion();
		this.updateLocalisation();
		this.bindComponents();
	}
	
	/**
	 * All assertions for the controller. Checks if all FXML-Components have been
	 * loaded properly.
	 */
	private void assertion() {
		assert search_Label != null : "fx:id=\"search_Label\" was not injected: check your FXML file 'SearchView.fxml'.";
		assert searchText_TextField != null : "fx:id=\"searchText_TextField\" was not injected: check your FXML file 'SearchView.fxml'.";
		assert search_Button != null : "fx:id=\"search_Button\" was not injected: check your FXML file 'SearchView.fxml'.";
		assert advancedSettings_Accordion != null : "fx:id=\"advancedSettings_Accordion\" was not injected: check your FXML file 'SearchView.fxml'.";
		assert searchResults_ListView != null : "fx:id=\"searchResults_ListView\" was not injected: check your FXML file 'SearchView.fxml'.";
	}
	
	/**
	 * Bind EventHandlers and JavaFX-Components
	 */
	private void bindComponents() {
		searchResults_ListView.setItems(getMainController().getSessionInfos().getNextBirthdays());
	
	}
	
	@Override
	public void updateLocalisation() {
	}

}
