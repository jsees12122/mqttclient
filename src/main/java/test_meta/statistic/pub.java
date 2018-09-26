package test_meta.statistic;

import com.thingdata.sdk.mbc.internal.MessageBusClient;
import com.thingdata.sdk.mbc.internal.MessageBusException;
import com.thingdata.sdk.mbc.internal.MessageLevel;
import com.thingdata.sdk.mbc.util.JsonMessage;
import com.thingdata.sdk.mbc.util.JsonMessageBuilder;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.UUID;

public class pub {

    static MessageBusClient client;

    public static void main(String[] args){
        try {
            client = new MessageBusClient("tcp://120.78.138.224:1883", UUID.randomUUID().toString());
            client.setUsername("dianqiyuan");
            client.setPassword("dqy123");
            client.connect();
            send("$META/IIPEvaluation/Task_Result/industrialConn/concurrent_ability/pub_sub");
        } catch (MessageBusException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void send(String topic) throws MessageBusException {
        boolean result = client.send(topic, MessageLevel.EXACTLY_ONCE, constructContent());
        System.out.println(constructContent());
        System.out.println("Send result: " + result);
    }

    private static String constructContent() {
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("user_id", "test_statistic");
        properties.put("task_id", "test_statistic");
        properties.put("task_name", "test_statistic");
        properties.put("task_type", "industrialConn/concurrent_ability/pub_sub");
        properties.put("availability", 1);
        properties.put("hash_val", "test");
        properties.put("hash_info", "testfas");
        properties.put("response_time_avg", 1);
        properties.put("result_code", 1);

        JsonMessage jsonMessage = new JsonMessageBuilder()
                .setProjectName("IIPEvaluation")
                .setEvent("Task_Result")
                .setProperties(properties) // 传入包含数据信息的 map 对象
                .createJsonMessage();
        return jsonMessage.getJsonStr();
    }
}
