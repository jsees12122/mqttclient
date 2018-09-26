package perfermance;

import com.thingdata.sdk.fuse.client.MqttAsyncClient;
import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;

public class FuseAsyncPubSubTimeTest {

    public static void main(String[] args) {
        String mqttSrvURI = "tcp://120.77.42.5:1883";
        String username = "dianqiyuan";
        String userpwd = "dqy123";
        String topic = "$META/HAT-SaaS-Data/DataPoint_Model/热平衡实验室/内侧";
        int total_send_num = 2000;

        String send_mqttClientId = "stress_send_" + System.currentTimeMillis();
        String sub_mqttClientId = "stress_sub_" + System.currentTimeMillis();

        MqttAsyncClient send_client = new MqttAsyncClient(mqttSrvURI, send_mqttClientId, username, userpwd);
        send_client.initClient();
        if (!send_client.initClient()) {
            System.out.println("send client init failed");
            System.exit(-1);
        }

        if (!send_client.connect()) {
            System.out.println("send client connect failed");
            System.exit(-1);
        }

        send_client.setSendBufferSize(1024 * 1024);

        //create a thread to subscribe the topic and receive the message
        FuseAsyncSuber suber = new FuseAsyncSuber(mqttSrvURI, username, userpwd, sub_mqttClientId, topic, total_send_num);
        Thread receiver = new Thread(suber);
        receiver.start();

        int success_send_num = 0;
        long start_send_time;
        long end_send_time;
        boolean sent = false;

        System.out.println("total send number is " + total_send_num);
        while (send_client.isConnected()) {
            start_send_time = System.currentTimeMillis();

            for (int i = 0; i < total_send_num; i++) {
                if(sent) continue;
                send_client.sendWithQoS2(topic, FuseSyncClientStressTest.msgGenerate());
//                send_client.sendAwait(topic, QoS.EXACTLY_ONCE, FuseSyncClientStressTest.msgGenerate());
                success_send_num++;
            }

            if(success_send_num != 0) System.out.println("success_send_num: " + success_send_num);

            if (success_send_num == total_send_num) {
                sent = true;
                end_send_time = System.currentTimeMillis();
                System.out.println("Cost " + (end_send_time - start_send_time) + " ms , send " + success_send_num + " message to the topic: " + topic);
            }

            success_send_num = 0;
        }
    }
}

class FuseAsyncSuber implements Runnable {
    private String mqtt_srv_uri;
    private String user_name;
    private String user_pwd;
    private String mqtt_client_id;
    private String topic;

    //statistic
    private long recv_msg_count;
    private int total_send_num;
    private long start_sub_time;
    private long end_sub_time;

    public FuseAsyncSuber(String mqttSrvURI, String username, String userpwd, String mqttClientId, String subTopic, int totalSendNum) {
        mqtt_srv_uri = mqttSrvURI;
        user_name = username;
        user_pwd = userpwd;
        mqtt_client_id = mqttClientId;
        topic = subTopic;
        total_send_num = totalSendNum;
        recv_msg_count = 0;
    }

    @Override
    public void run() {
        MqttAsyncClient sub_client = new MqttAsyncClient(mqtt_srv_uri, mqtt_client_id, user_name, user_pwd);

        if (!sub_client.initClient()) {
            System.out.println("sub client init failed");
            System.exit(0);
        }

        if (!sub_client.connect()) {
            System.out.println("sub client connect failed");
            System.exit(0);
        }

        sub_client.setReceiveBufferSize(1024 * 1024);

        if (topic.isEmpty()) {
            System.out.println("sub topic is empty");
            System.exit(0);
        }

        sub_client.subscribe(topic, QoS.EXACTLY_ONCE);
        Message msg = null;

        while (true) {
            Future<Message> fmsg = sub_client.receive();

            if (recv_msg_count == 0) {
                start_sub_time = System.currentTimeMillis();
            }

            try {
                msg = fmsg.await();
            } catch (Exception e) {
                System.out.println("receive message timeout...");
            }

            if (msg != null) {
                String topic = msg.getTopic();
                String rmsg = new String(msg.getPayload());

                if (!topic.isEmpty() && !rmsg.isEmpty()) {
                    recv_msg_count++;
                    msg.ack();
                }

                if (recv_msg_count == total_send_num) {
                    end_sub_time = System.currentTimeMillis();
                    System.out.println("Cost " + (end_sub_time - start_sub_time) + " ms ,from topic: " + topic + " receive " + recv_msg_count + " message");
//                    System.exit(0);
                }
            }
        }

    }
}
