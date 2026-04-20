/** */
package application.controller;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public abstract class Controller implements Initializable {

  private final MainController mainController;

  /**
   * @param mainController The "MainController" for this application
   */
  protected Controller(final MainController mainController) {
    this.mainController = mainController;
  }

  /**
   * @return the MainController
   */
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

  /** Called to set the focus of the scene. */
  public abstract void placeFocus();

  protected final void bindExplicitTabOrder(final Node... nodes) {
    for (int i = 0; i < nodes.length; i++) {
      final int currentIndex = i;
      nodes[i].setFocusTraversable(true);
      nodes[i].addEventFilter(
          KeyEvent.KEY_PRESSED,
          event -> {
            if (!shouldHandleExplicitTabTraversal(event)) {
              return;
            }

            final boolean[] availableNodes = new boolean[nodes.length];
            for (int index = 0; index < nodes.length; index++) {
              availableNodes[index] = nodes[index].isVisible() && !nodes[index].isDisabled();
            }
            final int nextIndex =
                findNextTabOrderIndex(currentIndex, event.isShiftDown(), availableNodes);
            nodes[nextIndex].requestFocus();
            event.consume();
          });
    }
  }

  static int findNextTabOrderIndex(
      final int currentIndex, final boolean reverse, final boolean[] availableNodes) {
    int nextIndex = currentIndex;
    do {
      nextIndex =
          reverse
              ? (nextIndex - 1 + availableNodes.length) % availableNodes.length
              : (nextIndex + 1) % availableNodes.length;
    } while (!availableNodes[nextIndex]);
    return nextIndex;
  }

  private static boolean shouldHandleExplicitTabTraversal(final KeyEvent event) {
    return event.getCode() == KeyCode.TAB
        && !event.isAltDown()
        && !event.isControlDown()
        && !event.isMetaDown();
  }
}
