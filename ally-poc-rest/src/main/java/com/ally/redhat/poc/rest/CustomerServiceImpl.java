package com.ally.redhat.poc.rest;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.camel.CamelContext;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.cdi.ContextName;

import com.ally.redhat.poc.rest.model.Customer;
import com.ally.redhat.poc.soap.model.SoapCustomer;

public class CustomerServiceImpl implements CustomerService {

	@Inject
	@ContextName("restservicecontext")
	private CamelContext myproxyContext;

	@Produce(uri = "direct:getUsers")
	private ProducerTemplate mygetProxy;

	@Override
	public Response getUser(String id, String name) {

		System.out.println(myproxyContext == null ? "Context is null" : myproxyContext.getName());
		System.out.println(myproxyContext);

		ProducerTemplate producerTemplate = myproxyContext.createProducerTemplate();

		SoapCustomer requestBody = null;
		try {
			requestBody = producerTemplate.requestBody("direct:getUsers", new Customer("1", "2"), SoapCustomer.class);
		
		} catch (Exception e) {
			e.printStackTrace();

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		
		
		return Response.ok(requestBody).build();
	}

	@Override
	public Response getUsers() {

		return null;
	}

}
