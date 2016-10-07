package com.ally.redhat.poc.routes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.converter.dozer.DozerBeanMapperConfiguration;
import org.apache.camel.converter.dozer.DozerTypeConverterLoader;
import org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy;

import com.ally.redhat.poc.soap.endpoint.CustomerServiceEndPoint;
import com.ally.redhat.poc.soap.model.SoapCustomer;

@ContextName("restservicecontext")
public class CamelRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		CustomerServiceEndPoint endpoint = new CustomerServiceEndPoint();
		CxfEndpoint customerServiceEndpoint = endpoint.getCustomerServiceEndpoint();
		customerServiceEndpoint.setCamelContext(getContext());
		DozerBeanMapperConfiguration configuration = new DozerBeanMapperConfiguration();
		configuration.setMappingFiles(Arrays.asList(new String[] { "dozerMapping.xml" }));
		new DozerTypeConverterLoader(getContext(), configuration);
		getContext().setTracing(true);

		from("direct:getUsers")
		.setHeader(CxfConstants.OPERATION_NAME, simple("getAll"))
			.process(new Processor() {
			@Override
			public void process(Exchange arg0) throws Exception {
				arg0.getOut().setBody(new Object[0]);
				}
			})
				
				.to(customerServiceEndpoint)
				.process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {
						List body = exchange.getIn().getBody(List.class);
						exchange.getOut().setBody(body.get(0));
						exchange.getOut().setHeader("aggSize", ((List) body.get(0)).size());
					}
				})
				.setBody(simple("body[0]")).convertBodyTo(SoapCustomer.class).end();
		
	}

	
}
