/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.monitoring.errorlog.model;

import dk.dbc.commons.jsonb.JSONBContext;
import dk.dbc.commons.jsonb.JSONBException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.postgresql.util.PGobject;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

@Converter
public class ErrorLogEntityContextConverter implements AttributeConverter<Map<String, String>, PGobject> {
    private static final JSONBContext JSONB_CONTEXT = new JSONBContext();

    @Override
    public PGobject convertToDatabaseColumn(Map<String, String> context) {
        if (context == null) {
            context = Collections.emptyMap();
        }
        final PGobject pgObject = new PGobject();
        pgObject.setType("jsonb");
        try {
            pgObject.setValue(JSONB_CONTEXT.marshall(context));
        } catch (SQLException | JSONBException e) {
            throw new IllegalStateException(e);
        }
        return pgObject;
    }

    @Override
    public Map<String, String> convertToEntityAttribute(PGobject pgObject) {
        if (pgObject == null) {
            return Collections.emptyMap();
        }
        try {
            return JSONB_CONTEXT.unmarshall(pgObject.getValue(), JSONB_CONTEXT.getTypeFactory()
                    .constructMapType(TreeMap.class, String.class, String.class));
        } catch (JSONBException e) {
            throw new IllegalStateException(e);
        }
    }
}
