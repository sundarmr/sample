package org.amex.login;

import junit.framework.TestCase;

import org.amex.epv.EPVPassWordRequestor;
import org.junit.Test;


public class TestEpvPassWord extends TestCase{
	
	@Test
	public void testPropFilePassWordRequestor() throws Exception{
		EPVPassWordRequestor requestor = new EPVPassWordRequestor();
		requestor.setEncryptedPassword("somepassword");
		assertNotNull(requestor.getPassword());
	}
	@Test
	public void testEpvFilePassWordRequestor() throws Exception{
		EPVPassWordRequestor requestor = new EPVPassWordRequestor();
		requestor.setAppId("applicationId");
		requestor.setSafe("safe");
		requestor.setQueryObject("queryObject");
		assertNull(requestor.getPassword());
	}
	
}
