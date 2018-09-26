package deyisdk;

import com.thingdata.sdk.fuse.client.MqttSyncClient;
import common.Util;
import org.fusesource.mqtt.client.QoS;
import test_meta.input.pub_input;

public class task implements Runnable {
    private MqttSyncClient client;
    public task(MqttSyncClient client){
        this.client = client;
    }
    public void run() {
        System.out.println("开始：" + System.currentTimeMillis());
        long starttime = System.currentTimeMillis();
        for(int i = 0;i<2000;i++){
            String message = Util.constructMessage("IIPEvaluation","Task_Input", pub_input.getproperties());
            client.send("$META/IIPEvaluation/Task_Input/connection_test/pub_sub", QoS.AT_MOST_ONCE,message);
            System.out.println(i);
            long endtime = System.currentTimeMillis();
            if(endtime - starttime > 1000){
                break;
            }
        }
        System.out.println("结束：" + System.currentTimeMillis());
    }
}
