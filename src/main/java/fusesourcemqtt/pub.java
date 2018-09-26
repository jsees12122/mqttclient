package fusesourcemqtt;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

import java.net.URISyntaxException;
// mqtt必需设置Version
public class pub {
    public static void main(String[] args){
        MQTT mqtt = new MQTT();
        try {
            mqtt.setHost("tcp://www.thing-data.com:1883");
            mqtt.setClientId("yesfsf");
//            mqtt.setVersion("3.1.1");
            BlockingConnection connection = mqtt.blockingConnection();
            System.out.println("test");
            try {
                connection.connect();
            }catch (Throwable e){
                System.out.println(e.getMessage());
            }
            System.out.println(connection.isConnected());
            connection.publish("/googlelset/goo","yesmode".getBytes(), QoS.EXACTLY_ONCE,false);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
