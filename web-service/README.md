errorlog-web
============

The purpose of this microservice is to show summary of errors for a team within a timer period, and be able to drill down in to more details for a namespace.

### Configuration

**Environment variables**

* ERRORLOG_DB database URL (USER:PASSWORD@HOST:PORT/DBNAME) of the underlying errorlog store.
* ERROR_LOG_WEB_PORT the port at which this service is accessible. Default is 8080.

### Prometheus integration

Point a prometheus instance to the URL api/v1/counters?fromSeconds=[SECONDS_FROM_NOW]&team=[TEAM]
to gather error counter metrics and expose them in OpenMetrics format.

Metrics are tagged with namespace and app name.

Metrics are only collected for the team specified by the &team query parameter and the &fromSeconds
parameter determines how many seconds back in time starting from now to base the error summary on.

Be advised, that there is an inherent lag in the time between an application logs an error and the
errorlog system actually picking this up, both due to delays before the log entry ends up in kafka,
and before the errorlog system consumes it. Because of this there is a real risk of the metrics
gathering process missing log entries, and the metrics should therefore be used for trends spotting
only, not for exact counts. A relatively long scrape interval is therefore recommended.

Example config:

```yaml
scrape_configs:
      - job_name: 'errorlog-web'
        scrape_interval: 600s
        metrics_path: '/api/v1/counters'
        params:
            team: ['metascrum']
            fromSeconds: ['600']
        static_configs:
            - targets: ['somehost:someport']
```

### Development

**Requirements**

To build this project JDK 1.8 and Apache Maven is required.

To start a local instance, docker is required.

**Scripts**
* clean - clears build artifacts
* build - builds artifacts
* test - runs unit and integration tests
* validate - analyzes source code and javadoc
* start - starts localhost instance
* stop - stops localhost instance

```bash
./clean && ./build && ./test && ./validate && ERRORLOG_DB="..." ./start
```

**Local web development**

It is possible to set up a local web server with npm which serves the web pages directly from the source code while running against the api in a container.

To set it up in IntelliJ:
* Under configurations -> Edit configurations...
* Click the + sign and choose npm
* Package.json and app should already be correct
* Scripts should be "dev-server"
* Node interpreter should be /local/path/to/errorlog-web/errorlog-web-gui/node/node
* Package manager should be /local/path/to/errorlog-web/errorlog-web-gui/node/node_modules/npm 
* Everything should be setup now so click the ok button
* Before starting the configuration you need to make sure the devServer proxy port in errorlog-web-gui/webpack.config.js corresponds with the port the api is running on.
* Now run the configuration and open localhost:8080 in your favorite browser

When ever the gui changes locally the local web page will refresh. 

### License

Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3.
See license text in LICENSE.txt