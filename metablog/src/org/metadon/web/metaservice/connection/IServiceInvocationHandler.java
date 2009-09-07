/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.metadon.web.metaservice.connection;

public interface IServiceInvocationHandler {

	public void onAuthenticationRequestComplete(String result);
	public void onAuthenticationRequestError(String code);
	public void onPostRequestComplete(String code);
	public void onPostRequestError(String code);
}