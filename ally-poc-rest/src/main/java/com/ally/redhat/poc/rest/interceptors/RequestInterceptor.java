package com.ally.redhat.poc.rest.interceptors;

import javax.ws.rs.WebApplicationException;

import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

public class RequestInterceptor implements PreProcessInterceptor{

	@Override
	public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException {
		
		
		return null;
	}


	

}
