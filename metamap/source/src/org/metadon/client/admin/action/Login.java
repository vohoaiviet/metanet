package org.metadon.client.admin.action;

public class Login
{
	
	/**
	 * Produces a MD5 Hash Result used for secure user authentication.
	 * 
	 * @param password
	 * @param challenge
	 * @return
	 */
	public native static String getHexHMACMD5(String password, String challenge)
	/*-{
		return hex_hmac_md5(password, challenge)
	}-*/;
	
	
	

}
