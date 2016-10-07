package com.ally.redhat.poc.soap.endpoint;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.namespace.QName;

import org.apache.camel.CamelContext;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.component.cxf.CxfEndpoint;
@Named("customerServiceEndpoint")
public class CustomerServiceEndPoint {
	
	
	@Inject
	@ContextName("restservicecontext")
	private CamelContext myproxyContext;
	
	
	
	
	public CxfEndpoint getCustomerServiceEndpoint(){
		CxfEndpoint endpoint = new CxfEndpoint();
		
		endpoint.setAddress("http://www.predic8.com:8080/crm/CustomerService");
		endpoint.setWsdlURL("http://www.predic8.com:8080/crm/CustomerService?wsdl");
		endpoint.setServiceClass(com.predic8.wsdl.crm.crmservice._1.CRMServicePT.class);
		endpoint.setPortName(new QName("http://predic8.com/wsdl/crm/CRMService/1/", "CRMServicePTPort"));
		endpoint.setServiceName(new QName("http://predic8.com/wsdl/crm/CRMService/1/","CustomerService"));
		return endpoint;
	}

}
