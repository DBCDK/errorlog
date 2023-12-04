/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.monitoring.errorlog.model;

import dk.dbc.commons.jsonb.JSONBContext;
import dk.dbc.commons.jsonb.JSONBException;
import org.junit.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ErrorLogEntityTest {
    @Test
    public void marshalling() throws JSONBException {
        final JSONBContext jsonbContext = new JSONBContext();
        final OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(
                Instant.now(), ZoneId.systemDefault());
        final Date expectedTimestamp = Date.from(offsetDateTime.toInstant());
        final ErrorLogEntity entity = new ErrorLogEntity()
                .withApp("testApp")
                .withTimeLogged(offsetDateTime);
        assertThat(jsonbContext.marshall(entity),
                is(String.format("{\"app\":\"testApp\",\"timeLogged\":%d,\"timeLoggedZonedDisplay\":\"%s\"}",
                        expectedTimestamp.getTime(), getExpectedTimeLoggedZonedDisplay(expectedTimestamp))));
    }

    private String getExpectedTimeLoggedZonedDisplay(Date timeLogged) {
        final OffsetDateTime offsetDateTime = timeLogged.toInstant().atOffset(ZoneOffset.UTC);
        final ZonedDateTime zonedDateTime = offsetDateTime.atZoneSameInstant(ZoneId.systemDefault());
        return zonedDateTime.toString();
    }
}
