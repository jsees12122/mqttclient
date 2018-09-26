package test_meta.statistic;

import com.thingdata.sdk.mbc.internal.Message;
import com.thingdata.sdk.mbc.internal.MessageBusClient;
import com.thingdata.sdk.mbc.internal.MessageBusException;
import com.thingdata.sdk.mbc.internal.MessageLevel;
import com.thingdata.sdk.mbc.service.MessageArrivedListener;
import com.thingdata.sdk.mbc.service.MessageBusClientService;

import java.net.URISyntaxException;

public class subERROR {

    private static MessageBusClientService client;
    private static String topic = "$META/IIPEvaluation/Task_Result/industrialConn/concurrent_ability/pub_sub";
    private static String errortopic = "$SYS/ERROR/JSON/IIPEvaluation/Task_Result/industrialConn/concurrent_ability/pub_sub";
    public static void main(String[] args){
        try {
            client = new MessageBusClient("tcp://120.78.138.224:1883", "qYsKGt12d");
//            client.setUsername("dianqiyuan");
//            client.setPassword("dqy123");
            client.connect();
            subscribe(errortopic);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MessageBusException e) {
            e.printStackTrace();
        }

    }

    static class OnMessageArrived implements MessageArrivedListener {
        public void onMessageArrived(Message message) {
            String topic = message.getTopic();
            String jsonContent = new String(message.getPayload());
            System.out.println("收到一条来自主题【"+topic+"】的消息：");
            System.out.println(jsonContent);
            System.out.println();
            System.out.println("----------------------------------");
        }
    }

    public static void subscribe(String sub_topic) throws MessageBusException {
        // 使用 OnMessageArrived 处理所有 $META/TEST/My_Hire/Engineer/ 所有下一级子目录下的事件
        client.subscribe(sub_topic, MessageLevel.EXACTLY_ONCE, new OnMessageArrived());
    }
}
