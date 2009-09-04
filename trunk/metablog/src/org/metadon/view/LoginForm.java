package org.metadon.view;

import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

import org.metadon.beans.Credentials;
import org.metadon.control.Controller;

import de.enough.polish.ui.Form;
import de.enough.polish.ui.TextField;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.Spacer;
import de.enough.polish.ui.ItemStateListener;

import de.enough.polish.util.Locale;

//#if ${polish.debugEnabled}
import de.enough.polish.util.Debug;
//#endif

/**
 * The LoginForm class. This class is a J2ME/J2ME Polish-conform GUI-based
 * formular for handling user credentials input.
 * 
 * @author Hannes Weingartner
 * @author <a target="_blank" href="http://www.ipsquare.at">www.ipsquare.at</a>
 */
public class LoginForm extends Form implements CommandListener,
		ItemStateListener {
	public static Image USER_ICON = null;
	public static Image KEY_ICON = null;
	public static Image OK_ICON = null;

	private ImageItem userIcon;
	private ImageItem keyIcon;
	private ImageItem userOKIcon;
	private ImageItem keyOKIcon;
	private TextField tfUser;
	private TextField tfKey;

	private final int USER_FIELD_ID = 0;
	private final int KEY_FIELD_ID = 1;
	private final int COMP = 2;
	private final int INCOMP = 3;

	private final int MIN_USER_NAME_LENGTH = 6;
	private final int MAX_USER_NAME_LENGTH = 12;
	private final int MIN_KEY_LENGTH = 6;
	private final int MAX_KEY_LENGTH = 12;

	private Spacer spacer = new Spacer(1, 1);
	private boolean tfUserComplete;
	private boolean tfKeyComplete;

	private Controller controller;
	private Credentials credentials;

	private final Command OK_CMD = new Command(Locale.get("cmd.submit"), Command.ITEM, 1);
	public final Command STANDALONE_CMD = new Command(Locale.get("cmd.standalone"), Command.ITEM, 3);
	private final Command EXIT_CMD = new Command(Locale.get("cmd.exit"), Command.ITEM, 10);
	//#ifdef polish.debugEnabled
	private final Command LOG_CMD = new Command("log", Command.ITEM, 1);
	//#endif

	/**
	 * The constructor.
	 * 
	 * @param controller
	 *            the MIDlet.
	 */
	public LoginForm(Controller c) {
		//#style loginScreen
		super(Locale.get("title.login"));
		controller = c;

		// load icons for login screen
		try {
			USER_ICON = Image.createImage("/user.png");
			KEY_ICON = Image.createImage("/key.png");
			OK_ICON = Image.createImage("/ok.png");
		} catch (IOException e) {
			//#debug error
			System.out.println("Unable to load application icon"
					+ e.getMessage());
		}

		if (USER_ICON != null) {
			//#style icon
			this.userIcon = new ImageItem(null, USER_ICON, ImageItem.PLAIN,
					"user");
		}
		if (KEY_ICON != null) {
			//#style icon
			this.keyIcon = new ImageItem(null, KEY_ICON, ImageItem.PLAIN, "key");
		}
		if (OK_ICON != null) {
			//#style icon
			this.userOKIcon = new ImageItem(null, OK_ICON, ImageItem.PLAIN,
					"ok");
			//#style icon
			this.keyOKIcon = new ImageItem(null, OK_ICON, ImageItem.PLAIN, "ok");
		}
		this.enableCommands();
		this.setCommandListener(this);
		this.setItemStateListener(this);

		this.init();
	}

	public void setCredentials(Credentials credentials, boolean autoFill) {
		this.credentials = credentials;
		// auto fill text fields with login data
		if (autoFill) {
			this.tfUser.setString(this.credentials.getUser());
			this.tfKey.setString(this.credentials.getKey());
			this.setFieldStatus(this.USER_FIELD_ID, this.COMP);
			this.setFieldStatus(this.KEY_FIELD_ID, this.COMP);
		}
	}

	/**
	 * Initializes the form.
	 */
	public void init() {
		if (this.tfUser == null && this.tfKey == null) {
			this.tfUser = new TextField(null, null, this.MAX_USER_NAME_LENGTH,
					TextField.ANY);
			this.tfKey = new TextField(null, null, this.MAX_KEY_LENGTH,
					TextField.PASSWORD | TextField.UNEDITABLE);

			// fill row 0
			this.append(this.userIcon);
			//#style loginTextField
			this.append(this.tfUser);
			this.append(this.spacer);
			// not working
			// UiAccess.setTextfieldHelp(this.tfUser, "username");

			// fill row 1
			this.append(this.keyIcon);
			//#style loginTextField
			this.append(this.tfKey);
			this.append(this.spacer);
			// UiAccess.setTextfieldHelp(this.tfKey, "password");
		}
		this.clearInput();
		this.controller.show(this);
	}

	/******************************************************************/

	/**
	 * Sets the status of the specified text input widget.
	 * 
	 * @param fieldID
	 *            the id of the text field for which the status should be set.
	 *            USER_FIELD_ID or KEY_FIELD_ID.
	 * @param status
	 *            the status which should be set for the specified field.
	 */
	private void setFieldStatus(int fieldID, int status) {
		if (status == this.COMP && !this.isCompleted(fieldID)) {
			if (fieldID == this.USER_FIELD_ID) {
				this.delete(2);
				this.insert(2, this.userOKIcon);
				this.tfUserComplete = true;
				this.tfKey.setConstraints(TextField.PLAIN);
			} else if (fieldID == this.KEY_FIELD_ID
					&& this.isCompleted(this.USER_FIELD_ID)) {
				this.delete(5);
				this.insert(5, this.keyOKIcon);
				this.tfKeyComplete = true;
			}
		} else if (status == this.INCOMP && this.isCompleted(fieldID)) {
			if (fieldID == this.USER_FIELD_ID) {
				this.delete(2);
				this.insert(2, this.spacer);
				this.tfUserComplete = false;
				// remove old key
				if (this.tfKey.size() > 0)
					this.tfKey.setString(null);
				this.tfKey.setConstraints(TextField.UNEDITABLE); // lock field
			} else if (fieldID == this.KEY_FIELD_ID) {
				this.delete(5);
				this.insert(5, this.spacer);
				this.tfKeyComplete = false;
			}
		}
	}

	/**
	 * Determines if the user input for a specific field is complete.
	 * 
	 * @param fieldID
	 *            the field for which the user input should be checked.
	 * @return <code>true</code> if user input is complete for the given field.
	 */
	private boolean isCompleted(int fieldID) {
		if (fieldID == this.USER_FIELD_ID) {
			return this.tfUserComplete;
		} else if (fieldID == this.KEY_FIELD_ID) {
			return this.tfKeyComplete;
		}
		return true;
	}

	/**
	 * Retrieves the user input for this form.
	 * 
	 * @return the credentials related to this user input.
	 */
	private Credentials retrieveUserInput() {
		Credentials credentials = new Credentials();

		// valid input
		if (this.isCompleted(this.USER_FIELD_ID)
				&& this.isCompleted(this.KEY_FIELD_ID)) {
			// get field values
			String user = this.tfUser.getString();
			String key = this.tfKey.getString();

			// set credentials data
			credentials.setUser(user);
			credentials.setKey(key);
		} else
			this.controller.show(Controller.ERROR_ASCR, Locale.get("form.login.alert.incomplete"), null, null);

		return credentials;
	}

	/**
	 * Resets the user input.
	 */
	private void clearInput() {
		if (this.tfUser != null) {
			this.tfUser.setString(null);
			this.setFieldStatus(this.USER_FIELD_ID, this.INCOMP);
		}
		if (this.tfKey != null) {
			this.tfKey.setString(null);
			this.setFieldStatus(this.KEY_FIELD_ID, this.INCOMP);
		}
		// always focus on user text field
		this.focus(this.tfUser);
		// UiAccess.setFocusedItem(this, this.tfUser);
	}

	/******************************************************************/

	/**
	 * Command-related user interaction handling.
	 * 
	 * @param cmd
	 *            the command to handle.
	 * @param disp
	 *            the application display.
	 */
	public void commandAction(Command cmd, Displayable disp) {
		if (cmd == this.OK_CMD && disp == this) {
			// FIXME use server authentication
			// Credentials c = this.retrieveUserInput();
			// if (c == null) {
			// return;
			// }
			// // authenticate user at tmblog server and log into server system
			// this.controller.authenticateUser(c);
			this.controller.show(this.controller
					.getScreen(this.controller.MAIN_SCR));

		} else if (cmd == this.STANDALONE_CMD) {
			this.authentiateLocalUser();

		} else if (cmd == this.EXIT_CMD) {
			this.controller.confirmExit(this);
		}
		//#if ${polish.debugEnabled}
		else if (cmd == this.LOG_CMD) {
			Debug.showLog(Controller.getDisplay());
		}
		//#endif
	}

	// use application without a server connectivity.
	// authentication for mobile application access only.
	// this option is possible after the first server authentication.
	private void authentiateLocalUser() {
		// retrieve credentials from user input.
		Credentials credentials = this.retrieveUserInput();
		Credentials storedCredentials = this.controller.retrieveCredentials();
		if (credentials == null || storedCredentials == null)
			return;

		// authenticate user on device
		if (credentials.getUser().equals(storedCredentials.getUser())
				&& credentials.getKey().equals(storedCredentials.getKey())) {
			this.controller.show(this.controller
					.getScreen(this.controller.MAIN_SCR));
		} else {
			this.controller.show(Controller.ERROR_ASCR, Locale.get("form.login.alert.accessDenied"), null, null);
		}
	}

	/**
	 * User interaction handling.
	 * 
	 * @param item
	 *            the item which has do be handled.
	 */
	public void itemStateChanged(Item item) {
		// check user field input
		if ((item instanceof TextField)
				&& (this.tfUser.getString().length() >= this.MIN_USER_NAME_LENGTH)) {
			this.setFieldStatus(this.USER_FIELD_ID, this.COMP);
		} else if ((item instanceof TextField)
				&& (this.tfUser.getString().length() < this.MIN_USER_NAME_LENGTH)) {
			this.setFieldStatus(this.USER_FIELD_ID, this.INCOMP);
		}
		// check key field input
		if ((item instanceof TextField)
				&& (this.tfKey.getString().length() >= this.MIN_KEY_LENGTH)) {
			this.setFieldStatus(this.KEY_FIELD_ID, this.COMP);
		} else if ((item instanceof TextField)
				&& (this.tfKey.getString().length() < this.MIN_KEY_LENGTH)) {
			this.setFieldStatus(this.KEY_FIELD_ID, this.INCOMP);
		}
	}

	/**
	 * Enable all commands for this form.
	 */
	public void enableCommands() {
		this.addCommand(this.OK_CMD);
		this.addCommand(this.EXIT_CMD);
		//#if ${polish.debugEnabled}
		this.addCommand(this.LOG_CMD);
		//#endif
	}

	/**
	 * Disable all commands for this form.
	 */
	public void disableCommands() {
		this.removeCommand(this.OK_CMD);
		this.removeCommand(this.EXIT_CMD);
		//#if ${polish.debugEnabled}
		this.removeCommand(this.LOG_CMD);
		//#endif
	}

}
