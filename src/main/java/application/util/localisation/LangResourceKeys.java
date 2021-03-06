/**
 *
 */
package application.util.localisation;

/**
 * @author Noah Ruben All keys for adressing the right string
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public enum LangResourceKeys {

    // Person
    age,

    // Overview
    str_nextBirthday_Label, str_recentBirthday_Label,

    file_menu, openFile_MenuItem, openRecent_MenuItem, closeFile_MenuItem, saveFile_MenuItem, saveAsFile_MenuItem, preferences_MenuItem, quit_MenuItem,

    edit_menu, showNextBirthdays_MenuItem, showLastBirthdays_MenuItem, newBirthday_MenuItem, importBirthdays_MenuItem, deleteBirthdays_MenuItem,

    help_menu,

    fileChooserCaption,

    // List menu
    openBirthday_MenuItem,

    week_tap, monday_column1, tuesday_column1, wednesday_column1, thursday_column1, friday_column1, saturday_column1, sunday_column1,

    month_tap, monday_column2, tuesday_column2, wednesday_column2, thursday_column2, friday_column2, saturday_column2, sunday_column2,

    // Edit Birthday view
    identifyingPerson_label, name_Label, name_TextField, middleName_Label, surname_Label, surname_TextField, birthday_Label, birthday_DatePicker,

    // Preferences View
    languageOptions_label, chooseLanguage_label, saveOptions_label, writeThru_CheckBox, autoSave_CheckBox, miscellaneous_label, openFileOnStart_Checkbox, chooseFile_button, firstHighlightingColor_label, secondHighlightingColor_label, appearanceOptions_label, countBirthdaysShown_Label, iCalNotification_checkBox, darkMode_button, reminder_CheckBox,

    // Search View
    search, enableFuzzy_RadioButton, enableRegEx_RadioButton, advancedSettings_TitledPane,

    // ToolTips
    autosave_Tooltip, writeThru_Tooltip, openFileOnStartUp_ToolTip,

    // Alert messages usw.
    save_before_exit, person_not_valid_warning, save_before_exit_question,

    // Miscellaneous
    yes, no, csv_file, txt_file, all_files, missedBirthdays, missedBirthdaysMsg,
    cancel_button, save_button, delete_button,
    calendar_file, export_msg, export, exportMenuItem, close_Button,

    // About page
    about, appName_Label, version_Label, createdBy_Label, visitWebsite, visitGithub, checkForUpdates;
}
