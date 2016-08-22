/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQSslConnectionFactory;

class Publisher {

	public static void main(String[] args) throws JMSException {

		String user = env("ACTIVEMQ_USER", "admin");
		String password = env("ACTIVEMQ_PASSWORD", "admin");
		// String destination = arg(args, 0, "ETSSSLTEST");
		System.setProperty("javax.net.ssl.trustStore", "/Users/smunirat/apps/jboss-master/truststore.ks");
		System.setProperty("javax.net.ssl.trustStorePassword ", "password");
		String destination = "testQueue";
		int messages = 1000;
		int size = 256;
		String DATA = "abcdefghijklmnopqrstuvwxyz";
		String line = null;
		String body = "testsample";
		ActiveMQSslConnectionFactory sslFactory = new ActiveMQSslConnectionFactory("ssl://"+ args[2] +":"+args[3]);
		Connection connection = sslFactory.createConnection(user, password);
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Session session1 = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue createTemporaryQueue = session1.createQueue(destination);
		System.out.println(createTemporaryQueue.getQueueName());
		MessageProducer producer = session.createProducer(createTemporaryQueue);

		producer.setDeliveryMode(DeliveryMode.PERSISTENT);

		for (int i = 1; i <= messages; i++) {
			TextMessage msg = session.createTextMessage(body);
			msg.setIntProperty("id", i);
			producer.send(msg);
			if ((i % 1000) == 0) {
				System.out.println(String.format("Sent %d messages", i));
			}
		}

		producer.send(session.createTextMessage("SHUTDOWN"));
		connection.close();

	}

	private static String env(String key, String defaultValue) {
		String rc = System.getenv(key);
		if (rc == null)
			return defaultValue;
		return rc;
	}

	private static String arg(String[] args, int index, String defaultValue) {
		if (index < args.length)
			return args[index];
		else
			return defaultValue;
	}

}