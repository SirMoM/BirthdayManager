package application.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utility methods for rendering {@link Throwable} instances as text.
 */
public final class ThrowableFormatter {

    private ThrowableFormatter() {
    }

    /**
     * Converts the supplied {@link Throwable} into its full stack trace text.
     *
     * @param throwable the exception or error to render
     * @return the complete stack trace as a string
     */
    public static String toStackTrace(final Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }
}
