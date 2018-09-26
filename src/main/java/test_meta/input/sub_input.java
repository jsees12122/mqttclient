package test_meta.input;

import com.thingdata.sdk.mbc.internal.Message;
import com.thingdata.sdk.mbc.internal.MessageBusClient;
import com.thingdata.sdk.mbc.internal.MessageBusException;
import com.thingdata.sdk.mbc.internal.MessageLevel;
import com.thingdata.sdk.mbc.service.MessageArrivedListener;
import com.thingdata.sdk.mbc.service.MessageBusClientService;
import test_meta.output.sub_output;

import java.net.URISyntaxException;

public class sub_input {
    private static MessageBusClientService client;

    public static void main(String[] args){
        try {
            client = new MessageBusClient("tcp://www.thing-data.com:1883", "test_sub_output");
            client.connect();
            subscribe("$META/IIPEvaluation/Task_Input/connection_test/pub_sub");
//            subscribe("$SYS/ERROR/JSON/IIPEvaluation/Task_Input/connection_test/pub_sub");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MessageBusException e) {
            e.printStackTrace();
        }

    }

    static class OnMessageArrived implements MessageArrivedListener {
        private static int num = 0;
        public void onMessageArrived(Message message) {
            String topic = message.getTopic();
            String jsonContent = new String(message.getPayload());
            System.out.println("收到一条来自主题【"+topic+"】的消息：");
            System.out.println(jsonContent);
            System.out.println();
            System.out.println("----------------------------------");
            num++;
            System.out.println("num:" + num);
        }
    }

    public static void subscribe(String sub_topic) throws MessageBusException {
        // 使用 OnMessageArrived 处理所有 $META/TEST/My_Hire/Engineer/ 所有下一级子目录下的事件
        client.subscribe(sub_topic, MessageLevel.EXACTLY_ONCE, new OnMessageArrived());
    }
}
