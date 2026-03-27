package application.processes;

import application.model.PersonManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SaveBirthdaysToFileTaskTest {

    @AfterEach
    void tearDown() {
        PersonManager.getInstance().getPersons().clear();
    }

    @Test
    void call_ReturnsFalseWhenNoSaveFileWasSelected() {
        SaveBirthdaysToFileTask task = new SaveBirthdaysToFileTask(null);

        assertThat(task.call()).isFalse();
    }
}
