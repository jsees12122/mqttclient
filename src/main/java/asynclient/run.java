package asynclient;

import com.thingdata.sdk.fuse.client.MqttAsyncClient;
import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;

import java.util.ArrayList;

public class run implements Runnable{
    private ArrayList<Message> list = new ArrayList<Message>();

    public void run() {
        MqttAsyncClient client = new MqttAsyncClient("tcp://www.thing-data.com:1883", "sub", "Deyi_admin"
                , "123456");
        client.initClient();
        client.connect(1000);
        client.subscribe("$META/IIPEvaluation/Task_Input/connection_test/pub_sub", QoS.EXACTLY_ONCE);
        while (true) {
            Future<Message> message = client.receive();
            try {
                Message mes = message.await();
                if (mes != null) {
                    System.out.println("收到topic:" + mes.getTopic());
                    synchronized (list) {
                        this.list.add(mes);
                    }
                } else {
                    System.out.println("没收到topic");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public Message getMessage() {
        synchronized (list) {
            if (list.size() == 0) {
                return null;
            } else {
                return list.remove(0);
            }
        }
    }
}
