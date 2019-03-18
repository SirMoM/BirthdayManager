/**
 *
 */
package application.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.logging.log4j.Level;

import application.util.PropertieFields;
import application.util.PropertieManager;
import application.util.localisation.LangResourceKeys;
import application.util.localisation.LangResourceManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * @author Admin
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class PreferencesViewController extends Controller {
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
	private Tooltip writeThru_Tooltip;

	@FXML
	private CheckBox autoSave_CheckBox;

	@FXML
	private Tooltip autsave_Tooltipp;

	@FXML
	private Label miscellaneous_label;

	@FXML
	private CheckBox openFileOnStart_Checkbox;

	@FXML
	private Tooltip openFileOnStartUp_ToolTipp;

	@FXML
	private TextField startupFile_textField;

	@FXML
	private Button chooseFile_button;

	@FXML
	private Button cancel_button;

	@FXML
	private Button save_button;
	ChangeListener<Boolean> openFileOnStartCheckboxChangeListener = new ChangeListener<Boolean>() {

		@Override
		public void changed(final ObservableValue<? extends Boolean> observable, final Boolean oldValue,
				final Boolean newValue) {
			PreferencesViewController.this.startupFile_textField.setDisable(!newValue);
			PreferencesViewController.this.chooseFile_button.setDisable(!newValue);
		}
	};

	private final EventHandler<ActionEvent> savePropertiesHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(final ActionEvent arg0) {

			PreferencesViewController.this.propertiesToEdit.setProperty(PropertieFields.SAVED_LOCALE,
					PreferencesViewController.this.language_CompoBox.getValue().toString());
			PreferencesViewController.this.propertiesToEdit.setProperty(PropertieFields.AUTOSAVE,
					PreferencesViewController.this.autoSave_CheckBox.selectedProperty().getValue().toString());
			PreferencesViewController.this.propertiesToEdit.setProperty(PropertieFields.WRITE_THRU,
					PreferencesViewController.this.writeThru_CheckBox.selectedProperty().getValue().toString());
			PreferencesViewController.this.propertiesToEdit.setProperty(PropertieFields.OPEN_FILE_ON_START,
					PreferencesViewController.this.openFileOnStart_Checkbox.selectedProperty().getValue().toString());
			PreferencesViewController.this.propertiesToEdit.setProperty(PropertieFields.FILE_ON_START,
					PreferencesViewController.this.startupFile_textField.getText());

			try {
				PropertieManager.getInstance().storeProperties("Saved properies" + LocalDateTime.now().toString());
				PreferencesViewController.this.updateLocalisation();
				PreferencesViewController.this.getMainController().settingsChanged();
			} catch (final FileNotFoundException fileNotFoundException) {
				PreferencesViewController.this.LOG.catching(fileNotFoundException);
			} catch (final IOException ioException) {
				PreferencesViewController.this.LOG.catching(ioException);
			}
		}
	};

	private final EventHandler<ActionEvent> exitHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(final ActionEvent event) {
			final Stage stage = (Stage) PreferencesViewController.this.cancel_button.getScene().getWindow();
			stage.close();
			PreferencesViewController.this.LOG.trace("Close preferences");
		}
	};

	private final EventHandler<ActionEvent> chooseFileHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(final ActionEvent event) {
			final FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(new LangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));

			try {
				PropertieManager.getInstance();
				fileChooser.setInitialDirectory(
						new File(PropertieManager.getPropertie(PropertieFields.LAST_OPEND).toString()).getParentFile());
			} catch (final NullPointerException nullPointerException) {
				fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
			}
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"),
					new ExtensionFilter("CSV Files", "*.csv"), new ExtensionFilter("All Files", "*.*"));

			final File saveFile = fileChooser.showOpenDialog(
					PreferencesViewController.this.getMainController().getStage().getScene().getWindow());

			PreferencesViewController.this.startupFile_textField.setText(saveFile.getAbsolutePath());
			((Stage) PreferencesViewController.this.cancel_button.getParent().getScene().getWindow()).toFront();
		}
	};

	/**
	 *
	 * @see application.controller.Controller#Controller(MainController)
	 */
	public PreferencesViewController(final MainController mainController) {
		super(mainController);
	}

	/**
	 * All assertions for the controller. Checks if all FXML-Components have been
	 * loaded properly.
	 */
	private void assertions() {
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

	/**
	 * Binds the JavaFX Components to their {@link EventHandler}.
	 */
	private void bindComponents() {
		this.chooseFile_button.addEventHandler(ActionEvent.ANY, this.chooseFileHandler);

		this.openFileOnStart_Checkbox.selectedProperty().addListener(this.openFileOnStartCheckboxChangeListener);

		this.save_button.addEventHandler(ActionEvent.ANY, this.savePropertiesHandler);
		this.cancel_button.addEventHandler(ActionEvent.ANY, this.exitHandler);
	}

	/**
	 * Fills the {@link ComboBox} with the available languages.
	 */
	private void fillComboBoxLanguages() {
		final StringConverter<Locale> converter = new StringConverter<Locale>() {

			@Override
			public Locale fromString(final String string) {
				return new Locale(string);
			}

			@Override
			public String toString(final Locale locale) {
				return locale.getDisplayLanguage();
			}
		};
		this.language_CompoBox.setConverter(converter);
		this.language_CompoBox.getItems().addAll(Locale.GERMANY, Locale.UK);

		this.language_CompoBox.setCellFactory(new Callback<ListView<Locale>, ListCell<Locale>>() {

			@Override
			public ListCell<Locale> call(final ListView<Locale> param) {
				return new ListCell<Locale>() {
					{
						this.setContentDisplay(ContentDisplay.TEXT_ONLY);
					}

					@Override
					protected void updateItem(final Locale item, final boolean empty) {
						super.updateItem(item, empty);
						if (item == null || empty) {
							this.setGraphic(null);
							PreferencesViewController.this.LOG.info("empty dropbox item");
						} else {
							this.setText(item.getDisplayLanguage());
						}
					}
				};
			}

		});
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		this.assertions();
		this.bindComponents();
		this.updateLocalisation();
		this.loadPreferences();
	}

	/**
	 * Loads the preferences of this application-
	 */
	private void loadPreferences() {
		this.fillComboBoxLanguages();
		this.propertiesToEdit = PropertieManager.getInstance().getProperties();
		final String displayLanguage = new Locale(this.propertiesToEdit.getProperty(PropertieFields.SAVED_LOCALE))
				.getDisplayLanguage();
		this.LOG.info(new Locale(displayLanguage).getDisplayLanguage());
		this.language_CompoBox.getSelectionModel().select(new Locale(displayLanguage));
		this.writeThru_CheckBox.selectedProperty()
				.set(new Boolean(this.propertiesToEdit.getProperty(PropertieFields.WRITE_THRU)));
		this.autoSave_CheckBox.selectedProperty()
				.set(new Boolean(this.propertiesToEdit.getProperty(PropertieFields.AUTOSAVE)));

		final Boolean openFileOnStart = new Boolean(
				this.propertiesToEdit.getProperty(PropertieFields.OPEN_FILE_ON_START));

		this.openFileOnStart_Checkbox.selectedProperty().set(openFileOnStart);
		try {
			PropertieManager.getInstance();
			this.startupFile_textField.setText(PropertieManager.getPropertie(PropertieFields.LAST_OPEND));
		} catch (final NullPointerException nullPointerException) {
			this.LOG.catching(Level.INFO, nullPointerException);
		}
		if (openFileOnStart) {
			this.startupFile_textField.setText(this.propertiesToEdit.getProperty(PropertieFields.FILE_ON_START));
		}
		this.startupFile_textField.setDisable(!openFileOnStart);
		this.chooseFile_button.setDisable(!openFileOnStart);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see application.controller.Controller#updateLocalisation()
	 */
	@Override
	public void updateLocalisation() {
		final LangResourceManager resourceManager = new LangResourceManager();

		this.languageOptions_label.setText(resourceManager.getLocaleString(LangResourceKeys.languageOptions_label));
		this.chooseLanguage_label.setText(resourceManager.getLocaleString(LangResourceKeys.chooseLanguage_label));
		this.autoSave_CheckBox.setText(resourceManager.getLocaleString(LangResourceKeys.autoSave_CheckBox));
		this.saveOptions_label.setText(resourceManager.getLocaleString(LangResourceKeys.saveOptions_label));
		this.writeThru_CheckBox.setText(resourceManager.getLocaleString(LangResourceKeys.writeThru_CheckBox));
		this.miscellaneous_label.setText(resourceManager.getLocaleString(LangResourceKeys.miscellaneous_label));
		this.openFileOnStart_Checkbox
				.setText(resourceManager.getLocaleString(LangResourceKeys.openFileOnStart_Checkbox));
		this.chooseFile_button.setText(resourceManager.getLocaleString(LangResourceKeys.chooseFile_button));
		this.cancel_button.setText(resourceManager.getLocaleString(LangResourceKeys.cancel_button));

		this.autsave_Tooltipp.setText(resourceManager.getLocaleString(LangResourceKeys.autsave_Tooltipp));
		this.writeThru_Tooltip.setText(resourceManager.getLocaleString(LangResourceKeys.writeThru_Tooltip));
		this.openFileOnStartUp_ToolTipp
				.setText(resourceManager.getLocaleString(LangResourceKeys.openFileOnStartUp_ToolTipp));
	}
}
