package application;

import application.controller.MainController;
import application.util.ApplicationSetup;
import application.util.ThrowableFormatter;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppStart extends Application {

  private static final Logger LOG = LogManager.getLogger(AppStart.class.getName());
  private static final String DEFAULT_UNCAUGHT_EXCEPTION_TITLE = "Unexpected error";
  private static final String DEFAULT_UNCAUGHT_EXCEPTION_HEADER =
      "Something went wrong!\nConsider sending me the log.";
  private static final String DEFAULT_COPY_BUTTON = "Copy";

  public static void main(final String[] args) {
    launch(args);
  }

  @Override
  public void init() throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(
        (thread, throwable) -> {
          LOG.fatal("Uncaught exception on thread {}", thread.getName(), throwable);
          final ResourceBundle startupMessages = loadStartupMessages();

          final Alert alert = new Alert(AlertType.ERROR);
          alert.setTitle(
              getStartupMessage(
                  startupMessages, "uncaughtException_Title", DEFAULT_UNCAUGHT_EXCEPTION_TITLE));
          alert.setHeaderText(
              getStartupMessage(
                  startupMessages, "uncaughtException_Header", DEFAULT_UNCAUGHT_EXCEPTION_HEADER));

          final String stackTrace = ThrowableFormatter.toStackTrace(throwable);

          final TextArea textArea = new TextArea(stackTrace);
          textArea.setEditable(false);
          textArea.setWrapText(true);
          final Button copyButton =
              new Button(getStartupMessage(startupMessages, "copy_Button", DEFAULT_COPY_BUTTON));
          copyButton.setOnAction(
              event -> {
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(stackTrace);
                clipboard.setContent(content);
              });

          final GridPane gridPane = new GridPane();
          gridPane.setMaxWidth(Double.MAX_VALUE);
          gridPane.add(textArea, 0, 0);
          gridPane.add(copyButton, 0, 1);
          alert.getDialogPane().setContent(gridPane);
          alert.showAndWait();
        });
    super.init();
  }

  private static ResourceBundle loadStartupMessages() {
    final Locale locale =
        switch (Locale.getDefault().getLanguage()) {
          case "de" -> Locale.GERMANY;
          default -> Locale.UK;
        };
    try {
      return ResourceBundle.getBundle("lang", locale);
    } catch (final MissingResourceException missingResourceException) {
      return null;
    }
  }

  private static String getStartupMessage(
      final ResourceBundle resourceBundle, final String key, final String fallback) {
    if (resourceBundle == null || !resourceBundle.containsKey(key)) {
      return fallback;
    }
    return resourceBundle.getString(key);
  }

  @Override
  public void start(final Stage stage) throws IOException {
    ApplicationSetup.setup();

    final MainController mainController = new MainController(stage);
    mainController.start();
  }

  @Override
  public void stop() {
    LOG.debug("CLOSING!");
  }
}
