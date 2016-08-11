package org.amex.epv;

import javapasswordsdk.PSDKPassword;
import javapasswordsdk.PSDKPasswordRequest;
import javapasswordsdk.PasswordSDK;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author smunirat
 *
 */
public class EPVPassWordRequestor implements PassWordRequestor {

	private String queryObject;
	private String appId;
	private String userName;
	private String safe;
	private String reason;
	private String folder;
	private String retryIntervalms = "1500";
	private String maxRetryCounts = "3";
	private String encryptedPassword;

	private static final Logger LOG = LoggerFactory.getLogger(EPVPassWordRequestor.class);
	private static final String EMPTRY_STRING = "";

	public EPVPassWordRequestor() {

	}

	public String getQueryObject() {
		return queryObject;
	}

	public void setQueryObject(String queryObject) {
		this.queryObject = queryObject;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSafe() {
		return safe;
	}

	public void setSafe(String safe) {
		this.safe = safe;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getRetryIntervalms() {
		return retryIntervalms;
	}

	public void setRetryIntervalms(String retryIntervalms) {
		this.retryIntervalms = retryIntervalms;
	}

	public String getMaxRetryCounts() {
		return maxRetryCounts;
	}

	public void setMaxRetryCounts(String maxRetryCounts) {
		this.maxRetryCounts = maxRetryCounts;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}
	
	/* (non-Javadoc)
	 * @see org.amex.epv.PassWordRequestor#getPassword()
	 * Returns Password
	 */
	public String getPassword() {

		PSDKPassword passwordPsdk = null;
		String password = null;
		if (getEncryptedPassword() == null || getEncryptedPassword().equalsIgnoreCase(EMPTRY_STRING)) {
			if (getAppId() == null || getQueryObject() == null || getSafe() == null) {
				LOG.error(" Safe , Application Id and Query Object are mandatory values");
			} else {
				try {
					LOG.info("Retreiving  password from Enterprise Password Vault ");
					PSDKPasswordRequest passwordRequest = new PSDKPasswordRequest();
					LOG.trace("Safe is :" + getSafe() + " App id is :" + getAppId() + " Reason is : " + getReason()
							+ " Object : " + getQueryObject() + " Username : " + getUserName());

					passwordRequest.setAppID(getAppId());
					passwordRequest.setSafe(getSafe());
					passwordRequest.setObject(getQueryObject());
					if (getReason() != null) {
						passwordRequest.setReason(getReason());
					}
					if (getUserName() != null) {
						passwordRequest.setUserName(getUserName());
					}
					if (getFolder() != null) {
						passwordRequest.setFolder(getFolder());
					}
					passwordRequest.setFailRequestOnPasswordChange(true);
					boolean passRetrieved = false;
					int retryCounter = 0;

					while (!passRetrieved && retryCounter < Integer.parseInt(getMaxRetryCounts())) {
						try { // Sending the request to get the password
							passwordPsdk = PasswordSDK.getPassword(passwordRequest);
							password = passwordPsdk.getContent();
							passRetrieved = true;
						} catch (Exception e) {
							passRetrieved = false;
							LOG.error("Password Could not be Retrieved on attempt {} of {} ", new Object[] {
									retryCounter, getMaxRetryCounts() }, e);
							try {
								retryCounter++;
								Thread.sleep(Integer.parseInt(getRetryIntervalms()));
							} catch (InterruptedException e1) {

							}
						}
					}

				} catch (Exception e) {
					LOG.error("Password Could not be retrieved from EPV , please restart AMQ:", e);
				}
			}
		} else {
			LOG.info("Returning password from Properties File ");
			password = getEncryptedPassword();
		}
		return password;
	}
}
