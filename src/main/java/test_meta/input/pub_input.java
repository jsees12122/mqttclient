package test_meta.input;

import com.thingdata.sdk.mbc.internal.MessageBusClient;
import com.thingdata.sdk.mbc.internal.MessageBusException;
import com.thingdata.sdk.mbc.internal.MessageLevel;
import common.Util;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class pub_input {
    public static void main(String[] args){
        try {
            MessageBusClient client = new MessageBusClient("tcp://www.thing-data.com:1883","client-test-input");
            client.connect();
            if(client.isConnected()){
                System.out.println("连接成功");
            }else {
                System.out.println("连接失败");
                return;
            }
            String message = Util.constructMessage("IIPEvaluation","Task_Input",getproperties());
            System.out.println(message);
            client.send("$META/IIPEvaluation/Task_Input/connection_test/pub_sub", MessageLevel.AT_MOST_ONCE,message);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MessageBusException e) {
            e.printStackTrace();
        }
    }

    public static Map getproperties(){
        HashMap map = new HashMap();
        map.put("user_id","test_input_userid");
        map.put("task_id","test_input_taskid");
        map.put("start_time",System.currentTimeMillis());
        map.put("task_name","test_input_taskname");
        map.put("task_type","connection_test/pub_sub");
        map.put("mqtt_url","127.0.0.1");
        map.put("mqtt_username","username");
        map.put("mqtt_password","password");
        map.put("http_url","http://domain.com:80/health");
        map.put("topic","test_topic");
        map.put("duration","10");
        return map;
    }
}
