This is a sample application to demo the message production in to activemq and consumption from activemq from Jboss EAP.This applicaiton is not intended to be used in produciton , it can be however used as a test base to check if message production and consumption from eap to activemq
works as intended.

Deploy the application to Jboss EAP server
Standalone mode

Sending Messages to ActiveMQ Via Rest (GET method)

http://host:port/example-jboss-webapp/rest/amq/sendMessage/{message}/{amqhostname}/{amqport}/{queuename}/{username}/{password}

Receiving Message from ActiveMQ Via Rest (GET method)

http://host:port/example-jboss-webapp/rest/amq/readMessages/{amqhostname}/{amqport}/{queuename}/{username}/{password}


The application will return a proper error message in case of failures.