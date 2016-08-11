LDAP Plugin  Project
======================

This project caters to Amex's specific need to having flat structured LDAP authorization entries

1. Amex cannot create OU's in LDAP
2. AMex cannot create Nest Groups in LDAP
3. So all the permissions , Queues , Topics should be present at the same level


This project assumes the below LDAP structure


ou=base
	cn=AdminGroup
		uid=fuseadmin														(Fuse Console Adminstrator Role User)
	cn=DeployerGroup
		uid=fusedeployer													(Fuse Console Deployer Role User)
	cn=QueueUsers															(User Group which will have Admin access  to Queue it is associated as a member)
		uid=queueuser														(User who wished to write , read and adminstrate the Queue with name QueueName )
	cn=TopicUsers															(User Group which will have Admin access  to Topic it is associated as a member)
		uid=topicuser														(User who wished to write , read and adminstrate the Topic with name QueueName )
	cn=QUEUES-QueueSample															(Actual Queue in AMQ)
		member=cn=QueueSampleAdminPermissionGroup    
		member=cn=QueueSampleWritePermissionGroup
		member=cn=QueueSampleReadPermissionGroup
	cn=TOPICS-TopicSample															(Actual Topic in AMQ)
		member=cn=TopicSampleAdminPermissionGroup	 
		member=cn=TopicSampleWritePermissionGroup     
		member=cn=TopicSampleReadPermissionGroup
	cn=TOPICS-ActiveMQ.Advisory.$													(Advisories Queue in AMQ)
		member=cn=AdvisoryAdminPermissionGroup								(All the application users who wish to connect to a Queue or Topic in AMQ should be present here )
		member=cn=AdvisoryWritePermissionGroup
		member=cn=AdvisoryReadPermissionGroup
	cn=Temp
		member=cn=TempAdminPermissionGroup	
		member=cn=TempWritePermissionGroup
		member=cn=TempReadPermissionGroup
	cn=PERMISSION-QueueSampleAdminPermissionGroup 										(Admin Permission group for Queue QueueSample)
		member=cn=QueueUsers			
	cn=PERMISSION-QueueSampleWritePermissionGroup 										(Write Permission group for Queue QueueSample)
		member=cn=QueueUsers												(User Group which will have Admin access )
	cn=PERMISSION-QueueSampleReadPermissionGroup 										(Read Permission group for Queue QueueSample)
		member=cn=QueueUsers		  										(User Group which will have Admin access )
	cn=PERMISSION-TopicSampleAdminPermissionGroup 										(Admin Permission group for Queue QueueSample)
		member=cn=TopicUsers												(User Group which will have Admin access )
	cn=PERMISSION-TopicSampleWritePermissionGroup 										(Write Permission group for Queue QueueSample)
		member=cn=TopicUsers												(User Group which will have Write access )
	cn=PERMISSION-TopicSampleReadPermissionGroup 										(Read Permission group for Queue QueueSample)
		member=cn=TopicUsers												(User Group which will have Read access )
	cn=PERMISSION-AdvisoryAdminPermissionGroup											( Admin Permission group for Advisories , all the applcation user groups who want
		member=cn=QueueUSers												 to access a Destination in AMQ for admin permissions should be added here )
		member=cn=TopicUsers
	cn=PERMISSION-AdvisoryReadPermissionGroup											( Read Permission group for Advisories , all the applcation user groups who want
		member=cn=QueueUSers												 to access a Destination in AMQ for Read permissions should be added here )
		member=cn=TopicUsers
	cn=PERMISSION-AdvisoryWritePermissionGroup											( Write Permission group for Advisories , all the applcation user groups who want
		member=cn=QueueUSers												  to access a Destination in AMQ for Write permissions should be added here )
		member=cn=TopicUsers
		
		
The Cached LDAP plugin has been modified to cater to the above structure

<broker>
	<plugins>
		<authorizationPlugin>
        	<map>
            	<bean id="propertiesAwareCached" 
            		  class="org.activemq.security.AmexCachedLdapAuthorizationMap"
                      xmlns="http://www.springframework.org/schema/beans">
                      
	                <property name="legacyGroupMapping" value="false"/>
	 			    <property name="connectionURL" value="ldap://localhost:10389"/>
	                <property name="connectionUsername" value="uid=admin,ou=system"/>
	                <!-- This will also be moved out as part of the EVP work -->
	                <property name="connectionPassword" value="secret"/>
	                <property name="queueSearchBase" value="ou=aexp,ou=e1ads,ou=system" />
	                <property name="topicSearchBase" value="ou=aexp,ou=e1ads,ou=system"/>
	                <property name="tempSearchBase" value="ou=aexp,ou=e1ads,ou=system"/>
	                <property name="permissionSearchBase" value="ou=aexp,ou=e1ads,ou=system" />
	                <property name="groupClass" value="org.apache.karaf.jaas.boot.principal.RolePrincipal"/>
	                <property name="groupObjectClass" value="groupOfNames"/>
	                <!-- The Queue name prefix for all the Queues as per the Orgnaization Standard-->
	                <property name="queueNamesPrefix" value="QUEUES-"/>
	                <!-- The Queue name prefix for all the Topics as per the Orgnaization Standard-->
	                <property name="topicNamesPrefix" value="TOPICS-"/>
	                <!-- The Queue name prefix for all the Temporary Queues as per the Orgnaization Standard-->
	                <property name="tempQueuesNamesPrefix" value="TEMP"/>    
	                <!--optional parameter defaults to TLSv1"-->
                    <property name="sslProtocol" value="TLSv1"/>
                    <!--optional parameter trust store algorithm defaults to PKIX -->
                    <property name="sslAlgorithm" value="PKIX"/>
                    <!--optional parameter defaults to java key store -->
                    <property name="storeType" value="JKS"/>
                    <property name="keyStorePath" value="${karaf.base}/etc/broker.ks"/>
                    <property name="keyStorePassword" value="secret"/>
                    <property name="trustStorePath" value="${karaf.base}/etc/truststore.ks"/>
                    <property name="trustStorePassword" value="secret"/>
               	</bean>
          	</map>
          </authorizationPlugin>
	</plugins>
</broker>
	