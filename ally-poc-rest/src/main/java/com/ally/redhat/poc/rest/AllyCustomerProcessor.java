package com.ally.redhat.poc.rest;

import javax.inject.Named;

import com.ally.redhat.poc.rest.model.Customer;

@Named("customerProcessor")
public class AllyCustomerProcessor {
	
	public String searchCustomer(Customer customer){
		return customer.getId()+"--"+customer.getName();
				
	}
}
