/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.monitoring.errorlog.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.dbc.monitoring.errorlog.ErrorCause;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LogEvent {
    private OffsetDateTime timestamp;
    @JsonProperty("sys_host")
    private String host;
    @JsonProperty("sys_appid")
    private String appID;
    @JsonProperty("sys_taskid")
    private String taskId;
    @JsonProperty("stack_trace")
    private String stacktrace;
    @JsonProperty("sys_kubernetes")
    private Kubernetes kubernetes;

    private LogEventLevel level;
    private String message;
    private String thread;
    private String logger;
    private Map<String, String> mdc;

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public Kubernetes getKubernetes() {
        return kubernetes;
    }

    public void setKubernetes(Kubernetes kubernetes) {
        this.kubernetes = kubernetes;
    }

    public LogEventLevel getLevel() {
        return level;
    }

    public void setLevel(LogEventLevel level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public String getLogger() {
        return logger;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public Map<String, String> getMdc() {
        return mdc;
    }

    public void setMdc(Map<String, String> mdc) {
        this.mdc = mdc;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LogEvent logEvent = (LogEvent) o;

        if (timestamp != null ? !timestamp.equals(logEvent.timestamp) : logEvent.timestamp != null) {
            return false;
        }
        if (host != null ? !host.equals(logEvent.host) : logEvent.host != null) {
            return false;
        }
        if (appID != null ? !appID.equals(logEvent.appID) : logEvent.appID != null) {
            return false;
        }
        if (taskId != null ? !taskId.equals(logEvent.taskId) : logEvent.taskId != null) {
            return false;
        }
        if (stacktrace != null ? !stacktrace.equals(logEvent.stacktrace) : logEvent.stacktrace != null) {
            return false;
        }
        if (kubernetes != null ? !kubernetes.equals(logEvent.kubernetes) : logEvent.kubernetes != null) {
            return false;
        }
        if (level != logEvent.level) {
            return false;
        }
        if (message != null ? !message.equals(logEvent.message) : logEvent.message != null) {
            return false;
        }
        if (thread != null ? !thread.equals(logEvent.thread) : logEvent.thread != null) {
            return false;
        }
        if (logger != null ? !logger.equals(logEvent.logger) : logEvent.logger != null) {
            return false;
        }
        return mdc != null ? mdc.equals(logEvent.mdc) : logEvent.mdc == null;

    }

    @Override
    public int hashCode() {
        int result = timestamp != null ? timestamp.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + (appID != null ? appID.hashCode() : 0);
        result = 31 * result + (taskId != null ? taskId.hashCode() : 0);
        result = 31 * result + (stacktrace != null ? stacktrace.hashCode() : 0);
        result = 31 * result + (kubernetes != null ? kubernetes.hashCode() : 0);
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (thread != null ? thread.hashCode() : 0);
        result = 31 * result + (logger != null ? logger.hashCode() : 0);
        result = 31 * result + (mdc != null ? mdc.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LogEvent{" +
                "timestamp=" + timestamp +
                ", host='" + host + '\'' +
                ", appID='" + appID + '\'' +
                ", taskId='" + taskId + '\'' +
                ", stacktrace='" + stacktrace + '\'' +
                ", kubernetes=" + kubernetes +
                ", level=" + level +
                ", message='" + message + '\'' +
                ", thread='" + thread + '\'' +
                ", logger='" + logger + '\'' +
                ", mdc=" + mdc +
                '}';
    }

    public ErrorLogEntity toErrorLogEntity() {
        final Optional<ErrorCause> errorCause = ErrorCause.of(this);
        final String cause;
        if (errorCause.isPresent()) {
            cause = errorCause.get().toString();
        } else {
            cause = message;
        }

        return new ErrorLogEntity()
                .withApp(appID)
                .withNamespace(kubernetes.getNamespace())
                .withHost(host)
                .withContainer(taskId)
                .withMessage(message)
                .withTeam(kubernetes.getTeam())
                .withLogger(logger)
                .withCause(cause)
                .withStacktrace(stacktrace)
                .withTimeLogged(timestamp)
                .withContext(mdc);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Kubernetes {
        private String namespace;
        private Map<String, Object> labels;

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getTeam() {
            return (String) labels.get("app_dbc_dk/team");
        }

        public Map<String, Object> getLabels() {
            return labels;
        }

        public void setLabels(Map<String, Object> labels) {
            this.labels = labels;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Kubernetes that = (Kubernetes) o;

            if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) {
                return false;
            }
            return labels != null ? labels.equals(that.labels) : that.labels == null;

        }

        @Override
        public int hashCode() {
            int result = namespace != null ? namespace.hashCode() : 0;
            result = 31 * result + (labels != null ? labels.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Kubernetes{" +
                    "namespace='" + namespace + '\'' +
                    ", labels=" + labels +
                    '}';
        }
    }
}
