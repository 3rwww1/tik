package be.okno.tik.tak.test.client.xmpp;
/* test program for creating pubsub nodes and publishing payloads to it */

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

public class PubSubTest {
	private static String host = new String("walls.okno.be");
	private static XMPPConnection con;

	public static void main(String[] args) {
		String rootNodeId = new String("clocks");
		login();
		LeafNode node = getNode(rootNodeId);
		publishPayload(node,rootNodeId);
	}

	public static void login() {
		ConnectionConfiguration config = new ConnectionConfiguration(host, 5222);
		config.setSASLAuthenticationEnabled(true);

		con = new XMPPConnection(config);
		try {
			con.connect();
			con.login("admin@" + host, "t1k_t6k");
			//con.loginAnonymously();
			System.out.println("Connected:" + con.isConnected());
		} catch (XMPPException e1) {
			System.out.println("Connection fails:");
			e1.printStackTrace();
		}
	}

	public static LeafNode getNode(String nodeId) {
		LeafNode myNode = null;
		// if connected to server
		if (con.isConnected()) {
			// Create and publish
			ConfigureForm form = new ConfigureForm(FormType.submit);
			form.setPersistentItems(false);
			form.setSubscribe(true);
			form.setDeliverPayloads(true);
			form.setAccessModel(AccessModel.open);
			form.setPublishModel(PublishModel.open);

			PubSubManager manager = new PubSubManager(con, "pubsub." + host);

			// delete supernode
			/*
			 * try { manager.deleteNode(rootNodeId);
			 * System.out.println("Node deleted"); } catch (XMPPException e) {
			 * e.printStackTrace(); }
			 */

			// create or discover supernode
			try {
				// myNode = (LeafNode) manager.createNode(nodeId, form);
				//System.out.println("Node Created");
				myNode = (LeafNode) manager.getNode(nodeId);
				System.out.println("Node retrieved");
			} catch (XMPPException e1) {
				System.out.println("creating/retrieving node fails:");
				e1.printStackTrace();
			}
		}
		return myNode;
	}

	public static void publishPayload(LeafNode node, String nodeId) {
		// create payload
		// or better JSON? "{'id':1,'tiks':12345}"
		SimplePayload payload = new SimplePayload("clock", "pubsub:"
				+ nodeId + ":clock", "<clock xmlns='pubsub:" + nodeId
				+ ":clock'><id>1</id><tiks>"+123456+"</tiks></clock>");

		PayloadItem<SimplePayload> item = new PayloadItem<SimplePayload>(
				"clock1", payload);
		
		ItemEventListener myEventHandler = new ItemEventListener() {
			@Override
			public void handlePublishedItems(ItemPublishEvent items) {
				if (items != null) {
					System.out.println("resulting payload:" + items.getItems().toString());
				}
			}
		};
		node.addItemEventListener(myEventHandler);

		// publish items
		node.publish(item);
		System.out.println("Item Published");
	}
}
