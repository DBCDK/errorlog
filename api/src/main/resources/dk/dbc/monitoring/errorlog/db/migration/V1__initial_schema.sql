 /*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

CREATE TABLE errorlog (
  id          SERIAL PRIMARY KEY,
  app         TEXT NOT NULL,
  namespace   TEXT NOT NULL,
  host        TEXT NOT NULL,
  container   TEXT NOT NULL,
  message     TEXT NOT NULL,
  timeLogged  TIMESTAMP WITH TIME ZONE NOT NULL,
  team        TEXT,
  logger      TEXT,
  cause       TEXT,
  stacktrace  TEXT,
  context     JSONB
);
CREATE INDEX errorlog_app_index ON errorlog(app);
CREATE INDEX errorlog_namespace_index ON errorlog(namespace);
CREATE INDEX errorlog_timeLogged_index ON errorlog(timeLogged);
CREATE INDEX errorlog_team_index ON errorlog(team);
