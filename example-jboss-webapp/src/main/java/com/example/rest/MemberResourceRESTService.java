package com.example.rest;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * JAX-RS Example
 * 
 * This class produces a RESTful service to read the contents of the members
 * table.
 */
@Path("/amq")
@RequestScoped
public class MemberResourceRESTService {

	@GET
	@Path("/sendMessage/{message}/{host}/{port}/{queue}/{username}/{password}")
	@Produces("text/plain")
	public String sendMessage(@PathParam("message") String message, @PathParam("host") String host,
			@PathParam("port") String port, @PathParam("queue") String queueName,
			@PathParam("username") String userName, @PathParam("password") String password) {
		try {

			System.setProperty("AMQ_HOST", host);
			System.setProperty("AMQ_PORT", port);
			return AmqRESTService.sendMessage(message, host, port,queueName,userName,password);

		} catch (Exception e) {
			return "Failed to send message" + e.getMessage();
		}
	}
	@GET
	@Path("/readMessages/{host}/{port}/{queue}/{username}/{password}")
	@Produces("text/plain")
	public String recieveMessage( @PathParam("host") String host,
			@PathParam("port") String port, @PathParam("queue") String queueName,
			@PathParam("username") String userName, @PathParam("password") String password) {
		try {

			System.setProperty("AMQ_HOST", host);
			System.setProperty("AMQ_PORT", port);
			return AmqRESTService.recieveMessage( host, port,queueName,userName,password);

		} catch (Exception e) {
			return "Failed to send message" + e.getMessage();
		}
	}
}
