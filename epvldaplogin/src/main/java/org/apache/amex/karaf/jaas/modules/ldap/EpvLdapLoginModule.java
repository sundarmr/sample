package org.apache.amex.karaf.jaas.modules.ldap;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.net.ssl.SSLSocketFactory;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.amex.epv.EPVPassWordRequestor;
import org.apache.karaf.jaas.boot.principal.RolePrincipal;
import org.apache.karaf.jaas.boot.principal.UserPrincipal;
import org.apache.karaf.jaas.config.KeystoreManager;
import org.apache.karaf.jaas.modules.ldap.LDAPCache;
import org.apache.karaf.jaas.modules.ldap.LDAPLoginModule;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EpvLdapLoginModule extends LDAPLoginModule {

	private static final String DEFAULT_AUTHENTICATION = "simple";

	private static Logger logger = LoggerFactory.getLogger(EpvLdapLoginModule.class);

	public final static String CONNECTION_URL = "connection.url";
	public final static String CONNECTION_USERNAME = "connection.username";
	public final static String CONNECTION_PASSWORD = "connection.password";
	public final static String USER_BASE_DN = "user.base.dn";
	public final static String USER_FILTER = "user.filter";
	public final static String USER_SEARCH_SUBTREE = "user.search.subtree";
	public final static String ROLE_BASE_DN = "role.base.dn";
	public final static String ROLE_FILTER = "role.filter";
	public final static String ROLE_NAME_ATTRIBUTE = "role.name.attribute";
	public final static String ROLE_SEARCH_SUBTREE = "role.search.subtree";
	public final static String ROLE_MAPPING = "role.mapping";
	public final static String AUTHENTICATION = "authentication";
	public final static String ALLOW_EMPTY_PASSWORDS = "allowEmptyPasswords";
	public final static String INITIAL_CONTEXT_FACTORY = "initial.context.factory";
	public static final String CONTEXT_PREFIX = "context.";
	public final static String SSL = "ssl";
	public final static String SSL_PROVIDER = "ssl.provider";
	public final static String SSL_PROTOCOL = "ssl.protocol";
	public final static String SSL_ALGORITHM = "ssl.algorithm";
	public final static String SSL_KEYSTORE = "ssl.keystore";
	public final static String SSL_KEYALIAS = "ssl.keyalias";
	public final static String SSL_TRUSTSTORE = "ssl.truststore";
	public final static String SSL_TIMEOUT = "ssl.timeout";
	private static final String CONNECTION_PASSWORD_CLASS = "connection.password.ref.class";

	public final static String DEFAULT_INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

	private String connectionURL;
	private String connectionUsername;
	private String connectionPassword;
	private String connectionPasswordClass;
	private String userBaseDN;
	private String userFilter;
	private boolean userSearchSubtree = true;
	private String roleBaseDN;
	private String roleFilter;
	private String roleNameAttribute;
	private boolean roleSearchSubtree = true;
	private Map<String, Set<String>> roleMapping;
	private String authentication = DEFAULT_AUTHENTICATION;
	private boolean allowEmptyPasswords = false;
	private String initialContextFactory = null;
	private boolean ssl;
	private String sslProvider;
	private String sslProtocol;
	private String sslAlgorithm;
	private String sslKeystore;
	private String sslKeyAlias;
	private String sslTrustStore;
	private int sslTimeout = 10;

	

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {
		try{
		super.initialize(subject, callbackHandler, options);
		connectionURL = (String) options.get(CONNECTION_URL);
		connectionUsername = (String) options.get(CONNECTION_USERNAME);
		// connectionPassword = (String) options.get(CONNECTION_PASSWORD);
		userBaseDN = (String) options.get(USER_BASE_DN);
		userFilter = (String) options.get(USER_FILTER);
		userSearchSubtree = Boolean.parseBoolean((String) options.get(USER_SEARCH_SUBTREE));
		roleBaseDN = (String) options.get(ROLE_BASE_DN);
		roleFilter = (String) options.get(ROLE_FILTER);
		roleNameAttribute = (String) options.get(ROLE_NAME_ATTRIBUTE);
		roleSearchSubtree = Boolean.parseBoolean((String) options.get(ROLE_SEARCH_SUBTREE));
		roleMapping = parseRoleMapping((String) options.get(ROLE_MAPPING));
		initialContextFactory = (String) options.get(INITIAL_CONTEXT_FACTORY);
		if (initialContextFactory == null) {
			initialContextFactory = DEFAULT_INITIAL_CONTEXT_FACTORY;
		}
		authentication = (String) options.get(AUTHENTICATION);
		if (authentication == null) {
			authentication = DEFAULT_AUTHENTICATION;
		}
		if (connectionURL == null || connectionURL.trim().length() == 0) {
			logger.error("No LDAP URL specified.");
		} else if (!connectionURL.startsWith("ldap:") && !connectionURL.startsWith("ldaps:")) {
			logger.error("Invalid LDAP URL: " + connectionURL);
		}
		if (options.get(SSL) != null) {
			ssl = Boolean.parseBoolean((String) options.get(SSL));
		} else {
			ssl = connectionURL.startsWith("ldaps:");
		}
		sslProvider = (String) options.get(SSL_PROVIDER);
		sslProtocol = (String) options.get(SSL_PROTOCOL);
		sslAlgorithm = (String) options.get(SSL_ALGORITHM);
		sslKeystore = (String) options.get(SSL_KEYSTORE);
		sslKeyAlias = (String) options.get(SSL_KEYALIAS);
		sslTrustStore = (String) options.get(SSL_TRUSTSTORE);
		if (options.get(SSL_TIMEOUT) != null) {
			sslTimeout = (Integer) options.get(SSL_TIMEOUT);
		}
		connectionPasswordClass = (String) options.get(CONNECTION_PASSWORD_CLASS);

		if (connectionPasswordClass != null) {
			ServiceReference ref = null;
			try {
				ref = bundleContext.getServiceReference(connectionPasswordClass);
				EPVPassWordRequestor passwo = (EPVPassWordRequestor) bundleContext.getService(ref);
				connectionPassword = passwo.getPassword();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				bundleContext.ungetService(ref);
			}

		} else {
			connectionPassword = (String) options.get(CONNECTION_PASSWORD);
		}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}

	@Override
	protected boolean doLogin() throws LoginException {
		Callback[] callbacks = new Callback[2];
		callbacks[0] = new NameCallback("Username: ");
		callbacks[1] = new PasswordCallback("Password: ", false);

		try {
			callbackHandler.handle(callbacks);
		} catch (IOException ioException) {
			throw new LoginException(ioException.getMessage());
		} catch (UnsupportedCallbackException unsupportedCallbackException) {
			throw new LoginException(unsupportedCallbackException.getMessage()
					+ " not available to obtain information from user.");
		}

		user = ((NameCallback) callbacks[0]).getName();

		char[] tmpPassword = ((PasswordCallback) callbacks[1]).getPassword();

		// If either a username or password is specified don't allow
		// authentication = "none".
		// This is to prevent someone from logging into Karaf as any user
		// without providing a
		// valid password (because if authentication = none, the password could
		// be any
		// value - it is ignored).
		if ("none".equals(authentication) && (user != null || tmpPassword != null)) {
			logger.debug("Changing from authentication = none to simple since user or password was specified.");
			// default to simple so that the provided user/password will get
			// checked
			authentication = "simple";
		}
		if (!"none".equals(authentication) && !allowEmptyPasswords && (tmpPassword == null || tmpPassword.length == 0)) {
			throw new LoginException("Empty passwords not allowed");
		}

		if (tmpPassword == null) {
			tmpPassword = new char[0];
		}
		String password = new String(tmpPassword);
		principals = new HashSet<Principal>();

		// step 1: get the user DN
		final Hashtable<String, Object> env = new Hashtable<String, Object>();
		logger.debug("Create the LDAP initial context.");
		for (String key : options.keySet()) {
			if (key.startsWith(CONTEXT_PREFIX)) {
				env.put(key.substring(CONTEXT_PREFIX.length()), options.get(key));
			}
		}
		env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
		env.put(Context.PROVIDER_URL, connectionURL);
		if (connectionUsername != null && connectionUsername.trim().length() > 0) {
			logger.debug("Bound access requested.");
			env.put(Context.SECURITY_AUTHENTICATION, authentication);
			env.put(Context.SECURITY_PRINCIPAL, connectionUsername);
			env.put(Context.SECURITY_CREDENTIALS, connectionPassword);
		}
		if (ssl) {
			setupSsl(env);
		}
		logger.debug("Get the user DN.");
		final String userDN;
		final String userDNNamespace;
		try {
			String[] userDnAndNamespace = LDAPCache.getCache(env).getUserDnAndNamespace(user, new Callable<String[]>() {
				public String[] call() throws Exception {
					DirContext context = null;
					NamingEnumeration namingEnumeration = null;
					try {
						logger.debug("Initialize the JNDI LDAP Dir Context.");
						context = new InitialDirContext(env);
						logger.debug("Define the subtree scope search control.");
						SearchControls controls = new SearchControls();
						if (userSearchSubtree) {
							controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
						} else {
							controls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
						}
						logger.debug("Looking for the user in LDAP with ");
						logger.debug("  base DN: " + userBaseDN);
						userFilter = userFilter.replaceAll(Pattern.quote("%u"), Matcher.quoteReplacement(user));
						userFilter = userFilter.replace("\\", "\\\\");
						logger.debug("  filter: " + userFilter);
						namingEnumeration = context.search(userBaseDN, userFilter, controls);
						if (!namingEnumeration.hasMore()) {
							logger.warn("User " + user + " not found in LDAP.");
							return null;
						}
						logger.debug("Get the user DN.");
						SearchResult result = (SearchResult) namingEnumeration.next();

						// We need to do the following because slashes are
						// handled badly. For example, when searching
						// for a user with lots of special characters like
						// cn=admin,=+<>#;\
						// SearchResult contains 2 different results:
						//
						// SearchResult.getName = cn=admin\,\=\+\<\>\#\;\\\\
						// SearchResult.getNameInNamespace =
						// cn=admin\,\=\+\<\>#\;\\,ou=people,dc=example,dc=com
						//
						// the second escapes the slashes correctly.
						String userDNNamespace = (String) result.getNameInNamespace();
						// handle case where cn, ou, dc case doesn't match
						int indexOfUserBaseDN = userDNNamespace.toLowerCase().indexOf("," + userBaseDN.toLowerCase());
						String userDN = (indexOfUserBaseDN > 0) ? userDNNamespace.substring(0, indexOfUserBaseDN)
								: result.getName();
						return new String[] { userDN, userDNNamespace };
					} finally {
						if (namingEnumeration != null) {
							try {
								namingEnumeration.close();
							} catch (Exception e) {
								// ignore
							}
						}
						if (context != null) {
							try {
								context.close();
							} catch (Exception e) {
								// ignore
							}
						}
					}
				}
			});
			if (userDnAndNamespace == null) {
				return false;
			}
			userDN = userDnAndNamespace[0];
			userDNNamespace = userDnAndNamespace[1];
		} catch (Exception e) {
			logger.warn("Can't connect to the LDAP server: {}", e.getMessage(), e);
			throw new LoginException("Can't connect to the LDAP server: " + e.getMessage());
		}
		// step 2: bind the user using the DN
		DirContext context = null;
		try {
			// switch the credentials to the Karaf login user so that we can
			// verify his password is correct
			logger.debug("Bind user (authentication).");
			env.put(Context.SECURITY_AUTHENTICATION, authentication);
			logger.debug("Set the security principal for " + userDN + "," + userBaseDN);
			env.put(Context.SECURITY_PRINCIPAL, userDN + "," + userBaseDN);
			env.put(Context.SECURITY_CREDENTIALS, password);
			logger.debug("Binding the user.");
			context = new InitialDirContext(env);
			logger.debug("User " + user + " successfully bound.");
			context.close();
		} catch (Exception e) {
			logger.warn("User " + user + " authentication failed.", e);
			return false;
		} finally {
			if (context != null) {
				try {
					context.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
		principals.add(new UserPrincipal(user));
		// step 3: retrieving user roles
		try {
			String[] roles = LDAPCache.getCache(env).getUserRoles(userDN, new Callable<String[]>() {
				public String[] call() throws Exception {
					DirContext context = null;
					try {
						logger.debug("Get user roles.");
						// switch back to the connection credentials for the
						// role search like we did for the user search in step 1
						if (connectionUsername != null && connectionUsername.trim().length() > 0) {
							env.put(Context.SECURITY_AUTHENTICATION, authentication);
							env.put(Context.SECURITY_PRINCIPAL, connectionUsername);
							env.put(Context.SECURITY_CREDENTIALS, connectionPassword);
						}
						context = new InitialDirContext(env);
						SearchControls controls = new SearchControls();
						if (roleSearchSubtree) {
							controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
						} else {
							controls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
						}
						if (roleNameAttribute != null) {
							controls.setReturningAttributes(new String[] { roleNameAttribute });
						}
						logger.debug("Looking for the user roles in LDAP with ");
						logger.debug("  base DN: " + roleBaseDN);
						roleFilter = roleFilter.replaceAll(Pattern.quote("%u"), Matcher.quoteReplacement(user));
						roleFilter = roleFilter.replaceAll(Pattern.quote("%dn"), Matcher.quoteReplacement(userDN));
						roleFilter = roleFilter.replaceAll(Pattern.quote("%fqdn"),
								Matcher.quoteReplacement(userDNNamespace));
						roleFilter = roleFilter.replace("\\", "\\\\");
						logger.debug("  filter: " + roleFilter);
						List<String> rolesList = new ArrayList<String>();
						NamingEnumeration namingEnumeration = context.search(roleBaseDN, roleFilter, controls);
						while (namingEnumeration.hasMore()) {
							SearchResult result = (SearchResult) namingEnumeration.next();
							Attributes attributes = result.getAttributes();
							Attribute roles = attributes.get(roleNameAttribute);
							if (roles != null) {
								for (int i = 0; i < roles.size(); i++) {
									String role = (String) roles.get(i);
									if (role != null) {
										logger.debug("User {} is a member of role {}", user, role);
										// handle role mapping ...
										Set<String> mapped = tryMappingRole(role);
										if (mapped.isEmpty()) {
											rolesList.add(role);
										} else {
											for (String r : mapped) {
												rolesList.add(r);
											}
										}
									}
								}
							}

						}
						return rolesList.toArray(new String[rolesList.size()]);
					} finally {
						if (context != null) {
							try {
								context.close();
							} catch (Exception e) {
								// ignore
							}
						}
					}
				}
			});
			for (String role : roles) {
				principals.add(new RolePrincipal(role));
			}
		} catch (Exception e) {
			throw new LoginException("Can't get user " + user + " roles: " + e.getMessage());
		}
		return true;
	}

	private Map<String, Set<String>> parseRoleMapping(String option) {
		Map<String, Set<String>> roleMapping = new HashMap<String, Set<String>>();
		if (option != null) {
			logger.debug("Parse role mapping {}", option);
			String[] mappings = option.split(";");
			for (String mapping : mappings) {
				String[] map = mapping.split("=", 2);
				String ldapRole = map[0].trim();
				String[] karafRoles = map[1].split(",");
				if (roleMapping.get(ldapRole) == null) {
					roleMapping.put(ldapRole, new HashSet<String>());
				}
				final Set<String> karafRolesSet = roleMapping.get(ldapRole);
				for (String karafRole : karafRoles) {
					karafRolesSet.add(karafRole.trim());
				}
			}
		}
		return roleMapping;
	}

	protected Set<String> tryMappingRole(String role) {
		Set<String> roles = new HashSet<String>();
		if (roleMapping == null || roleMapping.isEmpty()) {
			return roles;
		}
		Set<String> karafRoles = roleMapping.get(role);
		if (karafRoles != null) {
			// add all mapped roles
			for (String karafRole : karafRoles) {
				logger.debug("LDAP role {} is mapped to Karaf role {}", role, karafRole);
				roles.add(karafRole);
			}
		}
		return roles;
	}

	protected void setupSsl(Hashtable env) throws LoginException {
		ServiceReference ref = null;
		try {
			logger.debug("Setting up SSL");
			env.put(Context.SECURITY_PROTOCOL, "ssl");
			env.put("java.naming.ldap.factory.socket", ManagedSSLSocketFactory.class.getName());
			ref = bundleContext.getServiceReference(KeystoreManager.class.getName());
			KeystoreManager manager = (KeystoreManager) bundleContext.getService(ref);
			SSLSocketFactory factory = manager.createSSLFactory(sslProvider, sslProtocol, sslAlgorithm, sslKeystore,
					sslKeyAlias, sslTrustStore, sslTimeout);
			ManagedSSLSocketFactory.setSocketFactory(factory);
			Thread.currentThread().setContextClassLoader(ManagedSSLSocketFactory.class.getClassLoader());
		} catch (Exception e) {
			throw new LoginException("Unable to setup SSL support for LDAP: " + e.getMessage());
		} finally {
			bundleContext.ungetService(ref);
		}
	}

}
