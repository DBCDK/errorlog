/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.monitoring.errorlog;

import dk.dbc.monitoring.errorlog.model.LogEvent;
import org.junit.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ErrorCauseTest {
    @Test
    public void noStacktrace() {
        assertThat(ErrorCause.of(new LogEvent()), is(Optional.empty()));
    }

    @Test
    public void fromComplexJavaStacktrace() {
        final LogEvent logEvent = toLogEvent(
                "src/test/resources/java-complex-stacktrace.txt");
        assertThat(ErrorCause.of(logEvent), is(Optional.of(
                new ErrorCause("java.lang.IllegalStateException: Node failed to start!"))));
    }

    @Test
    public void fromSimpleJavaStacktrace() {
        final LogEvent logEvent = toLogEvent(
                "src/test/resources/java-simple-stacktrace.txt");
        assertThat(ErrorCause.of(logEvent), is(Optional.of(
                new ErrorCause("javax.ejb.EJBException: javax.ejb.CreateException: " +
                        "Initialization failed for Singleton InitializerBean"))));
    }

    @Test
    public void fromPythonStacktrace() {
        final LogEvent logEvent = toLogEvent(
                "src/test/resources/python-stacktrace.txt");
        assertThat(ErrorCause.of(logEvent), is(Optional.of(
                new ErrorCause("Exception: Bad value"))));
    }

    private static LogEvent toLogEvent(String resource) {
        try {
            final LogEvent logEvent = new LogEvent();
            logEvent.setStacktrace(new String(
                    Files.readAllBytes(Paths.get(resource)), StandardCharsets.UTF_8));
            return logEvent;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}