/**
 *
 */
package application.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.scene.control.*;
import org.apache.logging.log4j.Level;

import application.util.PropertyFields;
import application.util.PropertyManager;
import application.util.localisation.LangResourceKeys;
import application.util.localisation.LangResourceManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.paint.Color;
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


	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Label languageOptions_Label;

	@FXML
	private Label chooseLanguage_Label;

	@FXML
	private ComboBox<Locale> language_CompoBox;

	@FXML
	private Label saveOptions_Label;

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

	@FXML
	private CheckBox iCalNotification_CheckBox;

	@FXML
	private ColorPicker first_ColorPicker;

	@FXML
	private ColorPicker second_ColorPicker;

	@FXML
	private Label firstHighlightingColor_Label;

	@FXML
	private Label secondHighlightingColor_Label;

	@FXML
	private Label appearanceOptions_Label;

	@FXML
	private Spinner<Integer> countBirthdaysShown_Spinner;

	@FXML
	private Label countBirthdaysShown_Label;

	@FXML
	private ToggleButton darkMode_ToggleButton;

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
			PropertyManager.getInstance().getProperties().setProperty(PropertyFields.SAVED_LOCALE,
					PreferencesViewController.this.language_CompoBox.getValue().toString());
			PropertyManager.getInstance().getProperties().setProperty(PropertyFields.AUTOSAVE,
					PreferencesViewController.this.autoSave_CheckBox.selectedProperty().getValue().toString());
			PropertyManager.getInstance().getProperties().setProperty(PropertyFields.WRITE_THRU,
					PreferencesViewController.this.writeThru_CheckBox.selectedProperty().getValue().toString());
			PropertyManager.getInstance().getProperties().setProperty(PropertyFields.OPEN_FILE_ON_START,
					PreferencesViewController.this.openFileOnStart_Checkbox.selectedProperty().getValue().toString());
			PropertyManager.getInstance().getProperties().setProperty(PropertyFields.EXPORT_WITH_ALARM,
					PreferencesViewController.this.iCalNotification_CheckBox.selectedProperty().getValue().toString());
			try {
				if (!PreferencesViewController.this.startupFile_textField.getText().endsWith(".csv")) {
					PreferencesViewController.this.LOG.debug("1");
					new Alert(AlertType.WARNING)
							.setContentText("Tried to set the file to be opened automatically so that it is not CSV!");
				} else {
					PreferencesViewController.this.LOG.debug("2");
					PropertyManager.getInstance().getProperties().setProperty(PropertyFields.FILE_ON_START,
							PreferencesViewController.this.startupFile_textField.getText());
				}
			} catch (final NullPointerException nullPointerException) {
				PreferencesViewController.this.LOG.catching(Level.INFO, nullPointerException);
				PreferencesViewController.this.LOG.info("startupFile_textField was not properly set ?");
			}

			System.out.println(PropertyManager.getProperty(PropertyFields.FILE_ON_START));
			PropertyManager.getInstance().getProperties().setProperty(PropertyFields.FIRST_HIGHLIGHT_COLOR,
					"#" + Integer.toHexString(PreferencesViewController.this.first_ColorPicker.getValue().hashCode()));
			PropertyManager.getInstance().getProperties().setProperty(PropertyFields.SECOND_HIGHLIGHT_COLOR,
					"#" + Integer.toHexString(PreferencesViewController.this.second_ColorPicker.getValue().hashCode()));
			PropertyManager.getInstance().getProperties().setProperty(PropertyFields.SHOW_BIRTHDAYS_COUNT,
					PreferencesViewController.this.countBirthdaysShown_Spinner.getValue().toString());

			try {
				PropertyManager.getInstance().storeProperties("Saved properies" + LocalDateTime.now().toString());
				PreferencesViewController.this.updateLocalisation();
				PreferencesViewController.this.getMainController().settingsChanged();
			} catch (final FileNotFoundException fileNotFoundException) {
				PreferencesViewController.this.LOG.catching(fileNotFoundException);
			} catch (final IOException ioException) {
				PreferencesViewController.this.LOG.catching(ioException);
			} catch (final NullPointerException nullPointerException) {
				PreferencesViewController.this.LOG.catching(Level.INFO, nullPointerException);
				PreferencesViewController.this.LOG.info("A field was not properly set ?");
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
				PropertyManager.getInstance();
				fileChooser.setInitialDirectory(
						new File(PropertyManager.getProperty(PropertyFields.LAST_OPEND).toString()).getParentFile());
			} catch (final NullPointerException nullPointerException) {
				fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
			}
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("CSV Files", "*.csv"));

			final File saveFile = fileChooser.showOpenDialog(
					PreferencesViewController.this.getMainController().getStage().getScene().getWindow());

			PreferencesViewController.this.startupFile_textField.setText(saveFile.getAbsolutePath());
			((Stage) PreferencesViewController.this.cancel_button.getParent().getScene().getWindow()).toFront();
		}
	};

	public PreferencesViewController(final MainController mainController) {
		super(mainController);
	}

	/**
	 * All assertions for the controller. Checks if all FXML-Components have been
	 * loaded properly.
	 */
	private void assertions() {
		assert languageOptions_Label != null : "fx:id=\"languageOptions_Label\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert chooseLanguage_Label != null : "fx:id=\"chooseLanguage_Label\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert language_CompoBox != null : "fx:id=\"language_CompoBox\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert saveOptions_Label != null : "fx:id=\"saveOptions_Label\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert writeThru_CheckBox != null : "fx:id=\"writeThru_CheckBox\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert writeThru_Tooltip != null : "fx:id=\"writeThru_Tooltip\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert autoSave_CheckBox != null : "fx:id=\"autoSave_CheckBox\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert autsave_Tooltipp != null : "fx:id=\"autsave_Tooltipp\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert miscellaneous_label != null : "fx:id=\"miscellaneous_label\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert openFileOnStart_Checkbox != null : "fx:id=\"openFileOnStart_Checkbox\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert openFileOnStartUp_ToolTipp != null : "fx:id=\"openFileOnStartUp_ToolTipp\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert startupFile_textField != null : "fx:id=\"startupFile_textField\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert chooseFile_button != null : "fx:id=\"chooseFile_button\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert cancel_button != null : "fx:id=\"cancel_button\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert save_button != null : "fx:id=\"save_button\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert iCalNotification_CheckBox != null : "fx:id=\"iCalNotification_CheckBox\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert first_ColorPicker != null : "fx:id=\"first_ColorPicker\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert second_ColorPicker != null : "fx:id=\"second_ColorPicker\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert firstHighlightingColor_Label != null : "fx:id=\"firstHighlightingColor_Label\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert secondHighlightingColor_Label != null : "fx:id=\"secondHighlightingColor_Label\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert appearanceOptions_Label != null : "fx:id=\"appearanceOptions_Label\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert countBirthdaysShown_Spinner != null : "fx:id=\"countBirthdaysShown_Spinner\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert countBirthdaysShown_Label != null : "fx:id=\"countBirthdaysShown_Label\" was not injected: check your FXML file 'PreferencesView.fxml'.";
		assert darkMode_ToggleButton != null : "fx:id=\"darkMode_ToggleButton\" was not injected: check your FXML file 'PreferencesView.fxml'.";

	}

	/**
	 * Binds the JavaFX Components to their {@link EventHandler}.
	 */
	private void bindComponents() {
		this.chooseFile_button.addEventHandler(ActionEvent.ANY, this.chooseFileHandler);

		this.openFileOnStart_Checkbox.selectedProperty().addListener(this.openFileOnStartCheckboxChangeListener);

		this.save_button.addEventHandler(ActionEvent.ANY, this.savePropertiesHandler);
		this.cancel_button.addEventHandler(ActionEvent.ANY, this.exitHandler);
		this.countBirthdaysShown_Spinner.setValueFactory(new IntegerSpinnerValueFactory(5, 50, 10, 1));
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
		final String displayLanguage = new Locale(PropertyManager.getProperty(PropertyFields.SAVED_LOCALE))
				.getDisplayLanguage();
		this.LOG.info(new Locale(displayLanguage).getDisplayLanguage());
		this.language_CompoBox.getSelectionModel().select(new Locale(displayLanguage));
		this.writeThru_CheckBox.selectedProperty()
				.set(new Boolean(PropertyManager.getProperty(PropertyFields.WRITE_THRU)));
		this.autoSave_CheckBox.selectedProperty()
				.set(new Boolean(PropertyManager.getProperty(PropertyFields.AUTOSAVE)));

		final Boolean openFileOnStart = new Boolean(PropertyManager.getProperty(PropertyFields.OPEN_FILE_ON_START));

		this.openFileOnStart_Checkbox.selectedProperty().set(openFileOnStart);
		try {
			PropertyManager.getInstance();
			this.startupFile_textField.setText(PropertyManager.getProperty(PropertyFields.LAST_OPEND));
		} catch (final NullPointerException nullPointerException) {
			this.LOG.catching(Level.INFO, nullPointerException);
		}
		if (openFileOnStart) {
			if (PropertyManager.getProperty(PropertyFields.FILE_ON_START) != null) {
				if (PropertyManager.getProperty(PropertyFields.FILE_ON_START).endsWith(".csv")) {
					this.startupFile_textField.setText(PropertyManager.getProperty(PropertyFields.FILE_ON_START));
				} else {
					PropertyManager.getInstance().getProperties().setProperty(PropertyFields.FILE_ON_START, "");
				}
			}
		}
		this.startupFile_textField.setDisable(!openFileOnStart);
		this.chooseFile_button.setDisable(!openFileOnStart);

		this.first_ColorPicker.setValue(Color.web(PropertyManager.getProperty(PropertyFields.FIRST_HIGHLIGHT_COLOR)));
		this.second_ColorPicker.setValue(Color.web(PropertyManager.getProperty(PropertyFields.SECOND_HIGHLIGHT_COLOR)));
		this.iCalNotification_CheckBox.selectedProperty()
				.set(new Boolean(PropertyManager.getProperty(PropertyFields.EXPORT_WITH_ALARM)));
		this.countBirthdaysShown_Spinner.getValueFactory()
				.setValue(Integer.valueOf(PropertyManager.getProperty(PropertyFields.SHOW_BIRTHDAYS_COUNT)));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see application.controller.Controller#updateLocalisation()
	 */
	@Override
	public void updateLocalisation() {
		final LangResourceManager resourceManager = new LangResourceManager();

		this.languageOptions_Label.setText(resourceManager.getLocaleString(LangResourceKeys.languageOptions_label));
		this.chooseLanguage_Label.setText(resourceManager.getLocaleString(LangResourceKeys.chooseLanguage_label));
		this.autoSave_CheckBox.setText(resourceManager.getLocaleString(LangResourceKeys.autoSave_CheckBox));
		this.saveOptions_Label.setText(resourceManager.getLocaleString(LangResourceKeys.saveOptions_label));
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

		this.firstHighlightingColor_Label
				.setText(resourceManager.getLocaleString(LangResourceKeys.firstHighlightingColor_label));
		this.secondHighlightingColor_Label
				.setText(resourceManager.getLocaleString(LangResourceKeys.secondHighlightingColor_label));
		this.appearanceOptions_Label.setText(resourceManager.getLocaleString(LangResourceKeys.appearanceOptions_label));
		this.countBirthdaysShown_Label
				.setText(resourceManager.getLocaleString(LangResourceKeys.countBirthdaysShown_Label));
		this.iCalNotification_CheckBox
				.setText(resourceManager.getLocaleString(LangResourceKeys.iCalNotification_checkBox));
	}
}