package fusesourcemqtt;

import org.fusesource.mqtt.client.*;

import java.net.URISyntaxException;

public class sub {
    public static void main(String[] args){
        System.out.println("test");
        MQTT mqtt = new MQTT();
        try {
            mqtt.setHost("tcp://www.thing-data.com:1883");
            mqtt.setClientId("sub");
            mqtt.setVersion("3.1.1");
            BlockingConnection connection = mqtt.blockingConnection();
            try {
                connection.connect();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            Thread.sleep(5000);
            System.out.println("is connnected:" + connection.isConnected());
            Topic[] topic = {new Topic("/googlelset/goo", QoS.EXACTLY_ONCE)};
            connection.subscribe(topic);
            Message message = connection.receive();
            System.out.println("-----收到来自话题：" + message.getTopic() + "的信息------");
            System.out.println("----信息内容如下：" + message.getPayload());
            message.ack();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
