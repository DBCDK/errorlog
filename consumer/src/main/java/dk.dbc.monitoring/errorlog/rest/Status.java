/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.monitoring.errorlog.rest;

import dk.dbc.serviceutils.ServiceStatus;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.Path;

@Stateless
@Path("/")
public class Status implements ServiceStatus {}
