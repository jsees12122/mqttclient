package perfermance;

import com.thingdata.sdk.fuse.client.MqttSyncClient;
import com.thingdata.sdk.fuse.util.JsonMessage;
import com.thingdata.sdk.fuse.util.JsonMessageBuilder;
import org.apache.commons.cli.*;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;

import java.util.HashMap;

public class FuseSyncClientStressTest {

    public static String msgGenerate() {
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("CollectTime", "1000 Blackweill Dr., NC");
        props.put("TestMachine", "101/heat_machine");
        props.put("ExperimentNum", "5210001");
        props.put("DataValue", "12345");
        props.put("NameC", "hat_stress");
        props.put("NameE", String.valueOf(1));

        HashMap<String, Object> msg_setting = new HashMap<String, Object>();
        msg_setting.put("$Batch_ID","hat_stress");
        msg_setting.put("$Batch_Total_No",10000);
        msg_setting.put("$Batch_Seq_No", 1);

        JsonMessage jsonMessage = new JsonMessageBuilder()
                .setProjectName("HAT-SaaS-Data")
                .setEvent("DataPoint_Model")
                .setTimestamp(System.currentTimeMillis())
                .setProperties(props)
                .setMessageSetting(msg_setting)
                .createJsonMessage();
        return jsonMessage.getJsonStr();
    }

    public static void main(String[] args) {
        Options options = createOptions();
        CommandLine cmd = null;
        if (args.length == 0) {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.setWidth(120);
            formatter.printHelp("FuseClientStressTest", options);
            System.exit(-1);
        }

        // Parse commands
        try {
            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("parsing failed. Reason:  "+ e.getMessage());
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

        MqttSyncClient send_client = new MqttSyncClient(mqttSrvURI, send_mqttClientId, username, userpwd);
        send_client.initClient();
        send_client.connect();
        send_client.setSendBufferSize(1024*1024);

        //todo: create a thread to subscribe the topic and receive the message
        FuseMQTTSuber suber = new FuseMQTTSuber(mqttSrvURI, username, userpwd, sub_mqttClientId, topic, total_send_num);
        Thread receiver = new Thread(suber);
        receiver.start();

        int success_send_num = 0;
        long start_send_time;
        long end_send_time;

        while (send_client.isConnected()) {

            int send_loop_times = 1000;
            for (int j = 0; j < send_loop_times; j++) {   // for loop 1000 times, and each time send <1000> message
                start_send_time = System.currentTimeMillis();

                for (int i = 0; i < total_send_num; i++) {
                    send_client.send(topic, QoS.AT_MOST_ONCE, msgGenerate());
                    success_send_num++;
                }
                if (success_send_num == total_send_num) {
                    end_send_time = System.currentTimeMillis();
                    System.out.println("Loop: "+j+": During " + start_send_time + " ~ " + end_send_time + " ,successfully send " +success_send_num+ " message to the topic: "+ topic );
                    success_send_num = 0;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Options createOptions() {
        Options options = new Options();
        options.addOption(Option.builder("mUri")
                .longOpt("mqtt-server-uri")
                .hasArg()
                .argName("tcp://120.77.42.5:1883")
                .type(String.class)
                .required(false)
                .desc("MQTT Server URI")
                .build());

        options.addOption(Option.builder("uname")
                .longOpt("mqtt-auth-username")
                .hasArg()
                .argName("dianqiyuan")
                .type(String.class)
                .required(false)
                .desc("MQTT Auth Username")
                .build());

        options.addOption(Option.builder("pwd")
                .longOpt("mqtt-auth-password")
                .hasArg()
                .argName("dqy123")
                .type(String.class)
                .required(false)
                .desc("MQTT Auth password")
                .build());

        options.addOption(Option.builder("topic")
                .longOpt("topic-name")
                .hasArg()
                .argName("$META/HAT-SaaS-Data/DataPoint_Model/热平衡实验室/内侧")
                .type(String.class)
                .required(false)
                .desc("mqtt server topic name")
                .build());

        options.addOption(Option.builder("tn")
                .longOpt("batch-size")
                .hasArg()
                .argName("2000")
                .type(String.class)
                .required(false)
                .desc("Sending batch size")
                .build());

        return options;
    }
}

class FuseMQTTSuber implements Runnable{
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

    public FuseMQTTSuber(String mqttSrvURI, String username, String userpwd, String mqttClientId, String subTopic, int totalSendNum){
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
        MqttSyncClient sub_client = new MqttSyncClient(mqtt_srv_uri, mqtt_client_id, user_name, user_pwd);

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
            Message msg = sub_client.receive();

            if (recv_msg_count == 0) {
                start_sub_time = System.currentTimeMillis();
            }

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
