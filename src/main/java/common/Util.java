package common;

import com.thingdata.sdk.mbc.util.JsonMessage;
import com.thingdata.sdk.mbc.util.JsonMessageBuilder;

import java.util.Map;

public class Util {
    public static String constructMessage(String projectName,String event,Map properties){
        JsonMessage message = new JsonMessageBuilder().setProjectName(projectName).setEvent(event).setProperties(properties).createJsonMessage();
        return message.getJsonStr();
    }
}
