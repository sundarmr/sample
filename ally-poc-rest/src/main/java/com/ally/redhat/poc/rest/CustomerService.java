package com.ally.redhat.poc.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/ally")
public interface CustomerService {
		@GET
		@Path("/customer/search/{id}/{name}")
		@Produces("application/xml")
		public Response getUser(@PathParam("id") String id,@PathParam("name") String name);
		@GET
		@Path("/customer/search")
		@Produces("application/xml")
		public Response getUsers();
}
