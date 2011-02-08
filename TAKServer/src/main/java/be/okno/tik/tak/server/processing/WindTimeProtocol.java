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

	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = 5222;
	private static final String DEFAULT_PUBSUB = "pubsub";

	private static String host;
	private static int port;
	private static String pubsub;

	private static final String KEY_HOST = "tik.host";
	private static final String KEY_PORT = "tik.port";
	private static final String KEY_PUBSUB = "tik.pubsub";

	private static final String XMPP_USERNAME = "username";
	private static final String XMPP_PASSWORD = "password";
	private static final String XMPP_MAILADDR = "email";

	private static final String XMPP_JID = "jid";

	private static final char JID_SEP = '@';
	private static final char DOT_SEP = '.';
	private static final char SPC_SEP = ' ';
	private static final char COL_SEP = ':';
	private static final char COM_SEP = ',';
	private static final char OPN_BCKT = '[';
	private static final char CLS_BCKT = ']';
	private static final char NWL_SEP = '\n';
	private static final char TAB_SEP = '\t';
	private static final char EQU_SEP = '=';

	private static final String ERR_FMTPORT = "Error while parsing TIK port configuration value.";
	private static final String WARN_DEFPORT = "TIK XMPP port is not defined in configuration file, using default port: " +  DEFAULT_PORT + ".";
	private static final String WARN_DEFHOST = "TIK XMPP host is not defined in configuration file, connecting to default host: " + DEFAULT_HOST;
	private static final String WARN_DEFPUBSUB = "TIK XMPP pubsub service is not defined in configuration file, using default pubsub service: " + DEFAULT_PUBSUB;

	private static final String ERR_STREAM = "stream error code";
	private static final String ERR_XMPP = "XMPP error code";
	private static final String ERR_UNKNOWN = "unknown";

	private static final String LOG_XMPPSERVER = "on XMPP server";
	private static final String LOG_TYPE_CONNECT = "Connecting clock";
	private static final String LOG_TYPE_LOGON_1 = "Normal logon failed for clock";
	private static final String LOG_TYPE_LOGON_2 = "Trying to create a new user on the server";
	private static final String LOG_TYPE_CREATE = "Clock account creation";
	private static final String LOG_TYPE_DISCOVER = "Discovering nodes";
	private static final String LOG_TYPE_NEWNODE = "Creating a pubsub node for current TAK client";
	private static final String LOG_XMPPCONN = "XMPP connection established for current TAK client";

	private void logXMPPException(XMPPException e, Level level, String type) {

		boolean knownError = false;

		if (e.getStreamError() != null) {
			Launcher.getLogger().log(
					level,
					type + SPC_SEP + LOG_XMPPSERVER + COL_SEP + SPC_SEP
							+ ERR_STREAM + COL_SEP + SPC_SEP
							+ e.getStreamError().toString() + DOT_SEP, e);
			knownError = true;
		}
		if (e.getXMPPError() != null) {
			Launcher.getLogger().log(
					level,
					type + SPC_SEP + LOG_XMPPSERVER + COL_SEP + SPC_SEP
							+ ERR_XMPP + COL_SEP + SPC_SEP
							+ e.getXMPPError().getCode() + DOT_SEP, e);
			knownError = true;
		}
		if (!knownError) {
			Launcher.getLogger().log(
					level,
					type + SPC_SEP + LOG_XMPPSERVER + COL_SEP + SPC_SEP
							+ ERR_UNKNOWN + COL_SEP + SPC_SEP + DOT_SEP, e);
		}
	}

	private boolean tryConnect() {

		boolean result = true;
		try {
			connection.connect();
		} catch (XMPPException e) {
			logXMPPException(e, Level.SEVERE, LOG_TYPE_CONNECT);
			result = false;
		}
		return result;
	}

	private boolean tryNormalLogin() {
		boolean result = true;

		try {
			connection.login(jid + JID_SEP + host, jid,
					jid + clock.getIdClock());
		} catch (XMPPException e) {
			logXMPPException(e, Level.WARNING, LOG_TYPE_LOGON_1 + SPC_SEP
					+ OPN_BCKT + jid + CLS_BCKT + COM_SEP + SPC_SEP
					+ LOG_TYPE_LOGON_2);
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
					if (key.equals(XMPP_MAILADDR)) {
						value = jid + JID_SEP + host;
					} else if (key.equals(XMPP_USERNAME)) {
						value = jid;
					} else if (key.equals(XMPP_PASSWORD)) {
						value = jid;
					} else {
						value = jid;
					}
					newAttributes.put(key, value);
				}
				accMan.createAccount(jid, jid, newAttributes);
				result = true;
			} catch (XMPPException e) {
				logXMPPException(e, Level.SEVERE, LOG_TYPE_CREATE);
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
			logXMPPException(e, Level.SEVERE, LOG_TYPE_DISCOVER);
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
			logXMPPException(e, Level.SEVERE, LOG_TYPE_NEWNODE);
			result = false;
		}
		return result;
	}

	public static void loadProperties() {

		String portString;

		if ((portString = Launcher.getProperty(KEY_PORT)) == null || portString.isEmpty()) {
			port = DEFAULT_PORT;
			Launcher.getLogger().warning(WARN_DEFPORT);
		} else {
			try {
				port = Integer.parseInt(portString);
			} catch (NumberFormatException e) {
				Launcher.getLogger().log(Level.SEVERE, ERR_FMTPORT, e);
				Launcher.exitOnError();
			}
		}

		if ((host = Launcher.getProperty(KEY_HOST)) == null || host.isEmpty()) {
			host = DEFAULT_HOST;
			Launcher.getLogger().warning(WARN_DEFHOST);
		}
		if ((pubsub = Launcher.getProperty(KEY_PUBSUB)) == null || pubsub.isEmpty()) {
			pubsub = DEFAULT_PUBSUB;
			Launcher.getLogger().warning(WARN_DEFPUBSUB);
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

			Launcher.getLogger().info(
					LOG_XMPPCONN + NWL_SEP + TAB_SEP + XMPP_JID + EQU_SEP
							+ OPN_BCKT + connection.getUser() + CLS_BCKT
							+ DOT_SEP);

			mgr = new PubSubManager(connection, pubsub + DOT_SEP
					+ host);

			result = createClockNodeIfNotExists();
		}
		return result;
	}

	public boolean onTik(Tik tik) {

		boolean result = false;
		if (clockNode != null) {

			++tiks;
			// TODO: Major refactoring.
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
