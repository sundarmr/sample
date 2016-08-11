package org.amex.test;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

public class TestPermissions extends TestCase {
	String sslUrl = "ssl://lpdosput00716.phx.aexp.com:61617";
	String tcpUrl = "tcp://lpdosput00716.phx.aexp.com:61616";
	ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(tcpUrl);
	final static Logger LOG = LoggerFactory.getLogger(TestPermissions.class);

	@Test
	public void testQueuePermissoins() throws Exception {
		factory.setUserName("queueuser1");
		factory.setPassword("Amex1234");
		boolean success = false;
		try {
			Connection connection = factory.createConnection();
			Session createSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue createQueue = createSession.createQueue("GG-ADS-E1-ecp-AMQ-QUEUE-TestQueue");
			MessageProducer producer = createSession.createProducer(createQueue);

			TextMessage msg = createSession.createTextMessage("This is for Testing");
			producer.send(msg);
			success = true;
		} catch (Exception e) {
			// LOG.error(e.getMessage(), e);
		}
		assertFalse(success);

	}

	@Test
	public void testSendDeniedAuth() throws Exception {
		factory.setUserName("queueuser");
		factory.setPassword("Amex1234");
		boolean success = false;
		try {
			Connection connection = factory.createConnection();
			Session createSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue createQueue = createSession.createQueue("GG-ADS-E1-ecp-AMQ-QUEUE-TestQueue");
			MessageProducer producer = createSession.createProducer(createQueue);

			TextMessage msg = createSession.createTextMessage("This is for Testing");
			producer.send(msg);
			success = true;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		assertFalse(!success);
	}

	@Test
	public void testSendDeniedDiffQuue() throws Exception {
		factory.setUserName("queueuser");
		factory.setPassword("Amex1234");
		boolean success = false;
		try {
			Connection connection = factory.createConnection();
			Session createSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue createQueue = createSession.createQueue("GG-ADS-E1-ecp-AMQ-QUEUE-TestQueu1e");
			MessageProducer producer = createSession.createProducer(createQueue);

			TextMessage msg = createSession.createTextMessage("This is for Testing");
			producer.send(msg);
			success = true;
		} catch (Exception e) {
			// LOG.error(e.getMessage(), e);
		}
		assertFalse(success);
	}

	@Test
	public void testSendtemp() throws Exception {
		factory.setUserName("alternatequeueuser");
		factory.setPassword("Amex1234");
		boolean success = false;
		try {
			Connection connection = factory.createConnection();
			Session createSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue createQueue = createSession.createTemporaryQueue();
			MessageProducer producer = createSession.createProducer(createQueue);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			TextMessage msg = createSession.createTextMessage("This is for Testing");
			producer.send(msg);
			success = true;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		assertTrue(success);
	}

	@Test
	public void testSendtemptopic() throws Exception {
		factory.setUserName("alternatequeueuser");
		factory.setPassword("Amex1234");
		boolean success = false;
		try {
			Connection connection = factory.createConnection();
			Session createSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Topic createQueue = createSession.createTemporaryTopic();
			MessageProducer producer = createSession.createProducer(createQueue);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			TextMessage msg = createSession.createTextMessage("This is for Testing");
			producer.send(msg);
			success = true;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		assertTrue(success);
	}

	public void testCombinationQueues() {

		factory.setUserName("alternatequeueuser");
		factory.setPassword("Amex1234");
		boolean success = false;
		try {
			Connection connection = factory.createConnection();
			Session createSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue createQueue = createSession.createQueue("GG-ADS-E1-ecp-AMQ-QUEUE-T.All");
			MessageProducer producer = createSession.createProducer(createQueue);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			TextMessage msg = createSession.createTextMessage("This is for Testing");
			producer.send(msg);
			success = true;
		} catch (Exception e) {
			 LOG.error(e.getMessage(), e);
		}
		assertTrue(success);
	}

	public void testAdvisory() {

		factory.setUserName("topicuser");
		factory.setPassword("Amex1234");
		boolean success = false;
		try {
			Connection connection = factory.createConnection();
			Session createSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Topic createTopic = createSession.createTopic("GG-ADS-E1-ecp-AMQ-TOPIC-ALL.Every");
			MessageProducer producer = createSession.createProducer(createTopic);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			TextMessage msg = createSession.createTextMessage("This is for Testing");
			producer.send(msg);
			success = true;
		} catch (Exception e) {
		//	LOG.error(e.getMessage(), e);
		}
		assertTrue(!success);
	}

	@Test
	public void testSSLConnection() throws Exception{
		ActiveMQSslConnectionFactory connectionFactory = new ActiveMQSslConnectionFactory(sslUrl);
		connectionFactory.setUserName("queueuser");
		connectionFactory.setPassword("Amex1234");
		connectionFactory.setTrustStore("c://Amex-Project/jbossweb.keystore");
		connectionFactory.setTrustStorePassword("JbossPassword");
		Connection createConnection = connectionFactory.createConnection();
		Session createSession = createConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue createQueue = createSession.createQueue("GG-ADS-E1-ecp-AMQ-QUEUE-TestQueue");
		MessageProducer producer = createSession.createProducer(createQueue);
		producer.send(createSession.createTextMessage("Hello"));
	}
*/
}
