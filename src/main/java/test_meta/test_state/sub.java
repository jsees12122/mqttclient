package test_meta.test_state;


import com.thingdata.sdk.fuse.client.MqttSyncClient;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;

import java.net.URISyntaxException;

public class sub {
    private static MqttSyncClient client;

    public static void main(String[] args) {

        client = new MqttSyncClient("tcp://www.thing-data.com:1883", "sub-20180917",
                "dianqiyuan", "dqy123");
        client.initClient();
        client.connect();
        subscribe("$META/IIPEvaluation/Task_State/industrialConn/connection_availability/http_get");
//            subscribe("$SYS/ERROR/JSON/IIPEvaluation/Task_State");


    }


    public static void subscribe(String sub_topic){
        // 使用 OnMessageArrived 处理所有 $META/TEST/My_Hire/Engineer/ 所有下一级子目录下的事件
        client.subscribe(sub_topic, QoS.EXACTLY_ONCE);
        while (true){
            Message message = client.receive();
            System.out.println("收到来自话题：" + message.getTopic() + "的信息：" + new String(message.getPayload()));
        }
    }
}
