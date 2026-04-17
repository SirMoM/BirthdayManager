package application.model;

import static org.assertj.core.api.Assertions.assertThat;

import application.processes.CheckMissedBirthdays;
import application.processes.UpdateBirthdaysThisMonthTask;
import application.processes.UpdateBirthdaysThisWeekTask;
import application.processes.UpdateNextBirthdaysTask;
import application.processes.UpdateRecentBirthdaysTask;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import org.junit.jupiter.api.Test;

class SessionInfosTest {

  private static final class RecordingSessionInfos extends SessionInfos {
    private final List<Class<?>> startedTasks = new ArrayList<>();

    private RecordingSessionInfos() {
      super(null);
    }

    @Override
    void startTask(Task<?> task) {
      startedTasks.add(task.getClass());
    }
  }

  @Test
  void updateSubLists_StartsMissedBirthdaysCheckOnlyOncePerSession() {
    RecordingSessionInfos sessionInfos = new RecordingSessionInfos();

    sessionInfos.updateSubLists();
    sessionInfos.updateSubLists();

    assertThat(sessionInfos.startedTasks).filteredOn(CheckMissedBirthdays.class::equals).hasSize(1);
    assertThat(sessionInfos.startedTasks)
        .filteredOn(UpdateNextBirthdaysTask.class::equals)
        .hasSize(2);
    assertThat(sessionInfos.startedTasks)
        .filteredOn(UpdateRecentBirthdaysTask.class::equals)
        .hasSize(2);
    assertThat(sessionInfos.startedTasks)
        .filteredOn(UpdateBirthdaysThisWeekTask.class::equals)
        .hasSize(2);
    assertThat(sessionInfos.startedTasks)
        .filteredOn(UpdateBirthdaysThisMonthTask.class::equals)
        .hasSize(2);
  }
}
