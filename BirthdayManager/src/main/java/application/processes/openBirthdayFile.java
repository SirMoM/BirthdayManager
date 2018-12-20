/**
 * 
 */
package application.processes;

import java.io.File;

import application.controller.Controller;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class openBirthdayFile extends Task<File> {

	private Controller controller;
	private Stage stage;

	/**
	 * @param controller
	 */
	public openBirthdayFile(Controller controller) {
		super();
		this.controller = controller;
		stage = this.controller.getMainController().getStage();
	}

	@Override
	protected File call() throws Exception {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"), new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"), new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"), new ExtensionFilter("All Files", "*.*"));
		File selectedFile = fileChooser.showOpenDialog(stage);
		if (selectedFile != null) {
			System.out.println(selectedFile.getAbsolutePath());
		}
		return selectedFile;
	}

}
