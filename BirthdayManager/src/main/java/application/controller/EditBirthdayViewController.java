/**
 *
 */
package application.controller;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.model.Person;
import application.model.PersonManager;
import application.processes.UpdateAllSubBirthdayListsTask;
import application.util.PropertieFields;
import application.util.localisation.LangResourceKeys;
import application.util.localisation.LangResourceManager;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class EditBirthdayViewController extends Controller{
	final static Logger LOG = LogManager.getLogger();

	final private Person personToEdit;

	private MenuItem recentFiles_MenuItem;

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
	private MenuItem exporteFile_MenuItem;

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
	private MenuItem changeLanguage_MenuItem;

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

	private final EventHandler<ActionEvent> savePersonHandler = new EventHandler<ActionEvent>(){

		@Override
		public void handle(final ActionEvent actionEvent){
			boolean anyChangeAtAll = false;
			final String nameFromTextField = EditBirthdayViewController.this.name_TextField.getText();
			final String middleNameFromTextField = EditBirthdayViewController.this.middleName_TextField.getText();
			final String surnameFromTextfield = EditBirthdayViewController.this.surname_TextField.getText();

			try{
				if(!nameFromTextField.matches(EditBirthdayViewController.this.personToEdit.getName())){
					anyChangeAtAll = !anyChangeAtAll;
				}
				if(!middleNameFromTextField.matches(EditBirthdayViewController.this.personToEdit.getMisc())){
					anyChangeAtAll = !anyChangeAtAll;
				}
				if(!surnameFromTextfield.matches(EditBirthdayViewController.this.personToEdit.getSurname())){
					anyChangeAtAll = !anyChangeAtAll;
				}
				if(!(EditBirthdayViewController.this.birthday_DatePicker.getValue() == EditBirthdayViewController.this.personToEdit.getBirthday())){
					anyChangeAtAll = !anyChangeAtAll;
				}
			} catch (final NullPointerException exception){
				LOG.catching(Level.INFO, exception);
				LOG.info(EditBirthdayViewController.this.personToEdit.toExtendedString());
				anyChangeAtAll = true;
			}

			if(anyChangeAtAll){
				final Person updatedPerson = new Person(surnameFromTextfield, nameFromTextField, middleNameFromTextField, EditBirthdayViewController.this.birthday_DatePicker.getValue());
				PersonManager.getInstance().updatePerson(EditBirthdayViewController.this.personToEdit, updatedPerson);
				new Thread(new UpdateAllSubBirthdayListsTask(EditBirthdayViewController.this.getMainController().getSessionInfos())).start();
				EditBirthdayViewController.this.getMainController().goToBirthdaysOverview();
			}

		}

	};

	private final EventHandler<ActionEvent> changeLanguageHandler = new EventHandler<ActionEvent>(){
		// TODO this is shit
		@Override
		public void handle(final ActionEvent event){
			EditBirthdayViewController.this.getMainController().getSessionInfos().setAppLocale(Locale.getDefault());
			EditBirthdayViewController.this.getMainController().getSessionInfos().getLangResourceManager().changeLocale(new Locale("de", "DE"));
			EditBirthdayViewController.this.updateLocalisation();
		}
	};

	private final EventHandler<ActionEvent> exitHandler = new EventHandler<ActionEvent>(){

		@Override
		public void handle(final ActionEvent event){
			final BirthdaysOverviewController birthdaysOverviewController = new BirthdaysOverviewController(EditBirthdayViewController.this.getMainController());
			final UpdateAllSubBirthdayListsTask updateAllSubBirthdayListsTask = new UpdateAllSubBirthdayListsTask(EditBirthdayViewController.this.getMainController().getSessionInfos());
			updateAllSubBirthdayListsTask.addEventFilter(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>(){
				@Override
				public void handle(final WorkerStateEvent t){
					final Boolean result = updateAllSubBirthdayListsTask.getValue();
					if(result){
						LOG.info("updateAllSubBirthdayListsTask succsesfully");
						EditBirthdayViewController.this.getMainController().goToBirthdaysOverview(birthdaysOverviewController);
					} else{
						LOG.info("Saveing faild");
					}
				}
			});
			new Thread(updateAllSubBirthdayListsTask).start();

		}
	};

	private final EventHandler<ActionEvent> deletePersonHandler = new EventHandler<ActionEvent>(){
		@Override
		public void handle(final ActionEvent event){
			PersonManager.getInstance().deletePerson(EditBirthdayViewController.this.personToEdit);
			new Thread(new UpdateAllSubBirthdayListsTask(EditBirthdayViewController.this.getMainController().getSessionInfos())).start();
			EditBirthdayViewController.this.getMainController().goToBirthdaysOverview();
		}
	};

	public EditBirthdayViewController(final MainController mainController){
		super(mainController);
		this.personToEdit = new Person();
	}

	public EditBirthdayViewController(final MainController mainController, final Person person){
		super(mainController);
		this.personToEdit = person;
	}

	private void assertions(){
		assert this.file_menu != null : "fx:id=\"file_menu\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.openFile_MenuItem != null : "fx:id=\"openFile_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.openRecent_MenuItem != null : "fx:id=\"openRecent_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.closeFile_MenuItem != null : "fx:id=\"closeFile_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.saveFile_MenuItem != null : "fx:id=\"saveFile_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.saveAsFile_MenuItem != null : "fx:id=\"saveAsFile_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.exporteFile_MenuItem != null : "fx:id=\"exporteFile_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.preferences_MenuItem != null : "fx:id=\"preferences_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.quit_MenuItem != null : "fx:id=\"quit_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.edit_menu != null : "fx:id=\"edit_menu\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.newBirthday_MenuItem != null : "fx:id=\"newBirthday_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.importBirthdays_MenuItem != null : "fx:id=\"importBirthdays_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.deleteBirthdays_MenuItem != null : "fx:id=\"deleteBirthdays_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.help_menu != null : "fx:id=\"help_menu\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.debug != null : "fx:id=\"debug\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.refresh_MenuItem != null : "fx:id=\"refresh_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.changeLanguage_MenuItem != null : "fx:id=\"changeLanguage_MenuItem\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.cancel_Button != null : "fx:id=\"cancel_Button\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.delete_Button != null : "fx:id=\"delete_Button\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.save_Button != null : "fx:id=\"save_Button\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.identifyingPerson_label != null : "fx:id=\"identifyingPerson_label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.name_Label != null : "fx:id=\"name_Label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.name_TextField != null : "fx:id=\"name_TextField\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.middleName_Label != null : "fx:id=\"middleName_Label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.middleName_TextField != null : "fx:id=\"middleName_TextField\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.surname_Label != null : "fx:id=\"surname_Label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.surname_TextField != null : "fx:id=\"surname_TextField\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.birthday_Label != null : "fx:id=\"birthday_Label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.birthday_DatePicker != null : "fx:id=\"birthday_DatePicker\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.openedFile_label != null : "fx:id=\"openedFile_label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.x3 != null : "fx:id=\"x3\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.x4 != null : "fx:id=\"x4\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
		assert this.date_label != null : "fx:id=\"date_label\" was not injected: check your FXML file 'EditBirthdayView.fxml'.";
	}

	private void bindComponents(){
		this.cancel_Button.addEventHandler(ActionEvent.ANY, this.exitHandler);
		this.save_Button.addEventHandler(ActionEvent.ANY, this.savePersonHandler);
		this.delete_Button.addEventHandler(ActionEvent.ANY, this.deletePersonHandler);
	}

	/**
	 * @return the personToEdit
	 */
	public Person getPersonToEdit(){
		return this.personToEdit;
	}

	@Override
	public void initialize(final URL arg0, final ResourceBundle arg1){
		this.assertions();
		this.updateLocalisation();
		this.bindComponents();
		this.loadPerson();
	}

	private void loadPerson(){
		this.name_TextField.setText(this.personToEdit.getName());
		this.surname_TextField.setText(this.personToEdit.getSurname());
		this.birthday_DatePicker.setValue(this.personToEdit.getBirthday());
		this.middleName_TextField.setText(this.personToEdit.getMisc());
	}

	private void updateLocalisation(){
		final LangResourceManager resourceManager = this.getMainController().getSessionInfos().getLangResourceManager();

		this.file_menu.setText(resourceManager.getLocaleString(LangResourceKeys.file_menu));
		this.openFile_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.openFile_MenuItem));
		this.openRecent_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.openRecent_MenuItem));
		this.closeFile_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.closeFile_MenuItem));
		this.saveFile_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.saveFile_MenuItem));
		this.saveAsFile_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.saveAsFile_MenuItem));
		this.exporteFile_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.exporteFile_MenuItem));
		this.preferences_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.preferences_MenuItem));
		this.quit_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.quit_MenuItem));

		this.edit_menu.setText(resourceManager.getLocaleString(LangResourceKeys.edit_menu));
		this.newBirthday_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.newBirthday_MenuItem));
		this.importBirthdays_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.importBirthdays_MenuItem));
		this.deleteBirthdays_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.deleteBirthdays_MenuItem));

		this.help_menu.setText(resourceManager.getLocaleString(LangResourceKeys.help_menu));

		this.identifyingPerson_label.setText(resourceManager.getLocaleString(LangResourceKeys.identifyingPerson_label));
		this.name_Label.setText(resourceManager.getLocaleString(LangResourceKeys.name_Label));
		this.middleName_Label.setText(resourceManager.getLocaleString(LangResourceKeys.middleName_Label));
		this.surname_Label.setText(resourceManager.getLocaleString(LangResourceKeys.surname_Label));
		this.birthday_Label.setText(resourceManager.getLocaleString(LangResourceKeys.birthday_Label));

		this.openFile_MenuItem.addEventHandler(ActionEvent.ANY, this.getMainController().openFromFileChooserHandler);
		this.changeLanguage_MenuItem.addEventHandler(ActionEvent.ANY, this.changeLanguageHandler);
		String property = null;
		property = this.getMainController().getSessionInfos().getConfigHandler().getProperty(PropertieFields.LAST_OPEND);
		this.recentFiles_MenuItem = new MenuItem(new File(property).getName());
		this.recentFiles_MenuItem.addEventHandler(ActionEvent.ANY, this.getMainController().openFromRecentHandler);
		this.openRecent_MenuItem.getItems().add(this.recentFiles_MenuItem);
	}

}