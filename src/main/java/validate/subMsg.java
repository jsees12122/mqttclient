package validate;

import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.thingdata.sdk.mbc.internal.Message;
import com.thingdata.sdk.mbc.internal.MessageBusClient;
import com.thingdata.sdk.mbc.internal.MessageBusException;
import com.thingdata.sdk.mbc.internal.MessageLevel;
import com.thingdata.sdk.mbc.service.MessageArrivedListener;
import com.thingdata.sdk.mbc.service.MessageBusClientService;

public class subMsg {
    private final static String local_topic = "/persistence/test";

    private MessageBusClientService client;

    private static List<JSONObject> list = new ArrayList<JSONObject>();

    class OnMessageArrived implements MessageArrivedListener {
        public void onMessageArrived(Message message) {
            String topic = message.getTopic();
            String jsonContent = new String(message.getPayload());
            System.out.println("收到一条来自主题【"+topic+"】的消息：");
            System.out.println(jsonContent);
            System.out.println();
            System.out.println("----------------------------------");
            JSONObject obj = JSONObject.parseObject(jsonContent);
            list.add(obj);
        }
    }

    public subMsg(String client) throws URISyntaxException, MessageBusException {
        this.client = new MessageBusClient("tcp://www.thing-data.com:1883", client);
        this.client.connect();
    }

    public static void main(String[] args) throws URISyntaxException, MessageBusException {
        listener listener = new listener();
        while (true) {
            listener.produce("$META/IIPEvaluation/Task_Statistic/persistence_test/pub_sub");
            String objstr = listener.consumer();
            System.out.println(objstr);
            if(objstr != null) {
                JSONObject object = JSONObject.parseObject(objstr);
                JSONObject pro = object.getJSONObject("properties");
                String hash_info = (String) pro.get("hash_info");
                String hash_val = (String) pro.get("hash_val");
                subMsg demo = new subMsg(getClient(hash_info));
                System.out.println("是否连接：" + demo.client.isConnected());
                demo.subscribe();
                try {
                    Thread.sleep(10*60*1000);
                    boolean status = validate(hash_info, hash_val);
                    if (status) {
                        System.out.println("success");
                    } else {
                        System.out.println("测试失败");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("测试失败");
                }finally {
                    demo.client.disconnect();
                }
            }else {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void subscribe() throws MessageBusException {
        this.client.subscribe(local_topic, MessageLevel.EXACTLY_ONCE, new OnMessageArrived());
    }

    private static String getClient(String hash_info){
        String[] client = hash_info.split(":");
        return client[0];
    }

    private static boolean validate(String hash_info,String hash_val) throws Exception {
        String[] seq = hash_info.split(":");
        String[] seqlist = seq[1].split(",");
        String hash_origin = "";
        for(String s : seqlist){
            for(JSONObject obj : list){
                String msg_seq = obj.getString("msg_seq");
                String m_seq = msg_seq.split("_")[1];
                if(m_seq.equals(s)){
                    hash_origin += obj.getString("msg");
                }
            }
        }
        list.clear();
        String hash_val_2 = md5(hash_origin);
        System.out.println(hash_origin);
        if(hash_val.equals(hash_val_2)){
            System.out.println("哈希值相同");
            return true;
        }
        System.out.println("哈希值不相同");
        return false;
    }

    public static String md5(String str) throws Exception {
        if (str == null || str.length() == 0) {
            throw new Exception("zero");
        }
        StringBuffer hexString = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] hash = md.digest();
            for (int i = 0; i < hash.length; i++) {
                if ((0xff & hash[i]) < 0x10) {
                    hexString.append("0" + Integer.toHexString((0xFF & hash[i])));
                } else {
                    hexString.append(Integer.toHexString(0xFF & hash[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hexString.toString();
    }
}
