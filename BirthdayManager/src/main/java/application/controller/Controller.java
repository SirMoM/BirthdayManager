/**
 * 
 */
package application.controller;

import javafx.fxml.Initializable;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public abstract class Controller implements Initializable {

	private final MainController mainController;

	public Controller(MainController mainController) {
		this.mainController = mainController;
	}

	public MainController getMainController() {
		return this.mainController;
	}
}
