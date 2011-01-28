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
import be.okno.tik.tak.server.BootStrap;

public class WindTimeProtocol {

	private XMPPConnection connection;
	private Clock clock;
	private String loginPasswd;
	private PubSubManager mgr;
	private LeafNode clockNode;

	private static final String hostString = "@localhost";

	private void logXMPPException(XMPPException e, Level level, String type) {

		boolean kwnErr = false;

		if (level != Level.SEVERE) {
			BootStrap.getLogger().log(level, type + " on XMPP server");
		} else {
			if (e.getStreamError() != null) {
				BootStrap.getLogger().log(
						level,
						type + " on XMPP server: stream error code: "
								+ e.getStreamError().toString(), e);
				kwnErr = true;
			}
			if (e.getXMPPError() != null) {
				BootStrap.getLogger().log(
						level,
						type + " on XMPP server: XMPP error code: "
								+ e.getXMPPError().getCode(), e);
				kwnErr = true;
			}
			if (!kwnErr) {
				BootStrap.getLogger().log(level,
						type + " on XMPP server: unknown", e);
			}
		}

	}

	private boolean tryConnect() {

		boolean result = true;
		try {
			connection.connect();
		} catch (XMPPException e) {
			logXMPPException(e, Level.SEVERE, "connecting clock");
			result = false;
		}
		return result;
	}

	private boolean tryNormalLogin() {
		boolean result = true;

		try {
			connection.login(loginPasswd + hostString, loginPasswd, loginPasswd
					+ clock.getIdClock());
		} catch (XMPPException e) {
			logXMPPException(e, Level.WARNING,
					"normal logon failed for clock [" + loginPasswd
							+ "], trying to create a new user on the server");
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
					if (key.equals("email")) {
						value = loginPasswd + "@okno.be";
					} else if (key.equals("username")) {
						value = loginPasswd;
					} else if (key.equals("password")) {
						value = loginPasswd;
					} else {
						value = loginPasswd;
					}
					newAttributes.put(key, value);
				}
				accMan.createAccount(loginPasswd, loginPasswd, newAttributes);
				result = true;
			} catch (XMPPException e) {
				logXMPPException(e, Level.SEVERE, "clock account creation");
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
				System.out.println(item.getNode());
				if (item.getNode().equals(loginPasswd)) {
					nodeExists = true;
					break;
				}
			}
			if (!nodeExists) {
				result = createClockNode();
			}
		} catch (XMPPException e1) {
			logXMPPException(e1, Level.SEVERE, "discovering node");
			result = false;
		}
		return result;
	}

	private boolean createClockNode() {
		boolean result = true;

		ConfigureForm form = new ConfigureForm(FormType.submit);
		form.setAccessModel(AccessModel.open);
		form.setDeliverPayloads(true);
		form.setNotifyRetract(false);
		form.setPersistentItems(false);
		form.setPublishModel(PublishModel.open);
		try {
			clockNode = (LeafNode) mgr.createNode(loginPasswd, form);
		} catch (XMPPException e) {
			logXMPPException(e, Level.SEVERE, "creating pubsub node");
			result = false;
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

			BootStrap.getLogger().info(
					"XMPP connection success" + "\n\t" + "jid=["
							+ connection.getUser() + "]");

			mgr = new PubSubManager(connection, "pubsub.localhost");

			result = createClockNodeIfNotExists();
		}
		return result;
	}

	public boolean onTik(Tik tik) {
		try {
			clockNode.send(new PayloadItem<SimplePayload>(loginPasswd
					+ System.currentTimeMillis(), new SimplePayload("tik",
					"pubsub:clock:tik", "" + "")));

		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	protected void finalize() throws Throwable {
		connection.disconnect();
		super.finalize();
	}

	public static void main(String[] av) {

		class ClockWindTimeCreator {

			private Clock clock;
			private WindTimeProtocol wtp;

			public ClockWindTimeCreator(Integer id, String name) {

				this.clock = new Clock();
				clock.setIdClock(id);
				clock.setName(name);
				this.wtp = new WindTimeProtocol();
			}

			public void test() {
				wtp.onClock(clock);
			}
		}
		new ClockWindTimeCreator(42, "cool").test();
		new ClockWindTimeCreator(54, "prima").test();
		new ClockWindTimeCreator(66, "genau").test();
	}
}
