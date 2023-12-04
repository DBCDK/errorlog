package dk.dbc.monitoring.errorlog;

import dk.dbc.commons.jsonb.JSONBContext;
import dk.dbc.commons.jsonb.JSONBException;
import dk.dbc.monitoring.errorlog.model.ErrorLogAppView;
import dk.dbc.monitoring.errorlog.model.ErrorLogSummary;
import dk.dbc.monitoring.errorlog.model.QueryParameters;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Stateless
@Path("v1")
public class ErrorLogService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorLogService.class);
    private final JSONBContext jsonbContext = new JSONBContext();

    @EJB
    private ErrorLog errorLog;

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    private MetricRegistry metricRegistry;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("summary")
    public Response getTeamErrors(@DefaultValue("metascrum") @QueryParam("team") String team,
                                  @DefaultValue("1800") @QueryParam("fromSeconds") int fromSeconds) {
        String res = "";

        Instant now = Instant.now();

        QueryParameters queryParam = new QueryParameters();
        queryParam.withTeam(team);
        queryParam.withFrom(now.minus(fromSeconds, ChronoUnit.SECONDS));
        queryParam.withUntil(now);
        LOGGER.info("Calling getSummary with {}", queryParam);

        List<ErrorLogSummary> summary = errorLog.getSummary(queryParam);

        try {
            res = jsonbContext.marshall(summary);
            return Response.ok(res, MediaType.APPLICATION_JSON).build();
        } catch (JSONBException ex) {
            LOGGER.error("Exception during summary", ex);
            return Response.serverError().build();
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("appview")
    public Response getAppView(@QueryParam("namespace") String namespace,
                               @QueryParam("app") String app,
                               @QueryParam("team") String team,
                               @QueryParam("fromSeconds") int fromSeconds) {
        String res = "";

        Instant now = Instant.now();

        QueryParameters queryParam = new QueryParameters();
        queryParam.withNamespace(namespace);
        queryParam.withApp(app);
        queryParam.withTeam(team);
        queryParam.withFrom(now.minus(fromSeconds, ChronoUnit.SECONDS));
        queryParam.withUntil(now);
        LOGGER.info("Calling getAppView with {}", queryParam);

        ErrorLogAppView errorLogAppView = errorLog.getAppView(queryParam);

        try {
            res = jsonbContext.marshall(errorLogAppView);

            return Response.ok(res, MediaType.APPLICATION_JSON).build();
        } catch (JSONBException ex) {
            LOGGER.error("Exception during summary", ex);
            return Response.serverError().build();
        }
    }

    @GET
    @Produces({MediaType.TEXT_PLAIN})
    @Path("counters")
    public void getCounters(@Context HttpServletRequest req,
                            @Context HttpServletResponse resp,
                            @QueryParam("team") String team,
                            @QueryParam("fromSeconds") int fromSeconds)
            throws ServletException, IOException {

        Instant now = Instant.now();
        QueryParameters queryParam = new QueryParameters();
        queryParam.withTeam(team);
        queryParam.withFrom(now.minus(fromSeconds, ChronoUnit.SECONDS));
        queryParam.withUntil(now);
        List<ErrorLogSummary> summaries = errorLog.getSummary(queryParam);
        for (ErrorLogSummary summary : summaries) {
            if (summary.getKind() == ErrorLogSummary.Kind.APP) {
                Counter counter = metricRegistry.counter("errors",
                        new Tag("namespace", summary.getNamespace()),
                        new Tag("app", summary.getApp()));
                counter.inc(summary.getCount());
            }
        }

        req.getRequestDispatcher("/metrics").forward(req, resp);
    }
}
