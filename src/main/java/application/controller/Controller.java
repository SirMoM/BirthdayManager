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

    /** @param mainController The "MainController" for this application */
    protected Controller(final MainController mainController) {
        this.mainController = mainController;
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

    /**
     * Called to set the focus of the scene.
     */
    public abstract void placeFocus();
}
