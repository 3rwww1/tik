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

import static be.okno.tik.tak.commons.util.Constants.*; 

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;

import be.okno.tik.tak.commons.model.Clock;
import be.okno.tik.tak.commons.model.Tik;
import be.okno.tik.tak.server.Launcher;

public class WindTimeProtocol {

	private XMPPConnection connection;
	private Clock clock;
	private String jid;
	private PubSubManager mgr;
	private LeafNode clockNode;
	private int tiks;
	private static String host;
	private static int port;
	private static String pubsub;
	
	// Constant objects and values.
	// TODO Remove default configuration values.
	private static final int C_XMPPDEFPORT = 5222;
	private static final String C_XMPPDEFHOST = "localhost";
	private static final String C_XMPPDEFPUBSUB = "pubsub";
	private static final String C_XMPPUSER = "username";
	private static final String C_XMPPPASSWD = "password";
	private static final String C_XMPPMAIL = "email";
	private static final String C_XMPPJID = "jid";
	
	// Properties keys.
	private static final String K_HOST = "tik.host";
	private static final String K_PORT = "tik.port";
	private static final String K_PUBSUB = "tik.pubsub";

	// Error messages.
	private static final String E_FMTPORT = "Error while parsing TIK port configuration value.";
	
	// Warning messages.
	private static final String W_DEFPORT = "TIK XMPP port is not defined in configuration file, using default port: " +  C_XMPPDEFPORT + ".";
	private static final String W_DEFHOST = "TIK XMPP host is not defined in configuration file, connecting to default host: " + C_XMPPDEFHOST;
	private static final String W_DEFPUBSUB = "TIK XMPP pubsub service is not defined in configuration file, using default pubsub service: " + C_XMPPDEFPUBSUB;
	
	// Generic messages.
	private static final String M_STREAMERR = "stream error code";
	private static final String M_XMPPERR = "XMPP error code";
	private static final String M_UNKNWNERR = "unknown";
	private static final String M_XMPPSERVER = "on XMPP server";
	private static final String M_CONNCLK = "Connecting clock";
	private static final String M_FAILLOGON = "Logon failed for clock";
	private static final String M_TRYCREATACC = "Trying to create a new user on the server";
	private static final String M_CREATACC = "Clock account creation";
	private static final String M_DISCONODE = "Discovering nodes";
	private static final String M_CREATNODE = "Creating a pubsub node for current TAK client";
	
	// Info messages.
	private static final String I_XMPPCONN = "XMPP connection established for current TAK client";

	private void logXMPPException(XMPPException e, Level level, String type) {

		boolean knownError = false;

		if (e.getStreamError() != null) {
			Launcher.getLogger().log(
					level,
					type + C_SP + M_XMPPSERVER + C_COL + C_SP
							+ M_STREAMERR + C_COL + C_SP
							+ e.getStreamError().toString() + C_DOT, e);
			knownError = true;
		}
		if (e.getXMPPError() != null) {
			Launcher.getLogger().log(
					level,
					type + C_SP + M_XMPPSERVER + C_COL + C_SP
							+ M_XMPPERR + C_COL + C_SP
							+ e.getXMPPError().getCode() + C_DOT, e);
			knownError = true;
		}
		if (!knownError) {
			Launcher.getLogger().log(
					level,
					type + C_SP + M_XMPPSERVER + C_COL + C_SP
							+ M_UNKNWNERR + C_COL + C_SP + C_DOT, e);
		}
	}

	private boolean tryConnect() {

		boolean result = true;
		try {
			connection.connect();
		} catch (XMPPException e) {
			logXMPPException(e, Level.SEVERE, M_CONNCLK);
			result = false;
		}
		return result;
	}

	private boolean tryLogin() {
		boolean result = true;

		try {
			connection.login(jid + C_AT + host, jid,
					jid + clock.getIdClock());
		} catch (XMPPException e) {
			logXMPPException(e, Level.WARNING, M_FAILLOGON + C_SP
					+ C_OBKT + jid + C_CBKT + C_COM + C_SP
					+ M_TRYCREATACC);
			result = false;
		}
		return result;
	}

	private boolean tryCreateAccount() {

		boolean result = false;

		AccountManager accMan = connection.getAccountManager();

		if (accMan.supportsAccountCreation()) {
			try {
				Collection<String> attributesKeys = accMan
						.getAccountAttributes();

				Map<String, String> newAttributes = new HashMap<String, String>();

				String value;
				for (String key : attributesKeys) {
					if (key.equals(C_XMPPMAIL)) {
						value = jid + C_AT + host;
					} else if (key.equals(C_XMPPUSER)) {
						value = jid;
					} else if (key.equals(C_XMPPPASSWD)) {
						value = jid;
					} else {
						value = jid;
					}
					newAttributes.put(key, value);
				}
				accMan.createAccount(jid, jid, newAttributes);
				result = true;
			} catch (XMPPException e) {
				logXMPPException(e, Level.SEVERE, M_CREATACC);
			}
		}
		return result;
	}

	private boolean createClockNodeIfNotExists() {
		boolean result = true;
		boolean nodeExists = false;

		try {
			DiscoverItems items = mgr.discoverNodes(null);
			Iterator<DiscoverItems.Item> it = items.getItems();

			while (it.hasNext()) {
				DiscoverItems.Item item = it.next();
				if (item.getNode().equals(jid)) {
					nodeExists = true;
					clockNode = (LeafNode) mgr.getNode(item.getNode());
					break;
				}
			}
			if (!nodeExists) {
				result = createClockNode();
			}
		} catch (XMPPException e) {
			logXMPPException(e, Level.SEVERE, M_DISCONODE);
			result = false;
		}
		return result;
	}

	private boolean createClockNode() {
		boolean result = true;

		ConfigureForm form = new ConfigureForm(FormType.submit);
		form.setPersistentItems(true);
		form.setSubscribe(true);
		form.setDeliverPayloads(true);
		form.setAccessModel(AccessModel.open);
		form.setPublishModel(PublishModel.open);
		try {
			clockNode = (LeafNode) mgr.createNode(jid, form);
		} catch (XMPPException e) {
			logXMPPException(e, Level.SEVERE, M_CREATNODE);
			result = false;
		}
		return result;
	}

	public static void loadProperties() {

		String portString;

		if ((portString = Launcher.getProperty(K_PORT)) == null || portString.isEmpty()) {
			port = C_XMPPDEFPORT;
			Launcher.getLogger().warning(W_DEFPORT);
		} else {
			try {
				port = Integer.parseInt(portString);
			} catch (NumberFormatException e) {
				Launcher.getLogger().log(Level.SEVERE, E_FMTPORT, e);
				Launcher.exitOnError();
			}
		}

		if ((host = Launcher.getProperty(K_HOST)) == null || host.isEmpty()) {
			host = C_XMPPDEFHOST;
			Launcher.getLogger().warning(W_DEFHOST);
		}
		if ((pubsub = Launcher.getProperty(K_PUBSUB)) == null || pubsub.isEmpty()) {
			pubsub = C_XMPPDEFPUBSUB;
			Launcher.getLogger().warning(W_DEFPUBSUB);
		}
	}

	public boolean onClock(Clock clock) {

		this.clock = clock;
		this.jid = clock.getName();

		boolean result = false;
		ConnectionConfiguration config = new ConnectionConfiguration(
				host, port);

		config.setSelfSignedCertificateEnabled(true);

		connection = new XMPPConnection(config);

		result = tryConnect();
		if (result) {
			result = tryLogin();
			if (!result) {
				connection.disconnect();
				result = tryConnect();
				if (result) {
					result = tryCreateAccount();
					if (result) {
						result = tryLogin();
					}
				}
			}
		}

		if (result && connection != null) {

			Launcher.getLogger().info(
					I_XMPPCONN + C_NL + C_TAB + C_XMPPJID + C_EQ
							+ C_OBKT + connection.getUser() + C_CBKT
							+ C_DOT);

			mgr = new PubSubManager(connection, pubsub + C_DOT
					+ host);

			result = createClockNodeIfNotExists();
		}
		return result;
	}

	public boolean onTik(Tik tik) {

		boolean result = false;
		if (clockNode != null) {

			++tiks;
			// TODO Major refactoring.
			SimplePayload payload = new SimplePayload("clock", "pubsub:clock",
					"<clock xmlns='pubsub:clock'><id>" + clockNode.getId()
							+ tiks + "</id><tiks>" + tiks + "</tiks></clock>");

			PayloadItem<SimplePayload> item = new PayloadItem<SimplePayload>(
					clockNode.getId() + tiks, payload);

			clockNode.publish(item);
			result = true;
		}
		return result;
	}

	@Override
	protected void finalize() throws Throwable {
		connection.disconnect();
		super.finalize();
	}
}
