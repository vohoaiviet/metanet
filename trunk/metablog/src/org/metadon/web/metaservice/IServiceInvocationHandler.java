/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.metadon.web.metaservice;

public interface IServiceInvocationHandler {

	public void onAuthenticationSuccess(String msg);
	public void onAuthenticationError(String msg);
	public void onAuthenticationCancel();
	
	public void onLogoutSuccess(String msg);
	public void onLogoutError(String msg);
	
	public void onPublishComplete(String msg);
	public void onPublishError(String msg);
}