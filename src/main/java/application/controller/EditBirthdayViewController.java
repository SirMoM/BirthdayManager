/**
 *
 */
package application.controller;

import application.model.Person;
import application.model.PersonManager;
import application.processes.SaveBirthdaysToFileTask;
import application.util.PropertyFields;
import application.util.PropertyManager;
import application.util.localisation.LangResourceKeys;
import application.util.localisation.LangResourceManager;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class EditBirthdayViewController extends Controller {
    private static final Logger LOG = LogManager.getLogger(EditBirthdayViewController.class.getName());

    private final Person personToEdit;
    private final EventHandler<ActionEvent> exitHandler = event -> EditBirthdayViewController.this.getMainController().goToLastScene();
    private final EventHandler<ActionEvent> deletePersonHandler = event -> {
        PersonManager.getInstance().deletePerson(EditBirthdayViewController.this.personToEdit);
        EditBirthdayViewController.this.getMainController().goToLastScene();
    };
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Menu file_menu;
    @FXML
    private MenuItem openFile_MenuItem;
    @FXML
    private Menu openRecent_MenuItem;
    @FXML
    private MenuItem closeFile_MenuItem;
    @FXML
    private MenuItem saveFile_MenuItem;
    @FXML
    private MenuItem saveAsFile_MenuItem;
    @FXML
    private MenuItem preferences_MenuItem;
    @FXML
    private MenuItem quit_MenuItem;
    @FXML
    private Menu edit_menu;
    @FXML
    private MenuItem newBirthday_MenuItem;
    @FXML
    private MenuItem importBirthdays_MenuItem;
    @FXML
    private MenuItem deleteBirthdays_MenuItem;
    @FXML
    private Menu help_menu;
    @FXML
    private MenuItem debug;
    @FXML
    private MenuItem refresh_MenuItem;
    @FXML
    private Button cancel_Button;
    @FXML
    private Button delete_Button;
    @FXML
    private Button save_Button;
    @FXML
    private Label identifyingPerson_label;
    @FXML
    private Label name_Label;
    @FXML
    private TextField name_TextField;
    @FXML
    private Label middleName_Label;
    @FXML
    private TextField middleName_TextField;
    @FXML
    private Label surname_Label;
    @FXML
    private TextField surname_TextField;
    @FXML
    private Label birthday_Label;
    @FXML
    private DatePicker birthday_DatePicker;
    @FXML
    private Label openedFile_label;
    @FXML
    private Font x3;
    @FXML
    private Color x4;
    @FXML
    private Label date_label;
    private int indexPerson = -1;

    private final EventHandler<ActionEvent> savePersonHandler = actionEvent -> {
        boolean anyChangeAtAll = false;
        final String nameFromTextField = EditBirthdayViewController.this.name_TextField.getText();
        final String middleNameFromTextField = EditBirthdayViewController.this.middleName_TextField.getText();
        final String surnameFromTextfield = EditBirthdayViewController.this.surname_TextField.getText();
        final LocalDate birthdayFromDatePicker = EditBirthdayViewController.this.birthday_DatePicker.getValue();

        try {
            if (!nameFromTextField.matches(EditBirthdayViewController.this.personToEdit.getName())) {
                anyChangeAtAll = true;
            }
            if (!middleNameFromTextField.matches(EditBirthdayViewController.this.personToEdit.getMisc())) {
                anyChangeAtAll = true;
            }
            if (!surnameFromTextfield.matches(EditBirthdayViewController.this.personToEdit.getSurname())) {
                anyChangeAtAll = true;
            }
            if (EditBirthdayViewController.this.birthday_DatePicker.getValue() != EditBirthdayViewController.this.personToEdit.getBirthday()) {
                anyChangeAtAll = true;
            }
        } catch (final NullPointerException exception) {
            LOG.catching(Level.INFO, exception);
            if (EditBirthdayViewController.this.personToEdit != null) {
                LOG.info(EditBirthdayViewController.this.personToEdit.toExtendedString());
            }
            anyChangeAtAll = true;
        }
        if (anyChangeAtAll) {
            if (birthdayFromDatePicker == null || surnameFromTextfield.isEmpty() || nameFromTextField.isEmpty()) {
                final LangResourceManager langResourceManager = new LangResourceManager();
                final Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle(langResourceManager.getLocaleString(LangResourceKeys.person_not_valid_warning));
                alert.setHeaderText(langResourceManager.getLocaleString(LangResourceKeys.person_not_valid_warning));
                alert.showAndWait();
                return;
            }
            final Person updatedPerson = new Person(surnameFromTextfield, nameFromTextField, middleNameFromTextField, birthdayFromDatePicker);
            PersonManager.getInstance().updatePerson(EditBirthdayViewController.this.indexPerson, updatedPerson);
            EditBirthdayViewController.this.getMainController().goToLastScene();

        }
        if (Boolean.parseBoolean(PropertyManager.getProperty(PropertyFields.WRITE_THRU))) {
            SaveBirthdaysToFileTask task = new SaveBirthdaysToFileTask(getMainController().getSessionInfos().getSaveFile());
            task.setOnSucceeded(event -> {
                if (event.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
                    LOG.debug("Saved changes to file (via write thru)");
                }
            });
            new Thread(task).start();
        }
    };

    /**
     * @param mainController The "MainController" of this application.
     * @param indexPerson the index of Person to edit.
     * @see application.controller.Controller#Controller(MainController)
     */
    public EditBirthdayViewController(final MainController mainController, final int indexPerson) {
        super(mainController);
        this.personToEdit = PersonManager.getInstance().getPersonFromIndex(indexPerson);
        this.indexPerson = indexPerson;
    }

    /** All assertions for the controller. Checks if all FXML-Components have been loaded properly. */
    private void assertions() {
        assert file_menu != null : "fx:id=\"file_menu\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert openFile_MenuItem != null : "fx:id=\"openFile_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert openRecent_MenuItem != null : "fx:id=\"openRecent_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert preferences_MenuItem != null : "fx:id=\"preferences_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert quit_MenuItem != null : "fx:id=\"quit_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert cancel_Button != null : "fx:id=\"cancel_Button\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert delete_Button != null : "fx:id=\"delete_Button\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert save_Button != null : "fx:id=\"save_Button\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert identifyingPerson_label != null : "fx:id=\"identifyingPerson_label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert name_Label != null : "fx:id=\"name_Label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert name_TextField != null : "fx:id=\"name_TextField\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert middleName_Label != null : "fx:id=\"middleName_Label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert middleName_TextField != null : "fx:id=\"middleName_TextField\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert surname_Label != null : "fx:id=\"surname_Label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert surname_TextField != null : "fx:id=\"surname_TextField\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert birthday_Label != null : "fx:id=\"birthday_Label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert birthday_DatePicker != null : "fx:id=\"birthday_DatePicker\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert openedFile_label != null : "fx:id=\"openedFile_label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert x3 != null : "fx:id=\"x3\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert x4 != null : "fx:id=\"x4\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
        assert date_label != null : "fx:id=\"date_label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    }

    /** Binds the JavaFX Components to their {@link EventHandler}. */
    private void bindComponents() {
        this.cancel_Button.addEventHandler(ActionEvent.ANY, this.exitHandler);
        this.save_Button.addEventHandler(ActionEvent.ANY, this.savePersonHandler);
        this.delete_Button.addEventHandler(ActionEvent.ANY, this.deletePersonHandler);
        this.quit_MenuItem.addEventHandler(ActionEvent.ANY, this.getMainController().closeAppHandler);
    }

    /** @return the personToEdit */
    public Person getPersonToEdit() {
        return this.personToEdit;
    }

    /*
     * (non-Javadoc)
     *
     * @see javafx.fxml.Initializable#initialize(java.net.URL,
     * java.util.ResourceBundle)
     */
    @Override
    public void initialize(final URL arg0, final ResourceBundle arg1) {
        getMainController().getStage().setWidth(1200);

        this.assertions();
        this.updateLocalisation();
        this.bindComponents();
        this.loadPerson();
    }

    /** Loads the person data in the view. */
    private void loadPerson() {
        this.name_TextField.setText(this.personToEdit.getName());
        this.surname_TextField.setText(this.personToEdit.getSurname());
        this.middleName_TextField.setText(this.personToEdit.getMisc());
        try {
            this.birthday_DatePicker.setValue(this.personToEdit.getBirthday());
        } catch (final NullPointerException nullPointerException) {
            LOG.catching(Level.DEBUG, nullPointerException);
            LOG.debug("New Person creating ? IF not we have a problem!");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see application.controller.Controller#updateLocalisation()
     */
    @Override
    public void updateLocalisation() {
        final LangResourceManager resourceManager = new LangResourceManager();

        this.file_menu.setText(resourceManager.getLocaleString(LangResourceKeys.file_menu));
        this.openFile_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.openFile_MenuItem));
        this.openRecent_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.openRecent_MenuItem));
        this.preferences_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.preferences_MenuItem));
        this.quit_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.quit_MenuItem));

        this.identifyingPerson_label.setText(resourceManager.getLocaleString(LangResourceKeys.identifyingPerson_label));
        this.name_Label.setText(resourceManager.getLocaleString(LangResourceKeys.name_Label));
        this.middleName_Label.setText(resourceManager.getLocaleString(LangResourceKeys.middleName_Label));
        this.surname_Label.setText(resourceManager.getLocaleString(LangResourceKeys.surname_Label));
        this.birthday_Label.setText(resourceManager.getLocaleString(LangResourceKeys.birthday_Label));

        this.openFile_MenuItem.addEventHandler(ActionEvent.ANY, this.getMainController().openFromFileChooserHandler);

        this.save_Button.setText(resourceManager.getLocaleString(LangResourceKeys.save_button));
        this.cancel_Button.setText(resourceManager.getLocaleString(LangResourceKeys.cancel_button));
        this.delete_Button.setText(resourceManager.getLocaleString(LangResourceKeys.delete_button));
    }

    @Override
    public void placeFocus() {
        name_TextField.requestFocus();
        this.name_TextField.setFocusTraversable(true);
        this.middleName_TextField.setFocusTraversable(true);
        this.surname_TextField.setFocusTraversable(true);
    }
}
