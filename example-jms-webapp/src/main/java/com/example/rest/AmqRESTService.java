package com.example.rest;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class AmqRESTService {

	public static String sendMessage(String message, String host, String port, String queueName, String userName,
			String passWord) throws Exception {

		// String host = System.getenv("AMQ_HOST");

		// String port = System.getenv("AMQ_PORT");

		// Shouldn't create a connection/session for every message send, but
		// this should be fine
		// for the PoC
		ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://" + host + ":" + port);

		Connection connection = cf.createConnection("queueuser", "Amex1234");

		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Queue queue = session.createQueue(queueName);

		MessageProducer producer = session.createProducer(queue);

		TextMessage txtMessage = session.createTextMessage(message);

		producer.send(txtMessage);

		producer.close();
		connection.stop();
		connection.close();
		return "Message Sent";
	}

	public static String recieveMessage(String host, String port, String queueName, String userName,
			String passWord) throws Exception{
		// for the PoC
		ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://" + host + ":" + port);

		Connection connection = cf.createConnection(userName, passWord);
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue createQueue = session.createQueue(queueName);
		MessageConsumer consumer = session.createConsumer(createQueue);

		TextMessage responseMessage = (TextMessage) consumer.receive();
		String text = responseMessage.getText();
		consumer.close();
		connection.stop();
		connection.close();
		return text;
	}

}
