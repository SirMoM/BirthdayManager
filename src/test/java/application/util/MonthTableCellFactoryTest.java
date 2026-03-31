package application.util;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MonthTableCellFactoryTest {

    @Test
    void buildGraphic_PlacesDateTopLeftWithSubtleStyling() {
        VBox graphic = MonthTableCellFactory.buildGraphic("02", "First Entry\nSecond Entry");

        assertThat(graphic.getAlignment()).isEqualTo(Pos.TOP_LEFT);
        assertThat(graphic.getChildren()).hasSize(2);

        Text dayText = (Text) graphic.getChildren().get(0);
        Text birthdaysText = (Text) graphic.getChildren().get(1);

        assertThat(dayText.getText()).isEqualTo("02");
        assertThat(dayText.getOpacity()).isLessThan(1.0);
        assertThat(dayText.getStyle()).contains("-fx-font-size: 10px");

        assertThat(birthdaysText.getText()).isEqualTo("First Entry\nSecond Entry");
        assertThat(birthdaysText.isManaged()).isTrue();
        assertThat(birthdaysText.isVisible()).isTrue();
    }

    @Test
    void buildGraphic_HidesBirthdayTextWhenNoBirthdayIsPresent() {
        VBox graphic = MonthTableCellFactory.buildGraphic("02", "");

        Text birthdaysText = (Text) graphic.getChildren().get(1);

        assertThat(birthdaysText.getText()).isEmpty();
        assertThat(birthdaysText.isManaged()).isFalse();
        assertThat(birthdaysText.isVisible()).isFalse();
    }
}
