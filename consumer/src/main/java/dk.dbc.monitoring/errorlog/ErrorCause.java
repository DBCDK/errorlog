/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.monitoring.errorlog;

import dk.dbc.monitoring.errorlog.model.LogEvent;

import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

/**
 * This class represents the cause of an error log as
 * extracted from the string representation of a Java
 * or python stacktrace.
 */
public final class ErrorCause {
    public static Optional<ErrorCause> of(LogEvent logEvent) {
        if (logEvent.getStacktrace() == null) {
            return Optional.empty();
        }
        return Optional.of(new ErrorCause(
                extractCauseFromStacktrace(logEvent.getStacktrace())));
    }

    private static String extractCauseFromStacktrace(String stacktrace) {
        try (final Scanner scanner = new Scanner(stacktrace)) {
            final String firstLine = scanner.nextLine();
            String line = firstLine;
            String cause = null;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (line.startsWith("Caused by: ")) {
                    cause = line.substring(11);
                }
            }
            if (cause == null) {
                if (firstLine.startsWith("Traceback")) {
                    // Looks like python
                    return line;
                }
                return firstLine;
            }
            return cause;
        }
    }

    private final String cause;

    ErrorCause(String cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return cause;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ErrorCause that = (ErrorCause) o;

        return Objects.equals(cause, that.cause);
    }

    @Override
    public int hashCode() {
        return cause != null ? cause.hashCode() : 0;
    }
}
