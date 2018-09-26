package test_meta.test_state;

import com.thingdata.sdk.fuse.client.MqttSyncClient;
import com.thingdata.sdk.mbc.internal.MessageBusClient;
import com.thingdata.sdk.mbc.internal.MessageBusException;
import com.thingdata.sdk.mbc.internal.MessageLevel;
import common.Util;
import org.fusesource.mqtt.client.QoS;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class pub {
    public static void main(String[] args) {

        MqttSyncClient client = new MqttSyncClient("tcp://120.78.138.224:1883", "cl233", "dianqiyuan",
                "dqy123");
        client.initClient();
        client.connect();
        if (client.isConnected()) {
            System.out.println("连接成功");
        } else {
            System.out.println("连接失败");
            return;
        }
        String message = Util.constructMessage("IIPEvaluation", "Task_State", getproperties());
        System.out.println(message);
//            client.send("$META/IIPEvaluation/Task_State/connection_test/pub_sub", MessageLevel.EXACTLY_ONCE,message);
        client.send("$META/IIPEvaluation/Task_State/platformService/srvice_certainty/pub_sub", QoS.EXACTLY_ONCE, message);

    }

    public static Map getproperties() {
        HashMap map = new HashMap();
        map.put("user_id", "user001");
        map.put("task_id", "task005");
        map.put("task_name", "确定性测试pubsub");
        map.put("task_state", "succeed");
        map.put("error_reason", "no");
        map.put("task_type", "platformService/srvice_certainty/pub_sub");
        return map;
    }
}
