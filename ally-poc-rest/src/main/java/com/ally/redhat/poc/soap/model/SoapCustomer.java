package com.ally.redhat.poc.soap.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="customer")
public class SoapCustomer {

	@XmlElement(name="customerId")
	private String customerId;
	@XmlElement(name="customerName")
	private String customerName;
	@XmlElement(name="companyName")
	private String companyName;
	@XmlElement(name="street")
	private String street;
	@XmlElement(name="city")
	private String city;
	@XmlElement(name="zipCode")
	private String zipCode;
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "name :"+getCustomerName()+" companyName : "+getCompanyName();
	}
	
}
