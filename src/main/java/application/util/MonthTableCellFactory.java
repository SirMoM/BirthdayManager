package application.util;

import application.model.PersonsInAMonthWeek;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MonthTableCellFactory implements Callback<TableColumn<PersonsInAMonthWeek, String>, TableCell<PersonsInAMonthWeek, String>> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd");
    private final DayOfWeek forWhichDay;

    public MonthTableCellFactory(final DayOfWeek forWhichDay) {
        this.forWhichDay = forWhichDay;
    }

    @Override
    public TableCell<PersonsInAMonthWeek, String> call(final TableColumn<PersonsInAMonthWeek, String> param) {
        return new TableCell<PersonsInAMonthWeek, String>() {
            private final VBox graphic = MonthTableCellFactory.buildGraphic("", "");
            private final Text dayText = (Text) this.graphic.getChildren().get(0);
            private final Text birthdaysText = (Text) this.graphic.getChildren().get(1);

            {
                this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                this.setAlignment(Pos.TOP_LEFT);
                this.birthdaysText.wrappingWidthProperty().bind(this.widthProperty().subtract(16));
            }

            @Override
            protected void updateItem(final String item, final boolean empty) {
                super.updateItem(item, empty);

                final PersonsInAMonthWeek monthWeek =
                        this.getTableRow() == null ? null : this.getTableRow().getItem();
                final LocalDate date =
                        monthWeek == null ? null : monthWeek.getDateFor(MonthTableCellFactory.this.forWhichDay);

                if (empty || date == null) {
                    this.setText(null);
                    this.setGraphic(null);
                    return;
                }

                this.dayText.setText(DATE_FORMATTER.format(date));
                this.birthdaysText.setText(item == null ? "" : item);

                final boolean hasBirthdays = item != null && !item.isBlank();
                this.birthdaysText.setManaged(hasBirthdays);
                this.birthdaysText.setVisible(hasBirthdays);

                this.setText(null);
                this.setGraphic(this.graphic);
            }
        };
    }

    static VBox buildGraphic(final String dayTextValue, final String birthdaysTextValue) {
        final VBox container = new VBox(3);
        container.setAlignment(Pos.TOP_LEFT);

        final Text dayText = new Text(dayTextValue);
        dayText.setStyle("-fx-font-size: 10px; -fx-fill: -fx-text-background-color;");
        dayText.setOpacity(0.6);

        final Text birthdaysText = new Text(birthdaysTextValue == null ? "" : birthdaysTextValue);
        birthdaysText.setStyle("-fx-font-size: 12px; -fx-fill: -fx-text-background-color;");

        final boolean hasBirthdays = birthdaysTextValue != null && !birthdaysTextValue.isBlank();
        birthdaysText.setManaged(hasBirthdays);
        birthdaysText.setVisible(hasBirthdays);

        container.getChildren().addAll(dayText, birthdaysText);
        return container;
    }
}
