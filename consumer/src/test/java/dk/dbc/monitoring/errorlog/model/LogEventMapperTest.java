/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.monitoring.errorlog.model;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class LogEventMapperTest {
    private final LogEventMapper logEventMapper = new LogEventMapper();

    @Test
    public void unmarshallUnknownField() {
        final String json = "{\"host\":\"localhost\", \"hostess\":\"localhostess\"}";
        logEventMapper.unmarshall(json.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void mappings() {
        final String json =
                "{\"level\":\"FATAL\"," +
                 "\"sys_nydus_destination\":\"k8s-metascrum-prod\"," +
                 "\"logger\":\"dk.dbc.dataio.sink.batchexchange.ScheduledBatchFinalizerBean\"," +
                 "\"sys_appid\":\"metascrum-prod/dataio-batch-exchange-sink-boblebad-service\"," +
                 "\"thread\":\"__ejb-thread-pool8\"," +
                 "\"message\":\"Finalized 0 batches\"," +
                 "\"sys_kubernetes_ns\":\"metascrum-prod\"," +
                 "\"version\":1," +
                 "\"sys_stream\":\"stdout\"," +
                 "\"sys_kubernetes_container\":\"dataio-batch-exchange-sink-boblebad-service\"," +
                 "\"@timestamp\":\"2019-05-20T08:48:00.001+02:00\"," +
                 "\"sys_env\":\"kubernetes\"," +
                 "\"HOSTNAME\":\"dataio-batch-exchange-sink-boblebad-service-5545b64558-gcm6t\"," +
                 "\"level_value\":20000," +
                 "\"sys_host\":\"container-p13\"," +
                 "\"sys_kubernetes\":{" +
                        "\"container\":{" +
                            "\"name\":\"dataio-batch-exchange-sink-boblebad-service\"}," +
                        "\"labels\":{" +
                            "\"network-policy-http-outgoing\":\"yes\"," +
                            "\"network-policy-http-incoming\":\"yes\"," +
                            "\"network-policy-postgres-outgoing\":\"yes\"," +
                            "\"network-policy-payara-incoming\":\"yes\"," +
                            "\"network-policy-mq-outgoing\":\"yes\"," +
                            "\"app_dbc_dk/team\": \"metascrum\"," +
                            "\"pod-template-hash\":\"5545b64558\"" +
                        "}," +
                        "\"namespace\":\"metascrum-prod\"," +
                        "\"pod\":{" +
                            "\"name\":\"dataio-batch-exchange-sink-boblebad-service-5545b64558-gcm6t\"," +
                            "\"uid\":\"5b6b4092-77c8-11e9-b705-48df373baf30\"}," +
                        "\"replicaset\":{" +
                            "\"name\":\"dataio-batch-exchange-sink-boblebad-service-5545b64558\"}" +
                 "}," +
                 "\"sys_taskid\":\"metascrum-prod/dataio-batch-exchange-sink-boblebad-service-5545b64558-gcm6t\"," +
                 "\"timestamp\":\"2019-05-20T08:48:00.001+02:00\"}";
        final LogEvent logEvent = logEventMapper.unmarshall(json.getBytes(StandardCharsets.UTF_8));
        assertThat("team",logEvent.getKubernetes().getTeam(), is("metascrum"));
        assertThat("marshall->unmarshall",
                logEventMapper.unmarshall(logEventMapper.marshall(logEvent).getBytes(StandardCharsets.UTF_8)),
                is(logEvent));
    }

    @Test
    public void oldFormat() {
        final String json =
                "{\"level\":\"INFO\",\"sys_nydus_destination\":\"k8s-metascrum-prod\",\"logger\":\"dk.dbc.rawrepo.RecordServiceConnectorFactory\",\"sys_appid\":\"metascrum-prod/rr-harvester-service\",\"thread\":\"__ejb-thread-pool7\",\"message\":\"Creating RecordServiceConnector for: http://rawrepo-record-service.cisterne.svc.cloud.dbc.dk\",\"sys_kubernetes_ns\":\"metascrum-prod\",\"version\":\"1\",\"mdc\":{\"HARVESTER_ID\":\"Cisterne - basis-dbckat\"},\"sys_stream\":\"stdout\",\"sys_kubernetes_container\":\"rr-harvester-service\",\"@timestamp\":\"2020-11-17T07:41:30.209+01:00\",\"sys_env\":\"kubernetes\",\"HOSTNAME\":\"dataio-rr-harvester-service-684765fb98-6r5rc\",\"level_value\":20000,\"sys_host\":\"container-p21\",\"sys_kubernetes\":{\"container\":{\"name\":\"rr-harvester-service\"},\"labels\":{\"network-policy-postgres-outgoing\":\"yes\",\"network-policy-solr7-outgoing\":\"yes\",\"network-policy-solr8-outgoing\":\"yes\",\"app\":{\"dbc\":{\"dk/team\":\"metascrum\"},\"value\":\"rr-harvester-service\",\"kubernetes\":{\"io/name\":\"rr-harvester\",\"io/part-of\":\"dataio\",\"io/component\":\"service\"}},\"network-policy-http-outgoing\":\"yes\",\"promaas\":{\"enable\":\"true\"},\"pod-template-hash\":\"684765fb98\",\"network-policy-http-incoming\":\"yes\"},\"namespace\":\"metascrum-prod\",\"pod\":{\"name\":\"dataio-rr-harvester-service-684765fb98-6r5rc\",\"uid\":\"6820d7b1-10cb-49dc-b60a-91a4e3194d0c\"},\"replicaset\":{\"name\":\"dataio-rr-harvester-service-684765fb98\"}},\"sys_taskid\":\"metascrum-prod/dataio-rr-harvester-service-684765fb98-6r5rc\",\"timestamp\":\"2020-11-17T07:41:30.209+01:00\"}";

        final LogEvent logEvent = logEventMapper.unmarshall(json.getBytes(StandardCharsets.UTF_8));
        assertThat("team",logEvent.getKubernetes().getTeam(), is(nullValue()));
    }

    @Test
    public void nonSlf4jLogLevel() {
        final String json =
                "{\"level\":\"WARNING\"," +
                "\"sys_nydus_destination\":\"k8s-metascrum-prod\"," +
                "\"message\":\"a message\"," +
                "\"@timestamp\":\"2019-05-20T08:48:00.001+02:00\"}";
        final LogEvent logEvent = logEventMapper.unmarshall(json.getBytes(StandardCharsets.UTF_8));
        assertThat("marshall->unmarshall",
                logEventMapper.unmarshall(logEventMapper.marshall(logEvent).getBytes(StandardCharsets.UTF_8)),
                is(logEvent));
    }
}