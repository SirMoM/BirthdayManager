/**
 *
 */
package application.controller;

import javafx.fxml.Initializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public abstract class Controller implements Initializable {

    final Logger LOG;

    private final MainController mainController;

    /** @param mainController The "MainController" for this application */
    protected Controller(final MainController mainController) {
        this.mainController = mainController;
        this.LOG = LogManager.getLogger(this.getClass().getName());
    }

    /** @return the MainController */
    public MainController getMainController() {
        return this.mainController;
    }

    /**
     * Called if the Localisation of the view needs to be updated.
     *
     * <p>Before calling this the right locale needs to be set in {@link
     * application.util.localisation.LangResourceManager}
     */
    public abstract void updateLocalisation();
}
