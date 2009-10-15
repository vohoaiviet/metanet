package org.metadon.client.admin.view.panel;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.layout.HorizontalLayout;


public class WelcomePanel extends Panel
{
	
	private String	               loginIconCls	= "login-icon";
	private String	               logoutIconCls	= "logout-icon";
	
	private String	               atFlagIconCls	= "at-flag-icon";
	private String	               rsFlagIconCls	= "rs-flag-icon";
	private String	               gbFlagIconCls	= "gb-flag-icon";

	
	public void setLoginView()
	{
		// TODO layout
		
		this.removeAll();
		
		Panel loginPanel = new Panel();
		loginPanel.setLayout(new HorizontalLayout(10));
		loginPanel.setPaddings(3);
		
		
		TextField usernameField = new TextField("User: ", "username", 150);
		usernameField.setMaxLength(15);
		usernameField.setMinLength(6);
		usernameField.setAllowBlank(false);
		usernameField.focus();
		
		TextField passwordField = new TextField("Password: ", "password", 150);
		usernameField.setMaxLength(15);
		usernameField.setMinLength(6);
		passwordField.setAllowBlank(false);
		passwordField.setInputType("password");
		
		Button loginButton = new Button("Login");
		loginButton.setIconCls(loginIconCls);
		loginButton.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button,
			                    EventObject e)
			{
				login();
			}
		});

		loginPanel.add(usernameField);
		loginPanel.add(passwordField);
		loginPanel.add(loginButton);
		
		add(loginPanel);
		
	}
	
	public void setLogoutView()
	{
		this.removeAll();
		
		Panel logoutPanel = new Panel();
		logoutPanel.setLayout(new HorizontalLayout(10));
		logoutPanel.setPaddings(3);
		
		Button logoutButton = new Button("Logout");
		logoutButton.setIconCls(logoutIconCls);
		logoutButton.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button,
			                    EventObject e)
			{
				logout();
			}
		});

		logoutPanel.add(logoutButton);
		add(logoutPanel);
	}
	
	private void appendI18nSupport()
	{
		// TODO add flags and implement i18n
		
		Panel i18nPanel = new Panel();
		i18nPanel.setLayout(new HorizontalLayout(10));
		i18nPanel.setPaddings(3);
		
		
		
	}
	
	private void appendThemeChanger()
	{
		// TODO add combobox for choosing differnt themes 
		
	}
	
	private void appendHelp()
	{
		// TODO add help (window overlay with project- and usage info)
		
	}
	
	
	
	private void login()
	{

		// TODO
	}
	
	private void logout()
	{

		// TODO
	}
	
	

}
