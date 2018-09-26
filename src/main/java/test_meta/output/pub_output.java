package test_meta.output;

import com.thingdata.sdk.mbc.internal.MessageBusClient;
import com.thingdata.sdk.mbc.internal.MessageBusException;
import com.thingdata.sdk.mbc.internal.MessageLevel;
import com.thingdata.sdk.mbc.util.JsonMessage;
import com.thingdata.sdk.mbc.util.JsonMessageBuilder;
import common.Util;

import java.util.HashMap;
import java.util.Map;
import java.net.URISyntaxException;

public class pub_output {
    public static void main(String[] args){
        try {
            MessageBusClient client = new MessageBusClient("tcp://120.78.138.224:1883","11client-test-output");
            client.setUsername("dianqiyuan");
            client.setPassword("dqy123");
            client.connect();
            if(client.isConnected()){
                System.out.println("连接成功");
            }else {
                System.out.println("连接失败");
                return;
            }
            String message = Util.constructMessage("IIPEvaluation","Task_Op_Output",getproperties());
            System.out.println(message);
            client.send("$META/IIPEvaluation/Task_Op_Output/platformService/srvice_certainty/pub_sub", MessageLevel.EXACTLY_ONCE,message);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MessageBusException e) {
            e.printStackTrace();
        }
    }

    public static Map getproperties(){
        HashMap map = new HashMap();
        map.put("user_id","test_output_userid");
        map.put("task_id","test_output_taskid");
        map.put("start_time",System.currentTimeMillis());
        map.put("end_time",System.currentTimeMillis());
        map.put("task_name","test_output_taskname");
        map.put("task_type","platformService/srvice_certainty/pub_sub");
        map.put("qos_level",2);
        map.put("volume",1);
        map.put("response_time",1);
        map.put("batch_seq","msg_1");
        map.put("msg_seq",1);
        map.put("frequency","10");
        map.put("result_code",1);
        map.put("result_desc","test_desc");
        return map;
    }
}
