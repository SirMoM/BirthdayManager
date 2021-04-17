/**
 * 
 */
package application.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class BirthdaysOverviewController extends Controller{
	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="openFileMenuItem"
	private MenuItem openFileMenuItem; // Value injected by FXMLLoader

	@FXML // fx:id="nextBirthdayLabel"
	private Label nextBirthdayLabel; // Value injected by FXMLLoader

	@FXML // fx:id="x3"
	private Font x3; // Value injected by FXMLLoader

	@FXML // fx:id="x4"
	private Color x4; // Value injected by FXMLLoader

	public BirthdaysOverviewController(MainController mainController){
		super(mainController);
	}

	final EventHandler<Event> openHandler = new EventHandler<Event>() {
		@Override
		public void handle(Event event) {
			if (event.getEventType() != ActionEvent.ANY) {
				if (!((KeyEvent) event).getCode().equals(KeyCode.ENTER)) {
					return;
				}
			}
		}
	};
	
	@Override
	@FXML // This method is called by the FXMLLoader when initialization is complete
	public void initialize(URL location, ResourceBundle resources){
		assert this.openFileMenuItem != null : "fx:id=\"openFileMenuItem\" was not injected: check your FXML file 'Kalender.fxml'.";
		assert this.nextBirthdayLabel != null : "fx:id=\"nextBirthdayLabel\" was not injected: check your FXML file 'Kalender.fxml'.";
		assert this.x3 != null : "fx:id=\"x3\" was not injected: check your FXML file 'Kalender.fxml'.";
		assert this.x4 != null : "fx:id=\"x4\" was not injected: check your FXML file 'Kalender.fxml'.";
	}
}
