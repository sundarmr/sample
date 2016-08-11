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
		Connection connection = null;
		ActiveMQConnectionFactory cf = null;
		Session session = null;
		MessageProducer producer = null;
		String text = "Message Sent successfully";
		try {
			cf = new ActiveMQConnectionFactory("tcp://" + host + ":" + port);
			connection = cf.createConnection("queueuser", "Amex1234");
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue(queueName);
			producer = session.createProducer(queue);
			TextMessage txtMessage = session.createTextMessage(message);
			producer.send(txtMessage);
		} catch (Exception e) {
			text = e.getMessage();
		} finally {
			try {
				session.close();
				producer.close();
				connection.stop();
				connection.close();
			} catch (Exception e) {

			}
		}
		return text;
	}

	public static String recieveMessage(String host, String port, String queueName, String userName, String passWord)
			throws Exception {
		Connection connection = null;
		ActiveMQConnectionFactory cf = null;
		Session session = null;
		MessageConsumer consumer = null;
		String text = null;
		try {
			cf = new ActiveMQConnectionFactory("tcp://" + host + ":" + port);

			connection = cf.createConnection(userName, passWord);
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue createQueue = session.createQueue(queueName);
			consumer = session.createConsumer(createQueue);

			TextMessage responseMessage = (TextMessage) consumer.receive();
			text = responseMessage.getText();
		} catch (Exception e) {
			text = e.getMessage();
		} finally {
			try {
				session.close();
				consumer.close();
				connection.stop();
				connection.close();
			} catch (Exception e) {
			}
		}
		return text;
	}

}
