/**
 *
 */
package application.controller;

import application.util.PropertyFields;
import application.util.PropertyManager;
import application.util.localisation.LangResourceKeys;
import application.util.localisation.LangResourceManager;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author Admin
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class PreferencesViewController extends Controller {
    private static final Logger LOG = LogManager.getLogger(PreferencesViewController.class.getName());
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
    private Tooltip autsave_Tooltip;

    @FXML
    private Label miscellaneous_label;

    @FXML
    private CheckBox openFileOnStart_Checkbox;

    @FXML
    private Tooltip openFileOnStartUp_ToolTip;

    @FXML
    private TextField startupFile_textField;

    @FXML
    private Button chooseFile_button;
    ChangeListener<Boolean> openFileOnStartCheckboxChangeListener = (observable, oldValue, newValue) -> {
        PreferencesViewController.this.startupFile_textField.setDisable(!newValue);
        PreferencesViewController.this.chooseFile_button.setDisable(!newValue);
    };
    @FXML
    private Button cancel_button;
    private final EventHandler<ActionEvent> exitHandler = event -> {
        final Stage stage = (Stage) PreferencesViewController.this.cancel_button.getScene().getWindow();
        stage.close();
        LOG.trace("Close preferences");
    };
    private final EventHandler<ActionEvent> chooseFileHandler = event -> {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(new LangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));

        try {
            PropertyManager.getInstance();
            fileChooser.setInitialDirectory(new File(PropertyManager.getProperty(PropertyFields.LAST_OPENED)).getParentFile());
        } catch (final NullPointerException nullPointerException) {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("CSV Files", "*.csv"));

        final File saveFile = fileChooser.showOpenDialog(PreferencesViewController.this.getMainController().getStage().getScene().getWindow());

        PreferencesViewController.this.startupFile_textField.setText(saveFile.getAbsolutePath());
        ((Stage) PreferencesViewController.this.cancel_button.getParent().getScene().getWindow()).toFront();
    };
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
    @FXML
    private CheckBox reminder_CheckBox;
    private final EventHandler<ActionEvent> savePropertiesHandler = event -> {
        Properties properties = PropertyManager.getInstance().getProperties();
        properties.setProperty(PropertyFields.SAVED_LOCALE, PreferencesViewController.this.language_CompoBox.getValue().toString());
        properties.setProperty(PropertyFields.AUTOSAVE, PreferencesViewController.this.autoSave_CheckBox.selectedProperty().getValue().toString());
        properties.setProperty(PropertyFields.WRITE_THRU, PreferencesViewController.this.writeThru_CheckBox.selectedProperty().getValue().toString());
        properties.setProperty(PropertyFields.OPEN_FILE_ON_START, PreferencesViewController.this.openFileOnStart_Checkbox.selectedProperty().getValue().toString());
        properties.setProperty(PropertyFields.EXPORT_WITH_ALARM, PreferencesViewController.this.iCalNotification_CheckBox.selectedProperty().getValue().toString());
        try {
            if (!PreferencesViewController.this.startupFile_textField.getText().endsWith(".csv")) {
                // TODO Localisation
                new Alert(AlertType.WARNING).setContentText("Tried to set the file to be opened automatically so that it is not CSV!");
            } else {
                properties.setProperty(PropertyFields.FILE_ON_START, PreferencesViewController.this.startupFile_textField.getText());
            }
        } catch (final NullPointerException nullPointerException) {
            LOG.catching(Level.INFO, nullPointerException);
            LOG.info("startupFile_textField was not properly set ?");
        }
        properties.setProperty(PropertyFields.FIRST_HIGHLIGHT_COLOR, PreferencesViewController.this.first_ColorPicker.getValue().toString().replace("0x", "#"));
        properties.setProperty(PropertyFields.SECOND_HIGHLIGHT_COLOR, PreferencesViewController.this.second_ColorPicker.getValue().toString().replace("0x", "#"));
        properties.setProperty(PropertyFields.SHOW_BIRTHDAYS_COUNT, PreferencesViewController.this.countBirthdaysShown_Spinner.getValue().toString());
        properties.setProperty(PropertyFields.DARK_MODE, String.valueOf(PreferencesViewController.this.darkMode_ToggleButton.isSelected()));
        properties.setProperty(PropertyFields.NEW_VERSION_REMINDER, String.valueOf(PreferencesViewController.this.reminder_CheckBox.isSelected()));

        try {
            PropertyManager.getInstance().storeProperties("Saved properties" + LocalDateTime.now());
            PreferencesViewController.this.updateLocalisation();
        } catch (final IOException ioException) {
            LOG.catching(ioException);
        } catch (final NullPointerException nullPointerException) {
            LOG.catching(Level.INFO, nullPointerException);
            LOG.info("A field was not properly set ?");
        }
        PreferencesViewController.this.getMainController().settingsChanged();
        ((Stage) save_button.getScene().getWindow()).close();
    };

    public PreferencesViewController(final MainController mainController) {
        super(mainController);
    }

    /** All assertions for the controller. Checks if all FXML-Components have been loaded properly. */
    private void assertions() {
        assert languageOptions_Label != null : "fx:id=\"languageOptions_Label\" was not injected: check your FXML file 'PreferencesView.fxml'.";
        assert chooseLanguage_Label != null : "fx:id=\"chooseLanguage_Label\" was not injected: check your FXML file 'PreferencesView.fxml'.";
        assert language_CompoBox != null : "fx:id=\"language_CompoBox\" was not injected: check your FXML file 'PreferencesView.fxml'.";
        assert saveOptions_Label != null : "fx:id=\"saveOptions_Label\" was not injected: check your FXML file 'PreferencesView.fxml'.";
        assert writeThru_CheckBox != null : "fx:id=\"writeThru_CheckBox\" was not injected: check your FXML file 'PreferencesView.fxml'.";
        assert writeThru_Tooltip != null : "fx:id=\"writeThru_Tooltip\" was not injected: check your FXML file 'PreferencesView.fxml'.";
        assert autoSave_CheckBox != null : "fx:id=\"autoSave_CheckBox\" was not injected: check your FXML file 'PreferencesView.fxml'.";
        assert autsave_Tooltip != null : "fx:id=\"autsave_Tooltip\" was not injected: check your FXML file 'PreferencesView.fxml'.";
        assert miscellaneous_label != null : "fx:id=\"miscellaneous_label\" was not injected: check your FXML file 'PreferencesView.fxml'.";
        assert openFileOnStart_Checkbox != null : "fx:id=\"openFileOnStart_Checkbox\" was not injected: check your FXML file 'PreferencesView.fxml'.";
        assert openFileOnStartUp_ToolTip != null : "fx:id=\"openFileOnStartUp_ToolTip\" was not injected: check your FXML file 'PreferencesView.fxml'.";
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

    /** Binds the JavaFX Components to their {@link EventHandler}. */
    private void bindComponents() {
        this.chooseFile_button.addEventHandler(ActionEvent.ANY, this.chooseFileHandler);
        this.openFileOnStart_Checkbox.selectedProperty().addListener(this.openFileOnStartCheckboxChangeListener);
        this.save_button.addEventHandler(ActionEvent.ANY, this.savePropertiesHandler);
        this.cancel_button.addEventHandler(ActionEvent.ANY, this.exitHandler);
        this.countBirthdaysShown_Spinner.setValueFactory(new IntegerSpinnerValueFactory(5, 50, 10, 1));
    }

    /** Fills the {@link ComboBox} with the available languages. */
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
                            LOG.info("empty dropbox item");
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

    /** Loads the preferences of this application- */
    private void loadPreferences() {
        this.fillComboBoxLanguages();
        final String displayLanguage = new Locale(PropertyManager.getProperty(PropertyFields.SAVED_LOCALE)).getDisplayLanguage();
        LOG.info(new Locale(displayLanguage).getDisplayLanguage());
        this.language_CompoBox.getSelectionModel().select(new Locale(displayLanguage));
        this.writeThru_CheckBox.selectedProperty().set(Boolean.parseBoolean(PropertyManager.getProperty(PropertyFields.WRITE_THRU)));
        this.autoSave_CheckBox.selectedProperty().set(Boolean.parseBoolean(PropertyManager.getProperty(PropertyFields.AUTOSAVE)));

        final boolean openFileOnStart = Boolean.parseBoolean(PropertyManager.getProperty(PropertyFields.OPEN_FILE_ON_START));

        this.openFileOnStart_Checkbox.selectedProperty().set(openFileOnStart);
        try {
            this.startupFile_textField.setText(PropertyManager.getProperty(PropertyFields.LAST_OPENED));
        } catch (final NullPointerException nullPointerException) {
            LOG.catching(Level.INFO, nullPointerException);
        }
        if (openFileOnStart && PropertyManager.getProperty(PropertyFields.FILE_ON_START) != null) {
            if (PropertyManager.getProperty(PropertyFields.FILE_ON_START).endsWith(".csv")) {
                this.startupFile_textField.setText(PropertyManager.getProperty(PropertyFields.FILE_ON_START));
            } else {
                PropertyManager.getInstance().getProperties().setProperty(PropertyFields.FILE_ON_START, "");
            }
        }
        this.startupFile_textField.setDisable(!openFileOnStart);
        this.chooseFile_button.setDisable(!openFileOnStart);

        this.first_ColorPicker.setValue(Color.web(PropertyManager.getProperty(PropertyFields.FIRST_HIGHLIGHT_COLOR)));
        this.second_ColorPicker.setValue(Color.web(PropertyManager.getProperty(PropertyFields.SECOND_HIGHLIGHT_COLOR)));
        this.iCalNotification_CheckBox.selectedProperty().set(Boolean.parseBoolean(PropertyManager.getProperty(PropertyFields.EXPORT_WITH_ALARM)));
        this.countBirthdaysShown_Spinner.getValueFactory().setValue(Integer.valueOf(PropertyManager.getProperty(PropertyFields.SHOW_BIRTHDAYS_COUNT)));
        this.darkMode_ToggleButton.setSelected(Boolean.parseBoolean(PropertyManager.getProperty(PropertyFields.DARK_MODE)));
        this.reminder_CheckBox.setSelected(Boolean.parseBoolean(PropertyManager.getProperty(PropertyFields.NEW_VERSION_REMINDER)));
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
        this.openFileOnStart_Checkbox.setText(resourceManager.getLocaleString(LangResourceKeys.openFileOnStart_Checkbox));
        this.chooseFile_button.setText(resourceManager.getLocaleString(LangResourceKeys.chooseFile_button));
        this.cancel_button.setText(resourceManager.getLocaleString(LangResourceKeys.cancel_button));

        this.autsave_Tooltip.setText(resourceManager.getLocaleString(LangResourceKeys.autosave_Tooltip));
        this.writeThru_Tooltip.setText(resourceManager.getLocaleString(LangResourceKeys.writeThru_Tooltip));
        this.openFileOnStartUp_ToolTip.setText(resourceManager.getLocaleString(LangResourceKeys.openFileOnStartUp_ToolTip));

        this.firstHighlightingColor_Label.setText(resourceManager.getLocaleString(LangResourceKeys.firstHighlightingColor_label));
        this.secondHighlightingColor_Label.setText(resourceManager.getLocaleString(LangResourceKeys.secondHighlightingColor_label));
        this.appearanceOptions_Label.setText(resourceManager.getLocaleString(LangResourceKeys.appearanceOptions_label));
        this.countBirthdaysShown_Label.setText(resourceManager.getLocaleString(LangResourceKeys.countBirthdaysShown_Label));
        this.iCalNotification_CheckBox.setText(resourceManager.getLocaleString(LangResourceKeys.iCalNotification_checkBox));
        this.darkMode_ToggleButton.setText(resourceManager.getLocaleString(LangResourceKeys.darkMode_button));
        this.save_button.setText(resourceManager.getLocaleString(LangResourceKeys.save_button));
        this.cancel_button.setText(resourceManager.getLocaleString(LangResourceKeys.cancel_button));
        this.reminder_CheckBox.setText(resourceManager.getLocaleString(LangResourceKeys.reminder_CheckBox));
    }

    @Override
    public void placeFocus() {
        save_button.requestFocus();
    }
}
