/**
 * 
 */
package application.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.swing.text.StyledEditorKit.BoldAction;

import application.model.Person;
import application.model.PersonManager;
import application.util.BirthdayComparator;
import application.util.LevenshteinDistanz;
import application.util.localisation.LangResourceKeys;
import application.util.localisation.LangResourceManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
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
	private DatePicker dateSearch_DatePicker;

	@FXML
	private RadioButton enableFuzzy_RadioButton;

	@FXML
	private RadioButton enableRegEx_RadioButton;

	@FXML
	private Button dateSearch_Button;

	@FXML
	private ListView<Person> searchResults_ListView;

	@FXML
	private MenuItem openBirthday_MenuItem;

	private EventHandler<Event> searchEventHandler = new EventHandler<Event>() {

		@Override
		public void handle(Event event) {
			if (event instanceof KeyEvent) {
				KeyEvent keyEvent = (KeyEvent) event;
				boolean validEvent = keyEvent.getEventType() == KeyEvent.KEY_PRESSED
						&& keyEvent.getCode() == KeyCode.ENTER;
				if (!validEvent) {
					return;
				}
			}

			String searchText = SearchViewController.this.searchText_TextField.getText();
			if (searchText.isEmpty() || searchText == null) {
				return;
			}
			searchResults.clear();
			long start;
			long end;
			LOG.info("SearchText: " + searchText);
			if (enableFuzzy_RadioButton.isSelected()) {
				LOG.info("Fuzzy Search");
				start = System.currentTimeMillis();
				for (Person person : PersonManager.getInstance().getPersons()) {
					String searchString = searchText.toUpperCase();
					boolean surname = person.getSurname()	!= null && LevenshteinDistanz.calculate(person.getSurname().toUpperCase(), searchString) <= 2;
					boolean name = person.getName() 		!= null && LevenshteinDistanz.calculate(person.getName().toUpperCase(), searchString) <= 2;
					boolean misc = person.getMisc()			!= null && LevenshteinDistanz.calculate(person.getMisc().toUpperCase(), searchString) <= 2;

					if (surname || name || misc) {
						searchResults.add(person);
					}
				}
				end = System.currentTimeMillis();
			} else if (enableRegEx_RadioButton.isSelected()) {
				LOG.info("RegEx Search");
				start = System.currentTimeMillis();
				for (Person person : PersonManager.getInstance().getPersons()) {
					if (SearchViewController.this.match(person, searchText)) {
						searchResults.add(person);
					}
				}
				end = System.currentTimeMillis();
			} else {
				LOG.info("Normal Search");
				start = System.currentTimeMillis();
				for (Person person : PersonManager.getInstance().getPersons()) {
					if (SearchViewController.this.contains(person, searchText)) {
						searchResults.add(person);
					}
				}
				end = System.currentTimeMillis();
			}

			SearchViewController.this.LOG.info(start);
			SearchViewController.this.LOG.info(end);
			SearchViewController.this.LOG.info(end - start);
			double x = (end - start) * 1e-3;
			SearchViewController.this.LOG.debug("Searching the data took: " + x + " Seconds.");

		}};

	final EventHandler<ActionEvent> openBirthday=new EventHandler<ActionEvent>(){@Override public void handle(final ActionEvent arg0){final ObservableList<Person>selectedItems=SearchViewController.this.searchResults_ListView.getSelectionModel().getSelectedItems();if(selectedItems.isEmpty()){return;}else{final int indexOf=PersonManager.getInstance().getPersons().indexOf(selectedItems.get(0));SearchViewController.this.getMainController().goToEditBirthdayView(indexOf);}}};

	private EventHandler<ActionEvent> switchFuzzyRegExEventHandler=new EventHandler<ActionEvent>(){

	@Override public void handle(ActionEvent event){if(event.getSource()instanceof RadioButton){RadioButton radioButtonSource=(RadioButton)event.getSource();if(!radioButtonSource.isSelected()){return;}if(radioButtonSource.getId()==enableFuzzy_RadioButton.getId()){SearchViewController.this.enableRegEx_RadioButton.selectedProperty().set(!SearchViewController.this.enableFuzzy_RadioButton.selectedProperty().getValue());}else{SearchViewController.this.enableFuzzy_RadioButton.selectedProperty().set(!SearchViewController.this.enableRegEx_RadioButton.selectedProperty().getValue());}}}};

	private EventHandler<ActionEvent> searchDateEventHandler = new EventHandler<ActionEvent>() {
		
		@Override
		public void handle(ActionEvent event) {
			
			if (SearchViewController.this.dateSearch_DatePicker.getValue() == null) {
				return;
			}
			
			int year = LocalDate.now().getYear();
			SearchViewController.this.searchResults.clear();
			
			for (Person person : PersonManager.getInstance().getPersons()) {
//				if (person.getBirthday().withYear(year).isEqual(SearchViewController.this.dateSearch_DatePicker.getValue())) {
//					SearchViewController.this.searchResults.add(person);
//				}
				boolean start = person.getBirthday().withYear(year).isAfter(SearchViewController.this.dateSearch_DatePicker.getValue().minusDays(6));
				boolean end = person.getBirthday().withYear(year).isBefore(SearchViewController.this.dateSearch_DatePicker.getValue().plusDays(6));
				
				if (start && end) {
					SearchViewController.this.searchResults.add(person);
				}
			}
			SearchViewController.this.searchResults.sort(new BirthdayComparator(false));
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
	protected boolean contains(final Person person, final String searchText) {
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

	/**
	 * TODO DOCU
	 * 
	 * @param person
	 * @param searchText
	 * @return
	 */
	protected boolean match(final Person person, final String searchText) {
		Pattern pattern = Pattern.compile(searchText.toLowerCase());
		boolean surname = person.getSurname() != null && pattern.matcher(person.getSurname().toLowerCase()).find();
		boolean name = person.getName() != null && pattern.matcher(person.getName().toLowerCase()).find();
		boolean misc = person.getMisc() != null && pattern.matcher(person.getMisc().toLowerCase()).find();

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
		assert dateSearch_DatePicker != null : "fx:id=\"dateSearch_DatePicker\" was not injected: check your FXML file 'SearchView.fxml'.";
		assert enableFuzzy_RadioButton != null : "fx:id=\"enableFuzzy_RadioButton\" was not injected: check your FXML file 'SearchView.fxml'.";
		assert enableRegEx_RadioButton != null : "fx:id=\"enableRegEx_RadioButton\" was not injected: check your FXML file 'SearchView.fxml'.";
		assert dateSearch_Button != null : "fx:id=\"dateSearch_Button\" was not injected: check your FXML file 'SearchView.fxml'.";
		assert searchResults_ListView != null : "fx:id=\"searchResults_ListView\" was not injected: check your FXML file 'SearchView.fxml'.";
		assert openBirthday_MenuItem != null : "fx:id=\"openBirthday_MenuItem\" was not injected: check your FXML file 'SearchView.fxml'.";
	}

	/**
	 * Bind EventHandlers and JavaFX-Components
	 */
	private void bindComponents() {
		this.search_Button.addEventHandler(ActionEvent.ANY, this.searchEventHandler);
		this.searchText_TextField.addEventHandler(KeyEvent.KEY_PRESSED, this.searchEventHandler);
		this.searchResults_ListView.setItems(this.searchResults);
		this.openBirthday_MenuItem.addEventHandler(ActionEvent.ANY, this.openBirthday);

		this.enableFuzzy_RadioButton.addEventHandler(ActionEvent.ANY, this.switchFuzzyRegExEventHandler);
		this.enableRegEx_RadioButton.addEventHandler(ActionEvent.ANY, this.switchFuzzyRegExEventHandler);

		this.dateSearch_Button.addEventHandler(ActionEvent.ANY, this.searchDateEventHandler);
	}

	@Override
	public void updateLocalisation() {
		this.openBirthday_MenuItem
				.setText((new LangResourceManager()).getLocaleString(LangResourceKeys.openBirthday_MenuItem));
	}

}
