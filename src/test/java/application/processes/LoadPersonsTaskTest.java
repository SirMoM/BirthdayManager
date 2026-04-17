package application.processes;

import static org.assertj.core.api.Assertions.assertThat;

import application.model.Person;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LoadPersonsTaskTest {

  @TempDir Path tempDir;

  @Test
  void call_ParsesCsvStartingWithUtf8Bom() throws Exception {
    Path csvFile = tempDir.resolve("birthdays.csv");
    Files.writeString(csvFile, "\uFEFF03.01.1964,Grassmann;Rainer;\n", StandardCharsets.UTF_8);

    LoadPersonsTask.Result result = new LoadPersonsTask(csvFile.toFile(), true).call();

    assertThat(result.getErrors()).isEmpty();
    assertThat(result.getPersons()).hasSize(1);

    Person person = result.getPersons().get(0);
    assertThat(person.getBirthday()).isEqualTo(LocalDate.of(1964, 1, 3));
    assertThat(person.getName()).isEqualTo("Grassmann");
    assertThat(person.getSurname()).isEqualTo("Rainer");
    assertThat(person.getMisc()).isNull();
  }
}
