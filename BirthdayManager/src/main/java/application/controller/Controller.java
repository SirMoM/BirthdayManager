/**
 *
 */
package application.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.fxml.Initializable;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public abstract class Controller implements Initializable{
	final static Logger LOG = LogManager.getLogger();

	private final MainController mainController;

	public Controller(final MainController mainController){
		this.mainController = mainController;
	}

	public MainController getMainController(){
		return this.mainController;
	}
}
