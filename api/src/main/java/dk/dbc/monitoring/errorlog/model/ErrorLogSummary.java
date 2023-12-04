/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.monitoring.errorlog.model;

public class ErrorLogSummary {
    public enum Kind { TOTAL, NAMESPACE, APP, CAUSE }

    private String namespace;
    private String app;
    private String cause;
    private long count;
    private int grouping_namespace;
    private int grouping_app;
    private int grouping_cause;
    private Kind kind;

    public ErrorLogSummary() {}

    public ErrorLogSummary(String namespace, String app, String cause,
                           long count, int grouping_namespace, int grouping_app, int grouping_cause) {
        this.namespace = namespace;
        this.app = app;
        this.cause = cause;
        this.count = count;
        this.grouping_namespace = grouping_namespace;
        this.grouping_app = grouping_app;
        this.grouping_cause = grouping_cause;
    }

    public String getNamespace() {
        return namespace;
    }

    public ErrorLogSummary withNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getApp() {
        return app;
    }

    public ErrorLogSummary withApp(String app) {
        this.app = app;
        return this;
    }

    public String getCause() {
        return cause;
    }

    public ErrorLogSummary withCause(String cause) {
        this.cause = cause;
        return this;
    }

    public long getCount() {
        return count;
    }

    public ErrorLogSummary withCount(long count) {
        this.count = count;
        return this;
    }

    public int getGrouping_namespace() {
        return grouping_namespace;
    }

    public ErrorLogSummary withGrouping_namespace(int grouping_namespace) {
        this.grouping_namespace = grouping_namespace;
        return this;
    }

    public int getGrouping_app() {
        return grouping_app;
    }

    public ErrorLogSummary withGrouping_app(int grouping_app) {
        this.grouping_app = grouping_app;
        return this;
    }

    public int getGrouping_cause() {
        return grouping_cause;
    }

    public ErrorLogSummary withGrouping_cause(int grouping_cause) {
        this.grouping_cause = grouping_cause;
        return this;
    }

    public Kind getKind() {
        if (grouping_namespace == 0 && grouping_app == 0 && grouping_cause == 0) {
            return Kind.CAUSE;
        } else if (grouping_namespace == 0 && grouping_app == 0 && grouping_cause == 1) {
            return Kind.APP;
        } else if (grouping_namespace == 0 && grouping_app == 1 && grouping_cause == 1) {
            return Kind.NAMESPACE;
        }
        return Kind.TOTAL;
    }

    @Override
    public String toString() {
        return "ErrorLogSummary{" +
                "namespace='" + namespace + '\'' +
                ", app='" + app + '\'' +
                ", cause='" + cause + '\'' +
                ", count=" + count +
                ", grouping_namespace=" + grouping_namespace +
                ", grouping_app=" + grouping_app +
                ", grouping_cause=" + grouping_cause +
                ", kind=" + getKind() +
                '}';
    }
}
