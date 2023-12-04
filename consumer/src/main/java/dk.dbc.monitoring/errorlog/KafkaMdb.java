/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.monitoring.errorlog;

import dk.dbc.monitoring.errorlog.model.LogEvent;
import dk.dbc.monitoring.errorlog.model.LogEventLevel;
import dk.dbc.monitoring.errorlog.model.LogEventMapper;
import fish.payara.cloud.connectors.kafka.api.KafkaListener;
import fish.payara.cloud.connectors.kafka.api.OnRecord;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "clientId",
            propertyValue = "${ENV=KAFKA_CLIENT_ID}"),
    @ActivationConfigProperty(propertyName = "groupIdConfig",
            propertyValue = "${ENV=KAFKA_GROUP_ID}"),
    @ActivationConfigProperty(propertyName = "topics",
            propertyValue = "${ENV=KAFKA_TOPICS}"),
    @ActivationConfigProperty(propertyName = "bootstrapServersConfig",
            propertyValue = "${ENV=KAFKA_HOSTS}"),
    @ActivationConfigProperty(propertyName = "EnableAutoCommit",
            propertyValue = "false"),
    @ActivationConfigProperty(propertyName = "autoOffsetReset",
            propertyValue = "latest"),
    @ActivationConfigProperty(propertyName = "retryBackoff",
            propertyValue = "1000"),
    @ActivationConfigProperty(propertyName = "keyDeserializer",
            propertyValue = "org.apache.kafka.common.serialization.StringDeserializer"),
    @ActivationConfigProperty(propertyName = "valueDeserializer",
            propertyValue = "org.apache.kafka.common.serialization.StringDeserializer"),
    @ActivationConfigProperty(propertyName = "pollInterval",
            propertyValue = "3000"),
})
public class KafkaMdb implements KafkaListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMdb.class);
    private static final LogEventMapper LOG_EVENT_MAPPER = new LogEventMapper();
    private static final int TICK_INTERVAL = 250_000;

    @EJB ErrorLog errorLog;

    @OnRecord
    public void onMessage(ConsumerRecord record) {
        final LogEvent logEvent = LOG_EVENT_MAPPER.unmarshall(
                ((String) record.value()).getBytes(StandardCharsets.UTF_8));

        if (isSuitableLogEvent(logEvent)) {
            errorLog.persist(logEvent.toErrorLogEntity());
        }

        if (record.offset() % TICK_INTERVAL == 0) {
            LOGGER.info("consumed offset {} with timestamp {}", record.offset(), logEvent.getTimestamp());
        }
    }

    private boolean isSuitableLogEvent(LogEvent logEvent) {
        return logEvent.getLevel() != null
                && isSuitableLogLevel(logEvent.getLevel())
                && logEvent.getKubernetes() != null
                && logEvent.getKubernetes().getTeam() != null;
    }

    private boolean isSuitableLogLevel(LogEventLevel level) {
        return level == LogEventLevel.ERROR || level == LogEventLevel.FATAL;
    }
}
