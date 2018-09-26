package validate;

import com.thingdata.sdk.mbc.internal.Message;
import com.thingdata.sdk.mbc.internal.MessageBusClient;
import com.thingdata.sdk.mbc.internal.MessageBusException;
import com.thingdata.sdk.mbc.internal.MessageLevel;
import com.thingdata.sdk.mbc.service.MessageArrivedListener;
import com.thingdata.sdk.mbc.service.MessageBusClientService;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class listener {
    private static ArrayList<String> list = new ArrayList<String>();
    private MessageBusClientService clientService;

    public listener(){
        try {
            clientService = new MessageBusClient("tcp://www.thing-data.com:1883","client-id");
            clientService.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MessageBusException e) {
            e.printStackTrace();
        }

    }

     private MessageArrivedListener listener = new MessageArrivedListener() {
        public void onMessageArrived(Message message) {
            String jsonstr = new String(message.getPayload());
            synchronized (list){
                list.add(jsonstr);
            }
        }
    };

    public void produce(String topic){
        try {
            clientService.subscribe(topic, MessageLevel.EXACTLY_ONCE,listener);
        } catch (MessageBusException e) {
            e.printStackTrace();
        }
    }

    public String consumer(){
        String result = null;
        synchronized (list){
            if(list.size() != 0){
                result = list.remove(0);
            }
        }
        return result;
    }
}
