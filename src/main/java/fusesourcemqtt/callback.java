package fusesourcemqtt;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.*;

import java.net.URISyntaxException;

public class callback {
    public static void main(String[] args){
        MQTT mqtt = new MQTT();
        mqtt.setClientId("tete");
        mqtt.setCleanSession(true);
        try {
            mqtt.setHost("tcp://www.thing-data.com:1883");
            mqtt.setKeepAlive((short) 1);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        final CallbackConnection connection = mqtt.callbackConnection();
        connection.listener(new Listener() {

            public void onDisconnected() {
            }
            public void onConnected() {
            }

            public void onPublish(UTF8Buffer topic, Buffer payload, Runnable ack) {
                // You can now process a received message from a topic.
                // Once process execute the ack runnable.
                ack.run();
            }
            public void onFailure(Throwable value) {
                connection.disconnect(null); // a connection failure occured.
            }
        });
        connection.connect(new Callback<Void>() {
            public void onFailure(Throwable value) {
                System.out.println(value.toString()); // If we could not connect to the server.
            }

            // Once we connect..
            public void onSuccess(Void v) {

                // Subscribe to a topic
                Topic[] topics = {new Topic("/gggg/test", QoS.AT_LEAST_ONCE)};
                connection.subscribe(topics, new Callback<byte[]>() {
                    public void onSuccess(byte[] qoses) {
                        // The result of the subcribe request.
                        System.out.println(new String(qoses));
                    }
                    public void onFailure(Throwable value) {
                        System.out.println("sub失败");
                    }
                });

                // Send a message to a topic
                connection.publish("/gggg/test", "Hello".getBytes(), QoS.AT_LEAST_ONCE, false, new Callback<Void>() {
                    public void onSuccess(Void v) {
                        // the pubish operation completed successfully.
                    }
                    public void onFailure(Throwable value) {
                        System.out.println("sub失败"); // publish failed.
                    }
                });

                // To disconnect..
                connection.disconnect(new Callback<Void>() {
                    public void onSuccess(Void v) {
                        // called once the connection is disconnected.
                    }
                    public void onFailure(Throwable value) {
                        // Disconnects never fail.
                    }
                });
            }
        });
    }
}
