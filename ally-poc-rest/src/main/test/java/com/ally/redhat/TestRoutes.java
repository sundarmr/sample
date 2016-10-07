package com.ally.redhat;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ally.redhat.poc.rest.model.Customer;
//tag::example[]
@RunWith(CamelCdiRunner.class)
public class TestRoutes {
	
	@Produce(uri = "direct:getUsers")
    protected ProducerTemplate template;
	@Test
	public void testRoute(){
	
		Object requestBody = template.requestBody(new Customer("1","sundar"));
		System.out.println("The body is requestBody");
	}
}
