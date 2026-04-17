package application.util.localisation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.api.Test;

class LangResourceManagerTest {

  @Test
  void resolveLocale_StoredLocaleCodeMapsToSupportedLocale() {
    assertThat(LangResourceManager.resolveLocale("en_GB")).isEqualTo(Locale.UK);
    assertThat(LangResourceManager.resolveLocale("de_DE")).isEqualTo(Locale.GERMANY);
    assertThat(LangResourceManager.resolveLocale("en-GB")).isEqualTo(Locale.UK);
  }

  @Test
  void resolveLocale_UnsupportedOrEmptyLocaleFallsBackToUk() {
    assertThat(LangResourceManager.resolveLocale(null)).isEqualTo(Locale.UK);
    assertThat(LangResourceManager.resolveLocale("")).isEqualTo(Locale.UK);
    assertThat(LangResourceManager.resolveLocale("fr_FR")).isEqualTo(Locale.UK);
  }
}
