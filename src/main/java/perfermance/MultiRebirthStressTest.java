package perfermance;

import com.thingdata.sdk.fuse.client.MqttSyncClient;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;

/**
 * fuse-source blocking mqtt client rebirth testing.
 * this test-case is aimed at monitoring the performance when mqtt client create and kill for many times.
 * steps:
 *   1) create a client for testing;
 *   2) operation - pub and sub a msg;
 *   3) kill the client
 *   4) loop 1000 times
 * observe the performance.
 * */
public class MultiRebirthStressTest {
    private final static String mqttSrvURI = "tcp://120.77.42.5:1883";
    private final static String username ="dianqiyuan";
    private final static String userpwd = "dqy123";
    private final static String topic = "$META/HAT-SaaS-Data/DataPoint_Model/热平衡实验室/内侧";

    private final static int loop_times = 1000;
    private static int success_cnt = 0; // count for pub/sub successfully

    public static void main(String[] args) {
        int count = 0;

        while(count < loop_times){

            System.out.println("create the task");
            Thread t_task = new Thread("pub_sub") {     //create the task
                String mqttClientId = "rebirth_" + System.currentTimeMillis();
                MqttSyncClient m_client = new MqttSyncClient(mqttSrvURI, mqttClientId, username, userpwd);

                @Override
                public void run() {
                    m_client.initClient();
                    m_client.connect();

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    m_client.subscribe(topic, QoS.AT_LEAST_ONCE);  //subscribe the topic

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    m_client.send(topic, QoS.AT_MOST_ONCE, FuseSyncClientStressTest.msgGenerate()); // send the msg

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Message msg = m_client.receive();
                    if(msg != null){
                        System.out.println("from topic: " + msg.getTopic() + " receive " + new String(msg.getPayload()) );
                        success_cnt++;
                        msg.ack();
                    }

                    m_client.disconnect();
                    m_client.destroy();
                }
            };

            t_task.start();     // running the task

            try {
                Thread.sleep(1000);
                t_task.join();
                System.out.println("kill the task");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            count++;
        }

        if(success_cnt == loop_times){
            System.out.println("it's a good test!");
        }
    }

}
