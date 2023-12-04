/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.monitoring.errorlog.model;

import org.junit.Test;
import org.postgresql.util.PGobject;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ErrorLogEntityContextConverterTest {
    private final ErrorLogEntityContextConverter converter = new ErrorLogEntityContextConverter();

    @Test
    public void convertToDatabaseColumnWhenContextArgIsNull() {
        final PGobject pgObject = converter.convertToDatabaseColumn(null);
        assertThat("PGobject", pgObject, is(notNullValue()));
        assertThat("PGobject type", pgObject.getType(), is("jsonb"));
        assertThat("PGobject value", pgObject.getValue(), is("{}"));
    }

    @Test
    public void convertToDatabaseColumnWhenContextArgIsEmpty() {
        final PGobject pgObject = converter.convertToDatabaseColumn(Collections.emptyMap());
        assertThat("PGobject", pgObject, is(notNullValue()));
        assertThat("PGobject type", pgObject.getType(), is("jsonb"));
        assertThat("PGobject value", pgObject.getValue(), is("{}"));
    }

    @Test
    public void convertToDatabaseColumn() {
        final Map<String, String> context = new HashMap<>(3);
        context.put("key1", "value1");
        context.put("key2", "value2");
        context.put("key3", "value3");
        final PGobject pgObject = converter.convertToDatabaseColumn(context);
        assertThat("PGobject", pgObject, is(notNullValue()));
        assertThat("PGobject type", pgObject.getType(), is("jsonb"));
        assertThat("PGobject value", pgObject.getValue(),
                is("{\"key1\":\"value1\",\"key2\":\"value2\",\"key3\":\"value3\"}"));
    }

    @Test
    public void toEntityAttributeWhenPgObjectArgIsNull() {
        final Map<String, String> context = converter.convertToEntityAttribute(null);
        assertThat("context", context, is(notNullValue()));
        assertThat("context is empty", context.isEmpty(), is(true));
    }

    @Test
    public void toEntityAttribute() throws SQLException {
        final Map<String, String> expectedContext = new HashMap<>(3);
        expectedContext.put("key1", "value1");
        expectedContext.put("key2", "value2");
        expectedContext.put("key3", "value3");
        final PGobject pgObject = new PGobject();
        pgObject.setValue("{\"key1\":\"value1\",\"key2\":\"value2\",\"key3\":\"value3\"}");

        final Map<String, String> context = converter.convertToEntityAttribute(pgObject);
        assertThat("context", context, is(notNullValue()));
        assertThat("context value", context, is(expectedContext));
    }
}