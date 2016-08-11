package org.activemq.security.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Properties;

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
import org.apache.directory.server.annotations.SaslMechanism;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.api.CoreSession;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.ldap.handlers.bind.gssapi.GssapiMechanismHandler;
import org.apache.directory.server.ldap.handlers.bind.ntlm.NtlmMechanismHandler;
import org.apache.directory.server.ldap.handlers.extended.StartTlsHandler;
import org.apache.directory.shared.ldap.model.constants.SupportedSaslMechanisms;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(FrameworkRunner.class)
@CreateLdapServer(transports = { @CreateTransport(protocol = "LDAPS") }, saslHost = "localhost", saslMechanisms = {
		@SaslMechanism(name = SupportedSaslMechanisms.GSSAPI, implClass = GssapiMechanismHandler.class),
		@SaslMechanism(name = SupportedSaslMechanisms.GSS_SPNEGO, implClass = NtlmMechanismHandler.class) },

extendedOpHandlers = { StartTlsHandler.class })
@ApplyLdifFiles("org/activemq/security/activemq-apacheds.ldif")
public class SSLLdapCachedMapPrefixTest extends AbstractLdapTestUnit {

	public BrokerService broker;
	public static LdapServer ldapServer;
	
	private final String keystorePath="C://Amex-project/keystore.ks";
	private File ksFile = null;
	private static final String CERT_IDS = new String( "userCertificate" );
	private static final Logger LOG = LoggerFactory.getLogger(SSLLdapCachedMapPrefixTest.class);
	private String propertiesFilePath="src/test/resources/org/activemq/security/ldap.properties";
	Properties props = new Properties();
	@Before
	public void setup() throws Exception {
		props.load(new FileInputStream(propertiesFilePath));
		ksFile=new File(props.getProperty("keyStorePath"));
		System.setProperty("ldapPort", String.valueOf(getLdapServer().getPortSSL()));
		
	
		
		//ksFile = File.createTempFile("truststore", "ks",null);

		CoreSession session = (CoreSession) getLdapServer().getDirectoryService().getAdminSession();

		Entry entry = session.lookup(new Dn("uid=admin,ou=system"), CERT_IDS);
		byte[] userCertificate = entry.get(CERT_IDS).getBytes();
		assertNotNull(userCertificate);
		ByteArrayInputStream in = new ByteArrayInputStream(userCertificate);
		CertificateFactory factory = CertificateFactory.getInstance("X.509");
		Certificate cert = factory.generateCertificate(in);
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(null, null);
		ks.setCertificateEntry("apacheds", cert);
		ks.store(new FileOutputStream(ksFile), props.getProperty("keyStorePassword").toString().toCharArray());
		
		LOG.info("Keystore file installed: {}", ksFile.getAbsolutePath());


		broker = BrokerFactory.createBroker("xbean:org/activemq/security/activemq-apacheds-ssl.xml");
		broker.start();
		broker.waitUntilStarted();
	}

	@After
	public void shutdown() throws Exception {
		
		try {
			broker.stop();
			broker.waitUntilStopped();
		} catch (Exception e) {
		}
		if(ksFile!=null && ksFile.exists()){
			boolean delete = ksFile.delete();
		}
	}

	@Test
	public void testSendReceive() throws Exception {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
		Connection conn = factory.createQueueConnection("jdoe", "sunflower");
		Session sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		conn.start();
		Queue queue = sess.createQueue("BAR-Sundar");

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
		Queue queue = sess.createQueue("BAR-Sundar");

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
		Queue queue = sess.createQueue("BAR-Sundar,TEST-Sundar");

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