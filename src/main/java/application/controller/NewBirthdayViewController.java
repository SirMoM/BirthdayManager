/** */
package application.controller;

import application.model.Person;
import application.model.PersonManager;
import application.processes.SaveBirthdaysToFileTask;
import application.util.PropertyFields;
import application.util.PropertyManager;
import application.util.localisation.LangResourceKeys;
import application.util.localisation.LangResourceManager;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.apache.logging.log4j.Level;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class NewBirthdayViewController extends Controller {

  private final Person newPerson;
  private final EventHandler<ActionEvent> returnToMainView =
      event -> NewBirthdayViewController.this.getMainController().goToBirthdaysOverview();

  private MenuItem recentFiles_MenuItem;
  @FXML private ResourceBundle resources;
  @FXML private URL location;
  @FXML private Menu file_menu;
  @FXML private MenuItem openFile_MenuItem;
  @FXML private Menu openRecent_MenuItem;
  @FXML private MenuItem closeFile_MenuItem;
  @FXML private MenuItem saveFile_MenuItem;
  @FXML private MenuItem saveAsFile_MenuItem;
  @FXML private MenuItem preferences_MenuItem;
  @FXML private MenuItem quit_MenuItem;
  @FXML private Menu edit_menu;
  @FXML private MenuItem newBirthday_MenuItem;
  @FXML private MenuItem importBirthdays_MenuItem;
  @FXML private MenuItem deleteBirthdays_MenuItem;
  @FXML private Menu help_menu;
  @FXML private MenuItem debug;
  @FXML private MenuItem refresh_MenuItem;
  @FXML private Button cancel_Button;
  @FXML private Button delete_Button;
  @FXML private Button save_Button;
  @FXML private Label identifyingPerson_label;
  @FXML private Label name_Label;
  @FXML private TextField name_TextField;
  @FXML private Label middleName_Label;
  @FXML private TextField middleName_TextField;
  @FXML private Label surname_Label;
  @FXML private TextField surname_TextField;
  @FXML private Label birthday_Label;
  @FXML private DatePicker birthday_DatePicker;

  private final EventHandler<ActionEvent> savePersonHandler =
      actionEvent -> {
        final String nameFromTextField = NewBirthdayViewController.this.name_TextField.getText();
        final String middleNameFromTextField =
            NewBirthdayViewController.this.middleName_TextField.getText();
        final String surnameFromTextField =
            NewBirthdayViewController.this.surname_TextField.getText();
        final LocalDate birthdayFromDatePicker =
            NewBirthdayViewController.this.birthday_DatePicker.getValue();

        if (nameFromTextField != null) {
          NewBirthdayViewController.this.newPerson.setName(nameFromTextField);
        }
        if (middleNameFromTextField != null) {
          NewBirthdayViewController.this.newPerson.setMisc(middleNameFromTextField);
        }
        if (surnameFromTextField != null) {
          NewBirthdayViewController.this.newPerson.setSurname(surnameFromTextField);
        }
        if (birthdayFromDatePicker != null) {
          NewBirthdayViewController.this.newPerson.setBirthday(birthdayFromDatePicker);
        }

        if (birthdayFromDatePicker == null
            || surnameFromTextField.isEmpty()
            || nameFromTextField.isEmpty()) {
          final LangResourceManager langResourceManager = new LangResourceManager();
          final Alert alert = new Alert(AlertType.WARNING);
          alert.setTitle(
              langResourceManager.getLocaleString(LangResourceKeys.person_not_valid_warning));
          alert.setHeaderText(
              langResourceManager.getLocaleString(LangResourceKeys.person_not_valid_warning));
          alert.showAndWait();
          return;
        }
        PersonManager.getInstance().addNewPerson(NewBirthdayViewController.this.newPerson);
        NewBirthdayViewController.this.getMainController().goToBirthdaysOverview();

        if (Boolean.parseBoolean(PropertyManager.getProperty(PropertyFields.WRITE_THRU))
            && (getMainController().getSessionInfos().getSaveFile() != null)) {
          SaveBirthdaysToFileTask saveBirthdaysToFileTask =
              new SaveBirthdaysToFileTask(getMainController().getSessionInfos().getSaveFile());
          saveBirthdaysToFileTask.setOnSucceeded(
              event -> {
                if (event.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
                  LOG.debug("Saved changes to file (via write thru)");
                }
              });

          new Thread(saveBirthdaysToFileTask).start();
        }
      };
  @FXML private Label openedFile_label;
  @FXML private Font x3;
  @FXML private Color x4;
  @FXML private Label date_label;

  public NewBirthdayViewController(final MainController mainController) {
    super(mainController);
    this.newPerson = new Person();
  }

  /** All assertions for the controller. Checks if all FXML-Components have been loaded properly. */
  private void assertions() {
    assert this.file_menu != null
        : "fx:id=\"file_menu\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.openFile_MenuItem != null
        : "fx:id=\"openFile_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.openRecent_MenuItem != null
        : "fx:id=\"openRecent_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.closeFile_MenuItem != null
        : "fx:id=\"closeFile_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.saveFile_MenuItem != null
        : "fx:id=\"saveFile_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.saveAsFile_MenuItem != null
        : "fx:id=\"saveAsFile_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.preferences_MenuItem != null
        : "fx:id=\"preferences_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.quit_MenuItem != null
        : "fx:id=\"quit_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.edit_menu != null
        : "fx:id=\"edit_menu\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.newBirthday_MenuItem != null
        : "fx:id=\"newBirthday_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.importBirthdays_MenuItem != null
        : "fx:id=\"importBirthdays_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.deleteBirthdays_MenuItem != null
        : "fx:id=\"deleteBirthdays_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.help_menu != null
        : "fx:id=\"help_menu\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.debug != null
        : "fx:id=\"debug\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.refresh_MenuItem != null
        : "fx:id=\"refresh_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.cancel_Button != null
        : "fx:id=\"cancel_Button\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.delete_Button != null
        : "fx:id=\"delete_Button\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.save_Button != null
        : "fx:id=\"save_Button\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.identifyingPerson_label != null
        : "fx:id=\"identifyingPerson_label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.name_Label != null
        : "fx:id=\"name_Label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.name_TextField != null
        : "fx:id=\"name_TextField\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.middleName_Label != null
        : "fx:id=\"middleName_Label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.middleName_TextField != null
        : "fx:id=\"middleName_TextField\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.surname_Label != null
        : "fx:id=\"surname_Label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.surname_TextField != null
        : "fx:id=\"surname_TextField\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.birthday_Label != null
        : "fx:id=\"birthday_Label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.birthday_DatePicker != null
        : "fx:id=\"birthday_DatePicker\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.openedFile_label != null
        : "fx:id=\"openedFile_label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.x3 != null
        : "fx:id=\"x3\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.x4 != null
        : "fx:id=\"x4\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
    assert this.date_label != null
        : "fx:id=\"date_label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
  }

  /** Binds the JavaFX Components to their {@link EventHandler}. */
  private void bindComponents() {
    this.cancel_Button.addEventHandler(ActionEvent.ANY, this.returnToMainView);
    this.save_Button.addEventHandler(ActionEvent.ANY, this.savePersonHandler);
    this.delete_Button.setVisible(false);

    this.quit_MenuItem.addEventHandler(ActionEvent.ANY, this.getMainController().closeAppHandler);
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
    this.openFile_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.openFile_MenuItem));
    this.openRecent_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.openRecent_MenuItem));
    this.closeFile_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.closeFile_MenuItem));
    this.saveFile_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.saveFile_MenuItem));
    this.saveAsFile_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.saveAsFile_MenuItem));
    this.preferences_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.preferences_MenuItem));
    this.quit_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.quit_MenuItem));

    this.edit_menu.setText(resourceManager.getLocaleString(LangResourceKeys.edit_menu));
    this.newBirthday_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.newBirthday_MenuItem));
    this.importBirthdays_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.importBirthdays_MenuItem));
    this.deleteBirthdays_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.deleteBirthdays_MenuItem));

    this.help_menu.setText(resourceManager.getLocaleString(LangResourceKeys.help_menu));

    this.identifyingPerson_label.setText(
        resourceManager.getLocaleString(LangResourceKeys.identifyingPerson_label));
    this.name_Label.setText(resourceManager.getLocaleString(LangResourceKeys.name_Label));
    this.middleName_Label.setText(
        resourceManager.getLocaleString(LangResourceKeys.middleName_Label));
    this.surname_Label.setText(resourceManager.getLocaleString(LangResourceKeys.surname_Label));
    this.birthday_Label.setText(resourceManager.getLocaleString(LangResourceKeys.birthday_Label));

    this.openFile_MenuItem.addEventHandler(
        ActionEvent.ANY, this.getMainController().openFromFileChooserHandler);
    try {
      this.recentFiles_MenuItem =
          new MenuItem(new File(PropertyManager.getProperty(PropertyFields.LAST_OPEND)).getName());
      this.recentFiles_MenuItem.addEventHandler(
          ActionEvent.ANY, this.getMainController().openFromRecentHandler);
      this.openRecent_MenuItem.getItems().add(this.recentFiles_MenuItem);
    } catch (NullPointerException nullPointerException) {
      LOG.catching(Level.INFO, nullPointerException);
      LOG.info("No recent File opend ?");
    }
  }
}
