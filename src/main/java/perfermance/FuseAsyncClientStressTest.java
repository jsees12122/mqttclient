package perfermance;

import com.thingdata.sdk.fuse.client.MqttAsyncClient;
import org.apache.commons.cli.*;
import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;

public class FuseAsyncClientStressTest {

    public static void main(String[] args) {
        Options options = FuseSyncClientStressTest.createOptions();
        CommandLine cmd = null;
        if (args.length == 0) {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.setWidth(120);
            formatter.printHelp("FuseAsyncClientStressTest", options);
            System.exit(-1);
        }

        // Parse commands
        try {
            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("parse failed. Reason:  "+ e.getMessage());
            System.exit(-1);
        }

        String mqttSrvURI = cmd.getOptionValue("mqtt-server-uri");  // String mqttSrvURI = "tcp://127.0.0.1:1883";
        String username = cmd.getOptionValue("mqtt-auth-username");
        String userpwd = cmd.getOptionValue("mqtt-auth-password");
        String topic = "$META/HAT-SaaS-Data/DataPoint_Model/热平衡实验室/内侧";
        // String topic = cmd.getOptionValue("topic-name");
        int total_send_num = Integer.parseInt(cmd.getOptionValue("batch-size"));
        System.out.println("total send number is "+total_send_num);

        // String send1_mqttClientId = "stress_send1_" + System.currentTimeMillis();
        String send_mqttClientId = "stress_send_" + System.currentTimeMillis();
        String sub_mqttClientId = "stress_sub_" + System.currentTimeMillis();

        MqttAsyncClient send_client = new MqttAsyncClient(mqttSrvURI, send_mqttClientId, username, userpwd);
        send_client.initClient();
        if(!send_client.initClient()){
            System.out.println("send client init failed");
            System.exit(-1);
        }

        if(!send_client.connect()){
            System.out.println("send client connect failed");
            System.exit(-1);
        }

        send_client.setSendBufferSize(1024*1024);

        //create a thread to subscribe the topic and receive the message
        FuseMQTTAsyncSuber suber = new FuseMQTTAsyncSuber(mqttSrvURI, username, userpwd, sub_mqttClientId, topic, total_send_num);
        Thread receiver = new Thread(suber);
        receiver.start();

        int success_send_num = 0;
        long start_send_time;
        long end_send_time;

//        while (send_client.isConnected()) {

            int send_loop_times = 5;
            for (int j = 0; j < send_loop_times; j++) {   // for loop 1000 times, and each time send <1000> message
                start_send_time = System.currentTimeMillis();

                for (int i = 0; i < total_send_num; i++) {
                    send_client.sendWithQoS2(topic, FuseSyncClientStressTest.msgGenerate());
                    success_send_num++;
                }
                if (success_send_num == total_send_num) {
                    end_send_time = System.currentTimeMillis();
                    System.out.println("Loop: "+j+": During " + start_send_time + " ~ " + end_send_time + " , send " +success_send_num+ " message to the topic: "+ topic );
                    success_send_num = 0;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//        }
    }
}

class FuseMQTTAsyncSuber implements Runnable{
    private String mqtt_srv_uri;
    private String user_name;
    private String user_pwd;
    private String mqtt_client_id;
    private String topic;

    //statistic
    private long recv_msg_count;
    private int  total_send_num;
    private long start_sub_time;
    private long end_sub_time;

    public FuseMQTTAsyncSuber(String mqttSrvURI, String username, String userpwd, String mqttClientId, String subTopic, int totalSendNum){
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

        if(!sub_client.initClient()){
            System.out.println("sub client init failed");
            System.exit(0);
        }

        if(!sub_client.connect()){
            System.out.println("sub client connect failed");
            System.exit(0);
        }

        sub_client.setReceiveBufferSize(1024*1024);

        if(topic.isEmpty()){
            System.out.println("sub topic is empty");
            System.exit(0);
        }

        byte[] qoses = sub_client.subscribe(topic, QoS.AT_LEAST_ONCE);

        while(true){
            Future<Message> fmsg = sub_client.receive();
            Message msg = null;

            if (recv_msg_count == 0) {
                start_sub_time = System.currentTimeMillis();
            }

            try {
//                msg = fmsg.await(500, TimeUnit.MICROSECONDS);
                msg = fmsg.await();
            } catch (Exception e) {
                System.out.println("receive message timeout...");
            }

            if(msg != null){
                String topic = msg.getTopic();
                String rmsg = new String(msg.getPayload());

                if (!topic.isEmpty() && !rmsg.isEmpty()) {
                    recv_msg_count++;
                }

                if (recv_msg_count == total_send_num) {
                    end_sub_time = System.currentTimeMillis();
                    System.out.println("During " + start_sub_time + " ~ " + end_sub_time + " ,from topic: " + topic + " receive " +recv_msg_count+ " message" );
                    recv_msg_count = 0;
                }

                msg.ack();
            }
        }

    }
}
