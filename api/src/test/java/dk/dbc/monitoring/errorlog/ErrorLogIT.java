/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.monitoring.errorlog;

import dk.dbc.monitoring.errorlog.model.ErrorLogAppView;
import dk.dbc.monitoring.errorlog.model.ErrorLogEntity;
import dk.dbc.monitoring.errorlog.model.ErrorLogSummary;
import dk.dbc.monitoring.errorlog.model.QueryParameters;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ErrorLogIT extends IntegrationTest {
    ErrorLog errorLog;

    @Before
    public void createErrorLog() {
        errorLog = new ErrorLog();
        errorLog.entityManager = entityManager;
    }

    @Test
    public void persist() {
        final ErrorLogEntity entity = new ErrorLogEntity()
                .withApp("testApp")
                .withNamespace("testNamespace")
                .withHost("testHost")
                .withContainer("testContainer")
                .withMessage("testMessage")
                .withTimeLogged(OffsetDateTime.ofInstant(
                        Instant.now(), ZoneId.systemDefault()));
        transaction.run(() -> {
            errorLog.persist(entity);
            entityManager.flush();
            entityManager.refresh(entity);
        });

        assertThat(entity.getId(), is(notNullValue()));
    }

    @Test
    public void get() {
        final ErrorLogEntity entity = transaction.run(() -> errorLog.get(1).orElse(null));

        assertThat("ErrorLogEntity", entity, is(notNullValue()));
        assertThat("ErrorLogEntity.message", entity.getMessage(), is("app_1_message_1"));
    }

    @Test
    public void getWhenIdDoesNotExist() {
        final ErrorLogEntity entity = transaction.run(() -> errorLog.get(4242).orElse(null));

        assertThat(entity, is(nullValue()));
    }

    @Test
    public void getSummary() {
        final List<ErrorLogSummary> summary = errorLog.getSummary(new QueryParameters()
                .withTeam("teamX")
                .withFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .withUntil(Instant.now().plus(1, ChronoUnit.DAYS)));

        assertThat("number of summary elements", summary.size(), is(11));

        assertThat("(0) kind", summary.get(0).getKind(), is(ErrorLogSummary.Kind.TOTAL));
        assertThat("(0) count", summary.get(0).getCount(), is(6L));

        assertThat("(1) kind", summary.get(1).getKind(), is(ErrorLogSummary.Kind.NAMESPACE));
        assertThat("(1) namespace", summary.get(1).getNamespace(), is("prod_B"));
        assertThat("(1) count", summary.get(1).getCount(), is(1L));

        assertThat("(2) kind", summary.get(2).getKind(), is(ErrorLogSummary.Kind.APP));
        assertThat("(2) namespace", summary.get(2).getNamespace(), is("prod_B"));
        assertThat("(2) app", summary.get(2).getApp(), is("app_1"));
        assertThat("(2) count", summary.get(2).getCount(), is(1L));

        assertThat("(3) kind", summary.get(3).getKind(), is(ErrorLogSummary.Kind.CAUSE));
        assertThat("(3) namespace", summary.get(3).getNamespace(), is("prod_B"));
        assertThat("(3) app", summary.get(3).getApp(), is("app_1"));
        assertThat("(3) cause", summary.get(3).getCause(), is("EJBException"));
        assertThat("(3) count", summary.get(3).getCount(), is(1L));

        assertThat("(4) kind", summary.get(4).getKind(), is(ErrorLogSummary.Kind.NAMESPACE));
        assertThat("(4) namespace", summary.get(4).getNamespace(), is("prod_A"));
        assertThat("(4) count", summary.get(4).getCount(), is(5L));

        assertThat("(5) kind", summary.get(5).getKind(), is(ErrorLogSummary.Kind.APP));
        assertThat("(5) namespace", summary.get(5).getNamespace(), is("prod_A"));
        assertThat("(5) app", summary.get(5).getApp(), is("app_2"));
        assertThat("(5) count", summary.get(5).getCount(), is(1L));

        assertThat("(6) kind", summary.get(6).getKind(), is(ErrorLogSummary.Kind.CAUSE));
        assertThat("(6) namespace", summary.get(6).getNamespace(), is("prod_A"));
        assertThat("(6) app", summary.get(6).getApp(), is("app_2"));
        assertThat("(6) cause", summary.get(6).getCause(), is("EJBException"));
        assertThat("(6) count", summary.get(6).getCount(), is(1L));

        assertThat("(7) kind", summary.get(7).getKind(), is(ErrorLogSummary.Kind.APP));
        assertThat("(7) namespace", summary.get(7).getNamespace(), is("prod_A"));
        assertThat("(7) app", summary.get(7).getApp(), is("app_1"));
        assertThat("(7) count", summary.get(7).getCount(), is(4L));

        assertThat("(8) kind", summary.get(8).getKind(), is(ErrorLogSummary.Kind.CAUSE));
        assertThat("(8) namespace", summary.get(8).getNamespace(), is("prod_A"));
        assertThat("(8) app", summary.get(8).getApp(), is("app_1"));
        assertThat("(8) cause", summary.get(8).getCause(), is("NullPointerException"));
        assertThat("(8) count", summary.get(8).getCount(), is(1L));

        assertThat("(9) kind", summary.get(9).getKind(), is(ErrorLogSummary.Kind.CAUSE));
        assertThat("(9) namespace", summary.get(9).getNamespace(), is("prod_A"));
        assertThat("(9) app", summary.get(9).getApp(), is("app_1"));
        assertThat("(9) cause", summary.get(9).getCause(), is("EJBException"));
        assertThat("(9) count", summary.get(9).getCount(), is(2L));

        assertThat("(10) kind", summary.get(10).getKind(), is(ErrorLogSummary.Kind.CAUSE));
        assertThat("(10) namespace", summary.get(10).getNamespace(), is("prod_A"));
        assertThat("(10) app", summary.get(10).getApp(), is("app_1"));
        assertThat("(10) cause", summary.get(10).getCause(), is(""));
        assertThat("(10) count", summary.get(10).getCount(), is(1L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSummaryMissingTeamQueryParam() {
        errorLog.getSummary(new QueryParameters()
                .withFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .withUntil(Instant.now().plus(1, ChronoUnit.DAYS)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSummaryMissingFromQueryParam() {
        errorLog.getSummary(new QueryParameters()
                .withTeam("teamX")
                .withUntil(Instant.now().plus(1, ChronoUnit.DAYS)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSummaryMissingUntilQueryParam() {
        errorLog.getSummary(new QueryParameters()
                .withTeam("teamX")
                .withFrom(Instant.now().minus(1, ChronoUnit.DAYS)));
    }

    @Test
    public void getSummaryFilterOnTimestamp() {
        final List<ErrorLogSummary> summary = errorLog.getSummary(new QueryParameters()
                .withTeam("teamX")
                .withFrom(Instant.now().plus(1, ChronoUnit.DAYS))
                .withUntil(Instant.now().plus(2, ChronoUnit.DAYS)));

        assertThat("number of summary elements", summary.size(), is(1));
        assertThat("(0) kind", summary.get(0).getKind(), is(ErrorLogSummary.Kind.TOTAL));
        assertThat("(0) count", summary.get(0).getCount(), is(0L));
    }

    @Test
    public void getAppView() {
        final ErrorLogAppView appView = errorLog.getAppView(new QueryParameters()
                .withNamespace("prod_A")
                .withApp("app_1")
                .withTeam("teamX")
                .withFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .withUntil(Instant.now().plus(1, ChronoUnit.DAYS))
                .withLimit(2));

        assertThat("size of app view", appView.getSize(), is(4L));
        assertThat("limit", appView.getEntities().size(), is(2));
        assertThat("offset", appView.getFirstPosition(), is(0L));
        assertThat("1st entity ID", appView.getEntities().get(0).getId(), is(1));
        assertThat("2nd entity ID", appView.getEntities().get(1).getId(), is(5));
    }

    @Test
    public void getAppViewPagination() {
        final ErrorLogAppView appView = errorLog.getAppView(new QueryParameters()
                .withNamespace("prod_A")
                .withApp("app_1")
                .withTeam("teamX")
                .withFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .withUntil(Instant.now().plus(1, ChronoUnit.DAYS))
                .withOffset(2)
                .withLimit(2));

        assertThat("size of app view", appView.getSize(), is(4L));
        assertThat("limit", appView.getEntities().size(), is(2));
        assertThat("offset", appView.getFirstPosition(), is(2L));
        assertThat("1st entity ID", appView.getEntities().get(0).getId(), is(6));
        assertThat("2nd entity ID", appView.getEntities().get(1).getId(), is(7));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAppViewMissingNamespaceQueryParam() {
        errorLog.getAppView(new QueryParameters()
                .withApp("app_1")
                .withTeam("teamX")
                .withFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .withUntil(Instant.now().plus(1, ChronoUnit.DAYS)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAppViewMissingAppQueryParam() {
        errorLog.getAppView(new QueryParameters()
                .withNamespace("prod_A")
                .withTeam("teamX")
                .withFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .withUntil(Instant.now().plus(1, ChronoUnit.DAYS)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAppViewMissingTeamQueryParam() {
        errorLog.getAppView(new QueryParameters()
                .withNamespace("prod_A")
                .withApp("app_1")
                .withFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .withUntil(Instant.now().plus(1, ChronoUnit.DAYS)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAppViewMissingFromQueryParam() {
        errorLog.getAppView(new QueryParameters()
                .withNamespace("prod_A")
                .withApp("app_1")
                .withTeam("teamX")
                .withUntil(Instant.now().plus(1, ChronoUnit.DAYS)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAppViewMissingUntilQueryParam() {
        errorLog.getAppView(new QueryParameters()
                .withNamespace("prod_A")
                .withApp("app_1")
                .withTeam("teamX")
                .withFrom(Instant.now().minus(1, ChronoUnit.DAYS)));
    }
}
