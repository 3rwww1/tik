/* 
 * This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 */

package be.okno.tik.tak.server.processing;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import be.okno.tik.tak.commons.model.Clock;
import be.okno.tik.tak.commons.model.Tik;
import be.okno.tik.tak.server.BootStrap;

public class WindTimeProtocol {
	private XMPPConnection connection;
	private Clock clock;
	private String loginPasswd;
	private int tikNb = 0;
	private Chat chat;

	public void logXMPPException(XMPPException e, Level level, String type) {

		boolean kwnErr = false;

		if (e.getStreamError() != null) {
			BootStrap.getLogger().log(
					level,
					"ERROR: " + type + " on XMPP server: stream error code: "
							+ e.getStreamError().toString(), e);
			kwnErr = true;
		}
		if (e.getXMPPError() != null) {
			BootStrap.getLogger().log(
					level,
					"ERROR: " + type + " on XMPP server: XMPP error code: "
							+ e.getXMPPError().getCode(), e);
			kwnErr = true;
		}
		if (!kwnErr) {
			BootStrap.getLogger().log(level,
					"ERROR: " + type + " on XMPP server: unknown", e);
		}
	}

	public boolean tryConnect() {

		boolean result = true;
		try {
			connection.connect();
		} catch (XMPPException e) {
			logXMPPException(e, Level.SEVERE, "connecting clock");
			result = false;
		}
		return result;
	}

	public boolean tryNormalLogin() {
		boolean result = true;

		try {
			SASLAuthentication.supportSASLMechanism("PLAIN", 0);
			connection.login("airone" + "@localhost", "airone",
					clock.getName());
		} catch (XMPPException e) {
			logXMPPException(e, Level.WARNING, "normal logon");
			result = false;
		}
		return result;
	}

	public boolean tryAnonymousLogin() {
		boolean result = false;

		try {
			connection.getSASLAuthentication();
			connection.loginAnonymously();
			result = true;
		} catch (XMPPException e) {
			logXMPPException(e, Level.SEVERE, "anonymous logon");
		}

		return result;
	}

	public boolean tryCreateAccount() {

		boolean result = false;

		AccountManager accMan = connection.getAccountManager();

		if (accMan.supportsAccountCreation()) {
			try {
				Collection<String> attributesKeys = accMan
						.getAccountAttributes();

				Map<String, String> newAttributes = new HashMap<String, String>();

				String value;
				for (String key : attributesKeys) {
					if (key.equals("email")) {
						value = loginPasswd + "@okno.be";
					} else if (key.equals("username")) {
						value = "hello";
					} else if (key.equals("password")) {
						value = "secret";
					} else {
						value = loginPasswd;
					}
					newAttributes.put(key, value);
					System.out.println(key);
				}
				accMan.createAccount("hello", "secret", newAttributes);
				result = true;
			} catch (XMPPException e) {
				logXMPPException(e, Level.SEVERE, "clock account creation");
			}
		}
		return result;
	}

	public boolean onClock(Clock clock) {

		this.clock = clock;
		this.loginPasswd = clock.getName();

		boolean result = false;
		ConnectionConfiguration config = new ConnectionConfiguration(
				"localhost", 5222);

		config.setSelfSignedCertificateEnabled(true);

		connection = new XMPPConnection(config);

		result = tryConnect();
		if (result) {
			result = tryNormalLogin();
			if (!result) {
				connection.disconnect();
				result = tryConnect();
				if (result) {
					result = tryCreateAccount();
					if (result) {
						result = tryNormalLogin();
					}
				}
			}
		}
		
		

		if (result && connection != null) {

			
			chat = connection.getChatManager().createChat("isjtar@localhost", new MessageListener() {
				
			    public void processMessage(Chat chat, Message message) {
			        System.out.println("Received message: " + message);
			    }
			});
			
			BootStrap.getLogger().info(
					"XMPP connection success" + "\n\t" + "jid=["
							+ connection.getServiceName() + "]");

		}
		return result;
	}

	public boolean onTik(Tik tik) {
		try {
			chat.sendMessage("TIK " + ++tikNb + " FROM : " + clock.getName());
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return true;
	}
}
