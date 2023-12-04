errorlog-consumer
=================

The purpose of this microservice is to filter all error level log statements
from one or more Kafka log topics and persist them in a custom error log
database.

### Configuration

To be eligible for consumption a log entry must be logged with level ERROR
and the logging application must  define the app.dbc.dk/team label on its
kubernetes pods.

**Environment variables**

* KAFKA_HOSTS comma-separated list of host and port pairs that are the addresses of the Kafka brokers.
* KAFKA_TOPICS comma-separated list of names of topics to be consumed.
* KAFKA_GROUP_ID name of the consumer group this consumer belongs to.
* KAFKA_CLIENT_ID identifier of a consumer that is passed to a Kafka broker with every request, with 
the sole purpose of being able to track the source of requests in the brokers.
* NUM_WORKERS number of pooled MDBs handling incoming messages (in case the 32 default is not enough).
* ERRORLOG_DB database URL (USER:PASSWORD@HOST:PORT/DBNAME) of the underlying errorlog store.

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
./clean && ./build && ./test && ./validate && KAFKA_HOSTS="..." KAFKA_TOPICS="..." ./start
```


### License

Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3.
See license text in LICENSE.txt