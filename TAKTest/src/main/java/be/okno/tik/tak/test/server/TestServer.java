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

/* test server for creating pubsub nodes and publishing payloads to it */

package be.okno.tik.tak.test.server;

import java.util.Iterator;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.CollectionNode;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

public class TestServer {
	private static String host = new String("walls.okno.be");
	private static XMPPConnection con;
	private static PubSubManager manager = null;

	public static void main(String[] args) {
		login();
		manager = new PubSubManager(con, "pubsub." + host);
		// deleteNode("clocks");
		// CollectionNode node = createRootNode("clocks");
		// CollectionNode node = getRootNode("clocks");
		// addNode("clock1");
		// addNode("clock2");
		// addNode("clock3");
		doNodes();

		if (con.isConnected()) {
			LeafNode node = null;
	  	try {
				node = (LeafNode) manager.getNode("clock2");
				setNode(node);
			} catch (XMPPException e) {
				e.printStackTrace();
			}
			int tik = 1;
		  while(true){
				try {
					Thread.sleep(50);
					//publishPayload(node, new Integer(99999).intValue());
					//publishPayload(node, new Double(Math.random() * 10000).intValue());			
					publishPayload(node, tik++);	
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		  }
	  }
	}

	public static void login() {
		ConnectionConfiguration config = new ConnectionConfiguration(host, 5222);
		// config.setSASLAuthenticationEnabled(true);
		config.setSelfSignedCertificateEnabled(true);

		con = new XMPPConnection(config);
		try {
			con.connect();
			con.login("admin@" + host, "t1k_t6k");
			//con.loginAnonymously();
			System.out.println("Connected as:" + con.getUser());
		} catch (XMPPException e1) {
			System.out.println("Connection fails:");
			e1.printStackTrace();
		}
	}

	public static void deleteNode(String nodeId) {
		// if connected to server
		if (con.isConnected()) {
			// delete supernode
			try {
				manager.deleteNode(nodeId);
				System.out.println("Node deleted");
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}
	}

	public static CollectionNode getRootNode(String nodeId) {
		CollectionNode myNode = null;
		// if connected to server
		if (con.isConnected()) {
			// discover supernode
			try {
				// myNode = (LeafNode) manager.createNode(nodeId, form);
				// System.out.println("Node Created");
				myNode = (CollectionNode) manager.getNode(nodeId);
				System.out.println("Node retrieved");
			} catch (XMPPException e1) {
				System.out.println("creating/retrieving node fails:");
				e1.printStackTrace();
			}
		}
		return myNode;
	}

	public static void doNodes() {
		try {
			DiscoverItems items = manager.discoverNodes(null);
			Iterator<DiscoverItems.Item> it = items.getItems();

			while (it.hasNext()) {
				DiscoverItems.Item item = it.next();
				System.out.println("Node found:" + item.getNode());
			}
		} catch (XMPPException e1) {
			System.out.println("retrieving nodes failed:");
			e1.printStackTrace();
		}
	}

	public static void addNode(String nodeId) {
		// if connected to server
		if (con.isConnected()) {
			// Create and publish
			ConfigureForm form = new ConfigureForm(FormType.submit);
			form.setPersistentItems(false);
			form.setSubscribe(true);
			form.setDeliverPayloads(true);
			form.setAccessModel(AccessModel.open);
			form.setPublishModel(PublishModel.open);

			// add node
			try {
				LeafNode myNode = (LeafNode) manager.createNode(nodeId, form);
				System.out.println("node created: " + nodeId);
			} catch (XMPPException e1) {
				System.out.println("creating/retrieving node fails:");
				e1.printStackTrace();
			}
		}
	}

	public static void publishPayload(LeafNode node, int tiks) {
		// create payload
		// or better JSON? "{'id':1,'tiks':12345}"
		SimplePayload payload = new SimplePayload("clock", "pubsub:clock",
				"<clock xmlns='pubsub:clock'><id>" + node.getId() + "</id><tiks>" + tiks
						+ "</tiks></clock>");

		PayloadItem<SimplePayload> item = new PayloadItem<SimplePayload>("clock" + tiks, payload);
		node.publish(item);
		System.out.println("TIK item " + tiks + " published to node " + node.getId());
	}
	
	public static void setNode(LeafNode node){
		ItemEventListener myEventHandler = new ItemEventListener() {
			public void handlePublishedItems(ItemPublishEvent items) {
				if (items != null) {
					System.out.println("resulting payload:" + items.getItems().toString());
				}
			}
		};
		node.addItemEventListener(myEventHandler);
	}
}
