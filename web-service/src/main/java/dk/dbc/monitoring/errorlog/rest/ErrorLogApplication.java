package dk.dbc.monitoring.errorlog.rest;

import dk.dbc.monitoring.errorlog.ErrorLogService;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class ErrorLogApplication extends Application {
    private static final Set<Class<?>> classes = Set.of(ErrorLogService.class, HealthChecks.class);

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

}
