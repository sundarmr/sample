package org.amex.epv;

import javapasswordsdk.PSDKPassword;
import javapasswordsdk.PSDKPasswordRequest;
import javapasswordsdk.PasswordSDK;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EPVPassWordRequestor {

	private String queryObject;
	private String appId;
	private String userName;
	private String safe;
	private String reason;
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

	public String getPassword() {

		PSDKPassword passwordPsdk = null;
		String password = null;
		if (getEncryptedPassword() == null || getEncryptedPassword().equalsIgnoreCase(EMPTRY_STRING)) {
			try {
				PSDKPasswordRequest passwordRequest = new PSDKPasswordRequest();
				LOG.info("Safe is :" + getSafe() + " App id is :" + getAppId() + " Reason is : " + getReason()
						+ " Object : " + getQueryObject() + " Username : " + getUserName());
				passwordRequest.setAppID(getAppId());
				passwordRequest.setSafe(getSafe());
				passwordRequest.setReason(getReason());
				passwordRequest.setUserName(getUserName());
				passwordRequest.setObject(getQueryObject());
				passwordRequest.setFailRequestOnPasswordChange(true);
				boolean passRetrieved = false;
				int retryCounter = 0;
				
				while (!passRetrieved && retryCounter < Integer.parseInt(getMaxRetryCounts())) {
					try { // Sending the request to get the password
						passwordPsdk = PasswordSDK.getPassword(passwordRequest);
						password=passwordPsdk.getContent();
						passRetrieved = true;
					} catch (Exception e) {
						passRetrieved = false;
						LOG.error("Password Could not be Retrieved on attempt {} of {} ", new Object[] { retryCounter,
								getMaxRetryCounts() }, e);
						try {
							retryCounter++;
							Thread.sleep(Integer.parseInt(getRetryIntervalms()));
						} catch (InterruptedException e1) {

						}
					}
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}else{
			password=getEncryptedPassword();
		}
		return password;
	}
}
