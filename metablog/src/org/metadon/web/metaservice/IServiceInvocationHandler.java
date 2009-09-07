/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.metadon.web.metaservice;

public interface IServiceInvocationHandler {

	public void onAuthenticationRequestComplete(String msg);
	public void onAuthenticationRequestError(String msg);
	public void onAuthenticationCancel();
	public void onAuthenticationResponseError(String msg);
	
	public void onPublishRequestComplete(String msg);
	public void onPublishRequestError(String msg);
}