package application.processes;

import java.util.concurrent.CountDownLatch;
import javafx.embed.swing.JFXPanel;
import javax.swing.*;
import org.junit.jupiter.api.BeforeEach;

public class TaskTest {
  private boolean jfxIsSetup;

  @BeforeEach
  void setup() {
    if (!jfxIsSetup) {
      setupJavaFX();
      jfxIsSetup = true;
    }
  }

  protected void setupJavaFX() throws RuntimeException {
    final CountDownLatch latch = new CountDownLatch(1);
    SwingUtilities.invokeLater(
        () -> {
          new JFXPanel(); // initializes JavaFX environment
          latch.countDown();
        });

    try {
      latch.await();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
