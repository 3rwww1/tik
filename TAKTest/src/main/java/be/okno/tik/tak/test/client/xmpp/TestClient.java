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

/* test client for subscribing to nodes and receiving payloads */

package be.okno.tik.tak.test.client.xmpp;

import java.util.Iterator;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

public class TestClient {
  public static String host = "walls.okno.be";
	//public static String user = "admin@walls.okno.be";
  //public static String password = "t1k_t6k";
  public static String user = "geraldo@"+host;
  public static String password = "test";
  public static ConnectionConfiguration config;
  public static XMPPConnection connection;
  public static PubSubManager manager;

	public static void main(String[] args) {
		  connect();		
		  //test pubsub
		  getClocks();
		  //subscribe to clock
		  subscribe("clock1");

		  while(true){
		  	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }
	  }
	   
	  public static void connect(){ 
	    config = new ConnectionConfiguration(host, 5222);
	    //config.setSASLAuthenticationEnabled(true); //throws an error, set to true?
	    config.setSelfSignedCertificateEnabled(true);

	    connection = new XMPPConnection(config);
	    try 
	    {
	      connection.connect();
	      // we log in  
	      connection.login(user, password);
	      //connection.loginAnonymously();
	      System.out.println("logged in as "+connection.getUser());
	    } 
	    catch (XMPPException e1) 
	    {
	      e1.printStackTrace();
	      System.out.println("not connected");
	    }
	    manager = new PubSubManager(connection, "pubsub." + host);
	  }

	  //PubSub, will subscribe us to TAK, returns a list of clocks
	  public static void getClocks()
	  {      
	    try {
	      DiscoverItems items = manager.discoverNodes(null);
	      Iterator<DiscoverItems.Item> it = items.getItems();

	      while (it.hasNext()) {
	        DiscoverItems.Item item = it.next();
	        System.out.println("Clock found:"+item.getNode());

	        //LeafNode node = (LeafNode)manager.getNode(item.getNode());
	        //publishPayload(node, new Double(Math.random()*10000).intValue());
	      }
	    } 
	    catch(XMPPException e1) {
	      System.out.println("retrieving nodes failed:");
	      e1.printStackTrace();
	    }
	  }

	  //test method for accessing one node we know that it was created by smack
	  public static void subscribe(String clockId)
	  {		
	    try 
	    {
	      LeafNode clock = (LeafNode) manager.getNode(clockId);
	      ItemEventListener<PayloadItem> myEventHandler = new ItemEventListener<PayloadItem>() 
	      {
	        public void handlePublishedItems(ItemPublishEvent subNode) 
	        {
	        	System.out.println("new tik:"+subNode.getItems().toString());
	        }
	      };
	      
	      clock.addItemEventListener(myEventHandler);
	      clock.subscribe(connection.getUser());
	      System.out.println("subscribed to "+clockId);
	    }
	    catch (XMPPException e1)
	    {
	    	System.out.println("xmpp error: "+e1.getXMPPError());
	    }
	  }
	}
