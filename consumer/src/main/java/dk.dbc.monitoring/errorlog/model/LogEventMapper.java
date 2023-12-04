/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.monitoring.errorlog.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;

public class LogEventMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogEventMapper.class);

    private final ObjectMapper objectMapper;

    public LogEventMapper() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
    }

    public String marshall(Object object) {
        final StringWriter stringWriter = new StringWriter();
        try {
            objectMapper.writeValue(stringWriter, object);
        } catch (RuntimeException | IOException e) {
            throw new IllegalStateException("Exception caught when trying to marshall to JSON", e);
        }
        return stringWriter.toString();
    }

    public LogEvent unmarshall(byte[] json) {
        try {
            return objectMapper.readValue(json, LogEvent.class);
        } catch (IOException e) {
            LOGGER.warn("Unable to unmarshall to LogEvent", e);
            return new LogEvent();
        }
    }
}
