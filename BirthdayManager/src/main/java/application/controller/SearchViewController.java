/**
 * 
 */
package application.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.text.StyledEditorKit.BoldAction;

import application.model.Person;
import application.model.PersonManager;
import application.util.localisation.LangResourceKeys;
import application.util.localisation.LangResourceManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * @author Noah Ruben
 *
 */
public class SearchViewController extends Controller {
	

	private ObservableList<Person> searchResults;

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
	
	@FXML
	private MenuItem openBirthday_MenuItem;

	private EventHandler<Event> searchEventHandler = new EventHandler<Event>() {

		@Override
		public void handle(Event event) {
			if (event instanceof KeyEvent) {
				KeyEvent keyEvent = (KeyEvent) event;
				boolean validEvent = keyEvent.getEventType() == KeyEvent.KEY_PRESSED && keyEvent.getCode() == KeyCode.ENTER;
				if (!validEvent) {
					return;
				}
			}

			String searchText = SearchViewController.this.searchText_TextField.getText();
			if (searchText.isEmpty() || searchText == null) {
				return;
			}
			searchResults.clear();
			long start = System.currentTimeMillis();
			for (Person person : PersonManager.getInstance().getPersons()) {
				if (SearchViewController.this.match(person, searchText)) {
					searchResults.add(person);
				}
			}
			long end = System.currentTimeMillis();
			SearchViewController.this.LOG.info(start);
			SearchViewController.this.LOG.info(end);
			SearchViewController.this.LOG.info(end - start);
			double x = (end - start) * 1e-3;
			SearchViewController.this.LOG.debug("Searching the data took: " + x + " Seconds.");

		}
	};


	final EventHandler<ActionEvent> openBirthday = new EventHandler<ActionEvent>() {
		@Override
		public void handle(final ActionEvent arg0) {
			final ObservableList<Person> selectedItems = SearchViewController.this.searchResults_ListView.getSelectionModel().getSelectedItems();
			if (selectedItems.isEmpty()) {
				return;
			} else {
				final int indexOf = PersonManager.getInstance().getPersons().indexOf(selectedItems.get(0));
				SearchViewController.this.getMainController().goToEditBirthdayView(indexOf);
			}
		}
	};
	
	public SearchViewController(MainController mainController) {
		super(mainController);
		searchResults = FXCollections.observableArrayList();
	}

	/**
	 * TODO DOCU
	 * 
	 * @param person
	 * @param searchText
	 * @return
	 */
	protected boolean match(final Person person, final String searchText) {
		String searchString = searchText.toUpperCase();
		boolean surname = person.getSurname() != null && searchString.contains(person.getSurname().toUpperCase());
		boolean name = person.getName() != null && searchString.contains(person.getName().toUpperCase());
		boolean misc = person.getMisc() != null && searchString.contains(person.getMisc().toUpperCase());

		if (surname || name || misc) {
			return true;
		} else {
			return false;
		}

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
		assert openBirthday_MenuItem != null : "fx:id=\"openBirthday_MenuItem\" was not injected: check your FXML file 'SearchView.fxml'.";
	}

	/**
	 * Bind EventHandlers and JavaFX-Components
	 */
	private void bindComponents() {
		search_Button.addEventHandler(ActionEvent.ANY, this.searchEventHandler);
		searchText_TextField.addEventHandler(KeyEvent.KEY_PRESSED, this.searchEventHandler);
		searchResults_ListView.setItems(this.searchResults);
		this.openBirthday_MenuItem.addEventHandler(ActionEvent.ANY, this.openBirthday);
	}

	@Override
	public void updateLocalisation() {
		this.openBirthday_MenuItem.setText((new LangResourceManager()).getLocaleString(LangResourceKeys.openBirthday_MenuItem));
	}

}
