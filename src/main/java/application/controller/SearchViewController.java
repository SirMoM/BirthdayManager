/**
 *
 */
package application.controller;

import application.model.Person;
import application.model.PersonManager;
import application.util.BirthdayComparator;
import application.util.LevenshteinDistance;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/** @author Noah Ruben */
public class SearchViewController extends Controller {
    private static final Logger LOG = LogManager.getLogger(SearchViewController.class.getName());
    private final ObservableList<Person> searchResults;
    private final EventHandler<ActionEvent> returnToPreviousView = actionEvent -> SearchViewController.this.getMainController().goToLastScene();

    @FXML
    private Label search_Label;
    @FXML
    private TextField searchText_TextField;
    @FXML
    private Button search_Button;
    @FXML
    private TitledPane advancedSettings_TitledPane;
    @FXML
    private DatePicker dateSearch_DatePicker;
    private final EventHandler<ActionEvent> searchDateEventHandler = event -> {
        if (SearchViewController.this.dateSearch_DatePicker.getValue() == null) {
            return;
        }

        int year = LocalDate.now().getYear();
        getSearchResults().clear();

        for (Person person : PersonManager.getInstance().getPersons()) {
            boolean start = person.getBirthday().withYear(year).isAfter(SearchViewController.this.dateSearch_DatePicker.getValue().minusDays(6));
            boolean end = person.getBirthday().withYear(year).isBefore(SearchViewController.this.dateSearch_DatePicker.getValue().plusDays(6));

            if (start && end) {
                SearchViewController.this.searchResults.add(person);
            }
        }
        getSearchResults().sort(new BirthdayComparator(false));
    };
    @FXML
    private RadioButton enableFuzzy_RadioButton;
    @FXML
    private RadioButton enableRegEx_RadioButton;
    private final EventHandler<Event> searchEventHandler = event -> {
        if (event instanceof KeyEvent) {
            KeyEvent keyEvent = (KeyEvent) event;
            boolean validEvent = keyEvent.getEventType() == KeyEvent.KEY_PRESSED && keyEvent.getCode() == KeyCode.ENTER;
            if (!validEvent) {
                return;
            }
        }

        String searchText = SearchViewController.this.searchText_TextField.getText();
        if (searchText == null || searchText.isEmpty()) {
            return;
        }
        SearchViewController.this.getSearchResults().clear();
        long start;
        long end;
        LOG.info("SearchText: {}", searchText);
        if (enableFuzzy_RadioButton.isSelected()) {
            LOG.info("Fuzzy Search");
            start = System.currentTimeMillis();
            for (Person person : PersonManager.getInstance().getPersons()) {
                String searchString = searchText.toUpperCase();

                boolean surname = person.getSurname() != null && LevenshteinDistance.calculate(person.getSurname().toUpperCase(), searchString) <= 2;
                boolean name = person.getName() != null && LevenshteinDistance.calculate(person.getName().toUpperCase(), searchString) <= 2;
                boolean misc = person.getMisc() != null && LevenshteinDistance.calculate(person.getMisc().toUpperCase(), searchString) <= 2;

                if (surname || name || misc) {
                    getSearchResults().add(person);
                }
            }
            end = System.currentTimeMillis();
        } else if (enableRegEx_RadioButton.isSelected()) {
            LOG.info("RegEx Search");
            start = System.currentTimeMillis();
            for (Person person : PersonManager.getInstance().getPersons()) {
                if (SearchViewController.this.match(person, searchText)) {
                    getSearchResults().add(person);
                }
            }
            end = System.currentTimeMillis();
        } else {
            LOG.info("Normal Search");
            start = System.currentTimeMillis();
            for (Person person : PersonManager.getInstance().getPersons()) {
                if (SearchViewController.this.contains(person, searchText)) {
                    getSearchResults().add(person);
                }
            }
            end = System.currentTimeMillis();
        }

        LOG.info("Started at {} ended at {}", start, end);
        double searchTimeInSek = (end - start) * 1e-3;
        LOG.debug("Searching the data took: {} Seconds.", searchTimeInSek);
    };
    private final EventHandler<ActionEvent> switchFuzzyRegExEventHandler = event -> {
        if (event.getSource() instanceof RadioButton) {
            RadioButton radioButtonSource = (RadioButton) event.getSource();
            if (radioButtonSource.isSelected() && radioButtonSource.getId().equals(enableFuzzy_RadioButton.getId())) {
                SearchViewController.this.enableRegEx_RadioButton.selectedProperty().set(!SearchViewController.this.enableFuzzy_RadioButton.selectedProperty().getValue());
            } else {
                SearchViewController.this.enableFuzzy_RadioButton.selectedProperty().set(!SearchViewController.this.enableRegEx_RadioButton.selectedProperty().getValue());
            }
        }
    };
    @FXML
    private Button dateSearch_Button;
    @FXML
    private ListView<Person> searchResults_ListView;
    final EventHandler<ActionEvent> openBirthday = event -> {
        final ObservableList<Person> selectedItems = SearchViewController.this.searchResults_ListView.getSelectionModel().getSelectedItems();
        if (!selectedItems.isEmpty()) {
            final int indexOf = PersonManager.getInstance().getPersons().indexOf(selectedItems.get(0));
            SearchViewController.this.getMainController().goToEditBirthdayView(indexOf);
        }
    };
    @FXML
    private MenuItem openBirthday_MenuItem;
    @FXML
    private Button openBirthday_Button;
    @FXML
    private Button closeSearch_Button;

    public SearchViewController(MainController mainController) {
        super(mainController);
        searchResults = FXCollections.observableArrayList();
    }

    @FXML
    void showRegExHelp(ActionEvent event) throws IOException, URISyntaxException {
        String url = "https://regexr.com/";
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(url));
        }
    }

    protected boolean contains(final Person person, final String searchText) {
        String searchString = searchText.toUpperCase();
        boolean surname = person.getSurname() != null && searchString.contains(person.getSurname().toUpperCase());
        boolean name = person.getName() != null && searchString.contains(person.getName().toUpperCase());
        boolean misc = person.getMisc() != null && searchString.contains(person.getMisc().toUpperCase());

        return surname || name || misc;
    }

    protected boolean match(final Person person, final String searchText) {
        Pattern pattern = Pattern.compile(searchText.toLowerCase());
        boolean surname = person.getSurname() != null && pattern.matcher(person.getSurname().toLowerCase()).find();
        boolean name = person.getName() != null && pattern.matcher(person.getName().toLowerCase()).find();
        boolean misc = person.getMisc() != null && pattern.matcher(person.getMisc().toLowerCase()).find();

        return surname || name || misc;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.assertion();
        this.updateLocalisation();
        this.bindComponents();
    }

    /** All assertions for the controller. Checks if all FXML-Components have been loaded properly. */
    private void assertion() {
        assert search_Label != null : "fx:id=\"search_Label\" was not injected: check your FXML file 'SearchView.fxml'.";
        assert searchText_TextField != null : "fx:id=\"searchText_TextField\" was not injected: check your FXML file 'SearchView.fxml'.";
        assert search_Button != null : "fx:id=\"search_Button\" was not injected: check your FXML file 'SearchView.fxml'.";
        assert advancedSettings_TitledPane != null : "fx:id=\"advancedSettings_TitledPane\" was not injected: check your FXML file 'SearchView.fxml'.";
        assert dateSearch_DatePicker != null : "fx:id=\"dateSearch_DatePicker\" was not injected: check your FXML file 'SearchView.fxml'.";
        assert enableFuzzy_RadioButton != null : "fx:id=\"enableFuzzy_RadioButton\" was not injected: check your FXML file 'SearchView.fxml'.";
        assert enableRegEx_RadioButton != null : "fx:id=\"enableRegEx_RadioButton\" was not injected: check your FXML file 'SearchView.fxml'.";
        assert dateSearch_Button != null : "fx:id=\"dateSearch_Button\" was not injected: check your FXML file 'SearchView.fxml'.";
        assert searchResults_ListView != null : "fx:id=\"searchResults_ListView\" was not injected: check your FXML file 'SearchView.fxml'.";
        assert openBirthday_MenuItem != null : "fx:id=\"openBirthday_MenuItem\" was not injected: check your FXML file 'SearchView.fxml'.";
        assert openBirthday_Button != null : "fx:id=\"openBirthday_Button\" was not injected: check your FXML file 'SearchView.fxml'.";
        assert closeSearch_Button != null : "fx:id=\"closeSearch_Button\" was not injected: check your FXML file 'SearchView.fxml'.";
    }

    /** Bind EventHandlers and JavaFX-Components */
    private void bindComponents() {
        this.search_Button.addEventHandler(ActionEvent.ANY, this.searchEventHandler);
        this.searchText_TextField.addEventHandler(KeyEvent.KEY_PRESSED, this.searchEventHandler);
        this.searchResults_ListView.setItems(this.searchResults);
        this.openBirthday_MenuItem.addEventHandler(ActionEvent.ANY, this.openBirthday);
        this.enableFuzzy_RadioButton.addEventHandler(ActionEvent.ANY, this.switchFuzzyRegExEventHandler);
        this.enableRegEx_RadioButton.addEventHandler(ActionEvent.ANY, this.switchFuzzyRegExEventHandler);
        this.dateSearch_Button.addEventHandler(ActionEvent.ANY, this.searchDateEventHandler);
        this.openBirthday_Button.addEventHandler(ActionEvent.ANY, this.openBirthday);
        this.closeSearch_Button.addEventHandler(ActionEvent.ANY, this.returnToPreviousView);
    }

    @Override
    public void updateLocalisation() {
        LangResourceManager lrm = new LangResourceManager();
        this.openBirthday_MenuItem.setText(lrm.getLocaleString(LangResourceKeys.openBirthday_MenuItem));
        this.search_Label.setText(lrm.getLocaleString(LangResourceKeys.search));
        this.search_Button.setText(lrm.getLocaleString(LangResourceKeys.search));
        this.enableFuzzy_RadioButton.setText(lrm.getLocaleString(LangResourceKeys.enableFuzzy_RadioButton));
        this.enableRegEx_RadioButton.setText(lrm.getLocaleString(LangResourceKeys.enableRegEx_RadioButton));
        this.dateSearch_Button.setText(lrm.getLocaleString(LangResourceKeys.search));
        this.openBirthday_MenuItem.setText(lrm.getLocaleString(LangResourceKeys.openBirthday_MenuItem));
        this.advancedSettings_TitledPane.setText(lrm.getLocaleString(LangResourceKeys.advancedSettings_TitledPane));
    }

    @Override
    public void placeFocus() {
        searchText_TextField.requestFocus();
    }

    public ObservableList<Person> getSearchResults() {
        return searchResults;
    }
}
