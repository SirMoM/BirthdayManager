/**
 *
 */
package application.controller;

import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import application.util.PropertieFields;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * @author Admin
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class PreferencesViewController extends Controller{
	private Properties propertiesToEdit;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Label languageOptions_label;

	@FXML
	private Label chooseLanguage_label;

	@FXML
	private ComboBox<Locale> language_CompoBox;

	@FXML
	private Label saveOptions_label;

	@FXML
	private CheckBox writeThru_CheckBox;

	@FXML
	private CheckBox autoSave_CheckBox;

	@FXML
	private Label miscellaneous_label;

	@FXML
	private CheckBox openFileOnStart_Checkbox;

	@FXML
	private TextField startupFile_textField;

	@FXML
	private Button chooseFile_button;

	@FXML
	private Button cancel_button;

	@FXML
	private Button save_button;

	/**
	 * @param mainController
	 */
	public PreferencesViewController(final MainController mainController){
		super(mainController);
	}

	private void assertions(){
		assert this.languageOptions_label != null : "fx:id=\"languageOptions_label\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert this.chooseLanguage_label != null : "fx:id=\"chooseLanguage_label\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert this.language_CompoBox != null : "fx:id=\"language_CompoBox\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert this.saveOptions_label != null : "fx:id=\"saveOptions_label\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert this.writeThru_CheckBox != null : "fx:id=\"writeThru_CheckBox\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert this.autoSave_CheckBox != null : "fx:id=\"autoSave_CheckBox\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert this.miscellaneous_label != null : "fx:id=\"miscellaneous_label\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert this.openFileOnStart_Checkbox != null : "fx:id=\"openFileOnStart_Checkbox\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert this.startupFile_textField != null : "fx:id=\"startupFile_textField\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert this.chooseFile_button != null : "fx:id=\"chooseFile_button\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert this.cancel_button != null : "fx:id=\"cancel_button\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert this.save_button != null : "fx:id=\"save_button\" was not injected: check your FXML file 'PreferencesView.fxml'.";
	}

	private void bindComponents(){
		this.fillComboBoxLanguages();

	}

	private void fillComboBoxLanguages(){
		this.language_CompoBox.getItems().addAll(Locale.GERMAN, Locale.ENGLISH);

		this.language_CompoBox.setCellFactory(new Callback<ListView<Locale>, ListCell<Locale>>(){

			@Override
			public ListCell<Locale> call(final ListView<Locale> param){
				return new ListCell<Locale>(){
					{
						this.setContentDisplay(ContentDisplay.TEXT_ONLY);
					}

					@Override
					protected void updateItem(final Locale item, final boolean empty){
						super.updateItem(item, empty);
						if(item == null || empty){
							this.setGraphic(null);
						} else{
							this.setText(item.getDisplayLanguage());
						}
					}
				};
			}

		});
		final StringConverter<Locale> converter = new StringConverter<Locale>(){

			@Override
			public Locale fromString(final String string){
				return new Locale(string);
			}

			@Override
			public String toString(final Locale locale){
				return locale.getDisplayLanguage();
			}
		};
		this.language_CompoBox.setConverter(converter);
	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources){
		this.assertions();
		this.bindComponents();
		this.loadPreferences();
		this.updateLocalisation();
	}

	private void loadPreferences(){
		this.propertiesToEdit = this.getMainController().getSessionInfos().getPropertiesHandler().getProperties();
		this.language_CompoBox.setValue(new Locale(this.propertiesToEdit.getProperty(PropertieFields.SAVED_LOCALE)));
	}

	private void updateLocalisation(){
		// TODO Auto-generated method stub

	}
}
