package application.processes;

import java.time.LocalTime;
import javafx.concurrent.Task;

public class UpdateSublistsPeriodically extends Task<Boolean> {

  @Override
  protected Boolean call() throws Exception {
    LocalTime now = LocalTime.now();
    int hour = now.getHour();
    int minute = now.getMinute();

    return hour == 0 && minute == 1;
  }
}
