package application;

import application.controller.MainController;
import application.model.PersonManager;
import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppStart extends Application {

  Logger LOG = LogManager.getLogger(AppStart.class.getName());

  public static void main(final String[] args) {
    launch(args);
  }

  @Override
  public void init() throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(
        (thread, throwable) -> {
          LogManager.getLogger(PersonManager.class).catching(Level.FATAL, throwable);
          LogManager.getLogger(PersonManager.class).catching(Level.FATAL, throwable.getCause());

          final Alert alert = new Alert(AlertType.WARNING);
          alert.setTitle("ERROR");
          alert.setHeaderText("Someting went wrong! \n Consider sending me the log.");

          final StringBuilder stringBuilder = new StringBuilder("Stacktrace: \n");
          for (int i = 0; i < throwable.getStackTrace().length; i++) {
            stringBuilder.append(throwable.getStackTrace()[i]);
            stringBuilder.append(System.getProperty("line.separator"));
          }

          final TextArea textArea = new TextArea(stringBuilder.toString());
          textArea.setEditable(false);
          textArea.setWrapText(true);
          final Button copyButton = new Button("Copy");
          copyButton.setOnAction(
              event -> {
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(stringBuilder.toString());
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

  @Override
  public void start(final Stage stage) throws FileNotFoundException {
    final MainController mainController = new MainController(stage);
    mainController.start();
  }

  @Override
  public void stop() {
    LOG.debug("CLOSING!");
  }
}
