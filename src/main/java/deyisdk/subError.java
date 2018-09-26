package deyisdk;

import com.thingdata.sdk.fuse.client.MqttSyncClient;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;

public class subError {
    public static void main(String[] args){
        MqttSyncClient mqttSyncClient = new MqttSyncClient("tcp://120.78.138.224:1883","YgMdas",
                "dianqiyuan","dqy123");
        mqttSyncClient.initClient();
        mqttSyncClient.connect();
        if(mqttSyncClient.isConnected()){
            String topic = "$META/IIPEvaluation/Task_Result/industrialConn/connection_availability/http_get";
            String errortopic = "$SYS/ERROR/JSON/IIPEvaluation/Task_Result/infrastructure/data_persistence/pub_sub";
            mqttSyncClient.subscribe(errortopic,QoS.EXACTLY_ONCE);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            mqttSyncClient.send("/persistence/test", QoS.EXACTLY_ONCE,"test");
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            Message message = mqttSyncClient.receive();
            System.out.println("topic:" + message.getTopic());
            System.out.println(new String(message.getPayload()));
        }else {
            System.out.println("connnect fail");
        }

    }
}
