package org.metadon.client.admin.view.panel;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.TextField;


public class WelcomePanel extends Panel
{
	private Button loginButton;
	private TextField usernameField;
	private TextField passwordField;
	
	
	private String	               loginIconCls	= "source-icon";

	public void setLoginView()
	{
		// TODO layout
		
		this.removeAll();
		
		usernameField = new TextField("User: ", "username", 150);
		usernameField.setMaxLength(15);
		usernameField.setMinLength(6);
		usernameField.setAllowBlank(false);
		usernameField.focus();
		
		passwordField = new TextField("Password: ", "password", 150);
		usernameField.setMaxLength(15);
		usernameField.setMinLength(6);
		passwordField.setAllowBlank(false);
		passwordField.setInputType("password");
		
		loginButton = new Button("Login");
		loginButton.setIconCls(loginIconCls);
		loginButton.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button,
			                    EventObject e)
			{
				login();
			}
		});

		add(usernameField);
		add(passwordField);
		add(loginButton);
		
	}
	
	public void setLogoutView()
	{
		this.removeAll();
		
		// TODO
		
	}
	
	private void appendI18Support()
	{
		// TODO add flags and implement i18
		
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
	
	

}
