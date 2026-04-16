package application.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
