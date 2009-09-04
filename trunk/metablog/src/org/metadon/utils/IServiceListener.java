/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.metadon.utils;

public interface IServiceListener {

	public void onAuthenticationRequestComplete(String result);
	public void onAuthenticationRequestError(String code);
	public void onPostRequestComplete(String code);
	public void onPostRequestError(String code);
}