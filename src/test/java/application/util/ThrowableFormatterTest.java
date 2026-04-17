package application.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ThrowableFormatterTest {

  @Test
  void toStackTrace_IncludesExceptionTypeAndMessage() {
    IllegalStateException throwable = new IllegalStateException("boom");

    String stackTrace = ThrowableFormatter.toStackTrace(throwable);

    assertThat(stackTrace).contains("IllegalStateException");
    assertThat(stackTrace).contains("boom");
    assertThat(stackTrace).contains("toStackTrace_IncludesExceptionTypeAndMessage");
  }
}
