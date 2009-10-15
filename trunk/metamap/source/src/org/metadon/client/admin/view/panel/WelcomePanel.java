package org.metadon.client.admin.view.panel;

import org.metadon.client.admin.view.AboutWindow;

import com.google.gwt.user.client.ui.Image;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
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
//		Panel panel = new Panel();
//		panel.setLayout(new ColumnLayout());
		
		removeAll();
		
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
		
		loginPanel.add(getAboutButton());
		
//		panel.add(loginPanel, new ColumnLayoutData(0.5));
//		panel.add(getAboutButton(), new ColumnLayoutData(0.5));
		
//		add(panel);
		add(loginPanel);
	}
	
	public void setLogoutView()
	{
//		Panel panel = new Panel();
//		panel.setLayout(new ColumnLayout());
		
		removeAll();
		
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
		
//		panel.add(logoutPanel, new ColumnLayoutData(0.5));
//		panel.add(getAboutButton(), new ColumnLayoutData(0.5));
		
//		add(panel);
		add(logoutPanel);
	}
	
	private void appendI18nSupport()
	{
		// TODO add flags and implement i18n
		
		Panel panel = new Panel();
		Image i = new Image();
		panel.add(i);
		
//		HTMLPanel southPanel = new HTMLPanel("<p>south - generally for informational stuff, also could be for status bar</p>");
//		southPanel.setHeight(100);
//		southPanel.setCollapsible(true);
//		southPanel.setTitle("South");
//		
//		HTML i18nPanel = new HTML();
//		i18nPanel.setLayout(new HorizontalLayout(10));
//		i18nPanel.setPaddings(3);
		
		
		
	}
	
	private void appendThemeChanger()
	{
		// TODO add combobox for choosing differnt themes 
		
	}
	
	private Button getAboutButton()
	{
		return new AboutWindow().getButton();
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
