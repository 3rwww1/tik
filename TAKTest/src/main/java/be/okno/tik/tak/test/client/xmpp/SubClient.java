package be.okno.tik.tak.test.client.xmpp;

public class SubClient {
	
    ConfigureForm form = new ConfigureForm(FormType.submit);
    form.setPersistentItems(false);
    form.setDeliverPayloads(true);
    form.setAccessModel(AccessModel.open);



    PubSubManager manager = new PubSubManager(connection, "pubsub.my.openfire.server");

    Node myNode = manager.createNode("TestNode", form);



    SimplePayload payload = new SimplePayload("book","pubsub:test:book", "<book xmlns='pubsub:test:book'><title>Lord of the Rings</title></book>");

    Item<SimplePayload> item = new Item<SimplePayload>(itemId, payload);



    // Required to recieve the events being published

    myNode.addItemEventListener(myEventHandler);



    // Publish item

    myNode.publish(item);



}
