package org.activemq.security.test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith( FrameworkRunner.class )
@CreateLdapServer(transports = {@CreateTransport(protocol = "LDAP")})
@ApplyLdifFiles(
        "org/activemq/security/activemq-apacheds-prefix.ldif"
)
public class LdapCachedMapPrefixTest extends AbstractLdapTestUnit {

    public BrokerService broker;

    public static LdapServer ldapServer;

    @Before
    public void setup() throws Exception {
    	
        System.setProperty("ldapPort", String.valueOf(getLdapServer().getPort()));
        
        broker = BrokerFactory.createBroker("xbean:org/activemq/security/activemq-apacheds-prefix.xml");        
        broker.start();
        broker.waitUntilStarted();
    }

    @After
    
    public void shutdown() throws Exception {
    	try{
        broker.stop();
        broker.waitUntilStopped();
    	}catch(Exception e){
    	}
    }

    @Test
    public void testSendReceive() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
        Connection conn = factory.createQueueConnection("jdoe", "sunflower");
        Session sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        conn.start();
        Queue queue = sess.createQueue("QUEUES-BAR-Sundar");

        MessageProducer producer = sess.createProducer(queue);
        MessageConsumer consumer = sess.createConsumer(queue);

        producer.send(sess.createTextMessage("test"));
        Message msg = consumer.receive(1000);
        assertNotNull(msg);
    }

    @Test
    public void testSendDenied() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
        Connection conn = factory.createQueueConnection("admin", "sunflower");
        Session sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        conn.start();
        Queue queue = sess.createQueue("QUEUES-BAR-Sundar");

        try {
            sess.createProducer(queue);
            fail("expect auth exception");
        } catch (JMSException expected) {
        }
    }

    @Test
    public void testCompositeSendDenied() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
        Connection conn = factory.createQueueConnection("jdoe", "sunflower");
        Session sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        conn.start();
        Queue queue = sess.createQueue("QUEUES-BAR-Sundar,QUEUES-TEST-Sundar");

        try {
            sess.createProducer(queue);
            fail("expect auth exception");
        } catch (JMSException expected) {
        }
    }

    @Test
    public void testTempDestinations() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
        Connection conn = factory.createQueueConnection("jdoe", "sunflower");
        Session sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        conn.start();
        Queue queue = sess.createTemporaryQueue();

        MessageProducer producer = sess.createProducer(queue);
        MessageConsumer consumer = sess.createConsumer(queue);

        producer.send(sess.createTextMessage("test"));
        Message msg = consumer.receive(1000);
        assertNotNull(msg);
    }

}