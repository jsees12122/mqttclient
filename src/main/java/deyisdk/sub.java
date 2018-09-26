package deyisdk;

import com.thingdata.sdk.fuse.client.MqttSyncClient;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;

public class sub {
    public static void main(String[] args){
        MqttSyncClient client = new MqttSyncClient("tcp://127.0.0.1:1883","subsession",
                "admin","password");
        client.initClient();
        client.setWill("/uiggy111/ty","willmessage",QoS.EXACTLY_ONCE,false);
        client.setKeepAlive((short) 1);
        try {
            client.connect();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        if(client.isConnected()){
            System.out.println("----connect success----");
            while (true){
                client.subscribe("$META/IIPEvaluation/Task_Input/connection_test/pub_sub", QoS.EXACTLY_ONCE);
                Message message = client.receive();
                if(message == null){
                    System.out.println("connected...");
                }
                System.out.println("topic:" + new String(message.getPayload()));
//                message.ack();
            }

        }
    }
}
