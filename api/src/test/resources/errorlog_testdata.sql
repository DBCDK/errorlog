 /*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

INSERT INTO errorlog(app, namespace, host, container, message, timeLogged, team, cause, context)
VALUES ('app_1', 'prod_A', 'host_1', 'app_1_container', 'app_1_message_1', now() + '1 second'::INTERVAL, 'teamX', 'EJBException',
        '{"key1":"value1","key2": "value2"}'::JSONB);
INSERT INTO errorlog(app, namespace, host, container, message, timeLogged, team, cause, context)
VALUES ('app_1', 'prod_B', 'host_2', 'app_1_container', 'app_1_message_1', now() + '2 second'::INTERVAL, 'teamX', 'EJBException', '{}'::JSONB);
INSERT INTO errorlog(app, namespace, host, container, message, timeLogged, team, cause, context)
VALUES ('app_2', 'prod_A', 'host_1', 'app_2_container', 'app_2_message_1', now() + '3 second'::INTERVAL, 'teamX', 'EJBException', '{}'::JSONB);
INSERT INTO errorlog(app, namespace, host, container, message, timeLogged, team, cause, context)
VALUES ('app_3', 'prod_A', 'host_1', 'app_3_container', 'app_3_message_1', now() + '4 second'::INTERVAL, 'teamY', 'EJBException', '{}'::JSONB);
INSERT INTO errorlog(app, namespace, host, container, message, timeLogged, team, cause, context)
VALUES ('app_1', 'prod_A', 'host_1', 'app_1_container', 'app_1_message_1', now() + '5 second'::INTERVAL, 'teamX', 'EJBException', '{}'::JSONB);
INSERT INTO errorlog(app, namespace, host, container, message, timeLogged, team, cause, context)
VALUES ('app_1', 'prod_A', 'host_1', 'app_1_container', 'app_1_message_2', now() + '6 second'::INTERVAL, 'teamX', '', '{}'::JSONB);
INSERT INTO errorlog(app, namespace, host, container, message, timeLogged, team, cause, context)
VALUES ('app_1', 'prod_A', 'host_1', 'app_1_container', 'app_1_message_3', now() + '7 second'::INTERVAL, 'teamX', 'NullPointerException', '{}'::JSONB);