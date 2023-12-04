/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.monitoring.errorlog.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.Set;

@ApplicationPath("/")
public class ErrorLogConsumer extends Application {
    private static final Set<Class<?>> classes = Set.of(Status.class);

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
