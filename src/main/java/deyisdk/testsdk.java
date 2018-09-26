package deyisdk;

import com.thingdata.sdk.fuse.client.MqttAsyncClient;
import com.thingdata.sdk.fuse.client.MqttSyncClient;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;

public class testsdk {
    public static void main(String[] args){
        MqttSyncClient mqttSyncClient = new MqttSyncClient("tcp://www.thing-data.com:1883","sessionid",
                "Deyi_admin","123456");
        mqttSyncClient.initClient();
        mqttSyncClient.connect();
        if(mqttSyncClient.isConnected()){
            mqttSyncClient.subscribe("/persistence/test",QoS.EXACTLY_ONCE);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mqttSyncClient.send("/persistence/test", QoS.EXACTLY_ONCE,"test");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Message message = mqttSyncClient.receive();
            System.out.println("topic:" + message.getTopic());
        }else {
            System.out.println("connnect fail");
        }

    }
}
