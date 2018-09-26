package deyisdk;

import com.thingdata.sdk.fuse.client.MqttSyncClient;
import org.fusesource.mqtt.client.QoS;

public class pub {
    public static void main(String[] args){
        System.out.println("test");
        MqttSyncClient client = new MqttSyncClient("tcp://127.0.0.1:1883","pub_client_1",
                "admin","password");
        client.initClient();
        client.connect();
        System.out.println("test");
        if(client.isConnected()){
            client.send("$META/IIPEvaluation/Task_Input/connection_test/pub_sub", QoS.EXACTLY_ONCE,"hello");
            System.out.println("hello");
//            task task = new task(client);
//            Thread thread = new Thread(task);
//            Thread thread2 = new Thread(task);
//            Thread thread3 = new Thread(task);
//            Thread thread4 = new Thread(task);

//            thread.start();
//            thread2.start();
//            thread3.start();
//            thread4.start();
        }
    }

}
