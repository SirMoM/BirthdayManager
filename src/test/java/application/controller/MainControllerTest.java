package application.controller;

import javafx.scene.control.ButtonBar;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MainControllerTest {

    @Test
    void determineCloseAction_SavesToExistingFileWhenConfirmed() {
        assertThat(MainController.determineCloseAction(true, ButtonBar.ButtonData.YES))
                .isEqualTo(MainController.CloseAction.SAVE_TO_EXISTING_FILE);
    }

    @Test
    void determineCloseAction_AsksForAFileWhenConfirmedWithoutSaveTarget() {
        assertThat(MainController.determineCloseAction(false, ButtonBar.ButtonData.YES))
                .isEqualTo(MainController.CloseAction.ASK_FOR_SAVE_FILE);
    }

    @Test
    void determineCloseAction_ExitsWithoutSavingWhenDeclined() {
        assertThat(MainController.determineCloseAction(true, ButtonBar.ButtonData.NO))
                .isEqualTo(MainController.CloseAction.EXIT_WITHOUT_SAVING);
    }
}
