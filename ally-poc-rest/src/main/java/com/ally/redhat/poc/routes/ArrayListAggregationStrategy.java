package com.ally.redhat.poc.routes;

import java.util.ArrayList;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import com.ally.redhat.poc.soap.model.SoapCustomer;

public class ArrayListAggregationStrategy implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		if(oldExchange == null){
			SoapCustomer body = newExchange.getIn().getBody(SoapCustomer.class);
			newExchange.getIn().setBody(body);
			return newExchange;
		}else{
			SoapCustomer body = oldExchange.getIn().getBody(SoapCustomer.class);
			ArrayList<SoapCustomer> customer = new ArrayList<SoapCustomer>();
			customer.add(body);
			oldExchange.getIn().setHeader("customer",customer);
			oldExchange.getIn().setBody(body);
		}
		return oldExchange;
	}

}
