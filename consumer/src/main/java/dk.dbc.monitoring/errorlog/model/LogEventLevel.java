/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.monitoring.errorlog.model;

public enum LogEventLevel {
    FATAL,
    STAT,
    TIMER,
    WARNING,
    /* slf4j compatible begin */
    ERROR,
    WARN,
    INFO,
    DEBUG,
    TRACE
    /* slf4j compatible end */
}
