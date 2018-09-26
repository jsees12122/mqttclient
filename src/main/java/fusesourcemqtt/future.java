package fusesourcemqtt;

import org.fusesource.mqtt.client.*;

import java.net.URISyntaxException;

import static org.fusesource.hawtbuf.Buffer.utf8;

public class future {
    public static void main(String[] args) throws Exception {
        MQTT mqtt = new MQTT();
        mqtt.setClientId("ok");
        mqtt.setCleanSession(true);
        try {
            mqtt.setHost("tcp://www.thing-data.com:1883");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        FutureConnection connection = mqtt.futureConnection();
        Future<Void> f1 = connection.connect();
        f1.await();

        Future<byte[]> f2 = connection.subscribe(new Topic[]{new Topic(utf8("foo"), QoS.AT_LEAST_ONCE)});
        byte[] qoses = f2.await();

// We can start future receive..
        Future<Message> receive = connection.receive();

// send the message..
        Future<Void> f3 = connection.publish("/foo/test", "Hello".getBytes(), QoS.AT_LEAST_ONCE, false);

// Then the receive will get the message.
        Message message = receive.await();
        message.ack();

        Future<Void> f4 = connection.disconnect();
        f4.await();
    }
}
