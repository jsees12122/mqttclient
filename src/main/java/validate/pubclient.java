package validate;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class pubclient {
    private List<JSONObject> createVirtualMsg(){
        List<JSONObject> VirtualMsg = new ArrayList<JSONObject>();
        for(int i = 0;i<100;i++){
            String seq = "msg_" + i;
            String randomMsg = "testmsg";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg_seq",seq);
            jsonObject.put("msg",randomMsg);
            VirtualMsg.add(jsonObject);
        }
        return VirtualMsg;
    }


}
