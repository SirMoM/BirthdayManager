/**
 *
 */
package application.processes;

import application.model.Person;
import application.model.PersonManager;
import application.util.PropertyFields;
import application.util.PropertyManager;
import biweekly.ICalVersion;
import biweekly.ICalendar;
import biweekly.component.VAlarm;
import biweekly.component.VEvent;
import biweekly.io.text.ICalWriter;
import biweekly.parameter.Related;
import biweekly.property.Action;
import biweekly.property.Classification;
import biweekly.property.Summary;
import biweekly.property.Trigger;
import biweekly.util.Duration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

/** @author Noah Ruben */
public class ExportToCalenderTask extends PersonTasks<Boolean> {

    ICalendar ical;
    ICalWriter writer;
    Boolean successful = true;
    List<Person> persons;

    public ExportToCalenderTask(File file) throws IOException {
        this.ical = new ICalendar();
        writer = new ICalWriter(file, ICalVersion.V2_0);
    }

    /*
     * (non-Javadoc)
     *
     * @see javafx.concurrent.Task#call()
     */
    @Override
    protected Boolean call() throws Exception {
        persons = getPersons();
        createCalender();
        if (writer != null && ical != null) {
            writer.write(ical);
        } else {
            successful = false;
        }
        close();
        return successful;
    }

    private void close() {
        try {
            writer.close();
        } catch (IOException ioException) {
            successful = false;
        }
    }

    private void createCalender() {
        int size = PersonManager.getInstance().getPersons().size();
        IntStream.range(0, persons.size()).forEach(i -> {
            Person person = PersonManager.getInstance().getPersons().get(i);
            VEvent event = new VEvent();
            Summary summary = event.setSummary(person.namesToString());
            summary.setLanguage("de-DE");
            Date start = Date.from(person.getBirthday().withYear(LocalDate.now().getYear()).atStartOfDay(ZoneId.systemDefault()).toInstant());
            event.setDateStart(start, false);
            event.setClassification(Classification.PUBLIC);
            if (Boolean.parseBoolean(PropertyManager.getProperty(PropertyFields.EXPORT_WITH_ALARM))) {
                Duration dayBefore = Duration.builder().prior(true).days(1).build();
                Duration onBirthday = Duration.builder().prior(false).hours(8).build();
                event.addAlarm(new VAlarm(Action.display(), new Trigger(dayBefore, Related.START)));
                event.addAlarm(new VAlarm(Action.display(), new Trigger(onBirthday, Related.START)));
            }
            this.ical.addEvent(event);
            updateProgress(i, size);
        });
    }
}
