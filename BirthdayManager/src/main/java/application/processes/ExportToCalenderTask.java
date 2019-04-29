/**
 * 
 */
package application.processes;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;

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
import javafx.concurrent.Task;

/**
 * @author Noah Ruben
 *
 */
public class ExportToCalenderTask extends Task<Boolean> {

	ICalendar ical;
	ICalWriter writer;
	Boolean succsessfull = true;

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
		createCalender();
		if (writer != null && ical != null) {
			writer.write(ical);
		} else {
			succsessfull = false;
		}
		close();
		return succsessfull;
	}

	private void close() {
		try {
			writer.close();
		} catch (IOException ioException) {
			succsessfull = false;
		}
	}

	private void createCalender() {
		int size = PersonManager.getInstance().getPersonDB().size();
		for (int i = 0; i < size; i++) {
			Person person = PersonManager.getInstance().getPersonDB().get(i);
			VEvent event = new VEvent();
			Summary summary = event.setSummary(person.namesToString());
			summary.setLanguage("de-DE");
			Date start = Date
					.from(person.getBirthday().withYear(2019).atStartOfDay(ZoneId.systemDefault()).toInstant());
			event.setDateStart(start, false);
			event.setClassification(Classification.PUBLIC);

			if (new Boolean(PropertyManager.getProperty(PropertyFields.EXPORT_WITH_ALARM))) {
				Duration dayBefore = Duration.builder().prior(true).days(1).build();
				Duration onBirthday = Duration.builder().prior(false).hours(8).build();
				event.addAlarm(new VAlarm(Action.display(), new Trigger(dayBefore, Related.START)));
				event.addAlarm(new VAlarm(Action.display(), new Trigger(onBirthday, Related.START)));
			}

			this.ical.addEvent(event);
			updateProgress(i, size);
		}

	}

}
