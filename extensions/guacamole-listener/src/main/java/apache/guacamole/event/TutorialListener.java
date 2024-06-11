package org.apache.guacamole.event;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.event.TunnelCloseEvent;
import org.apache.guacamole.net.event.TunnelConnectEvent;
import org.apache.guacamole.net.event.listener.Listener;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

/**
 * A Listener implementation intended to demonstrate basic use
 * of Guacamole's listener extension API.
 */
public class TutorialListener implements Listener {

    // private static final Logger logger = 
    //      LoggerFactory.getLogger(TutorialListener.class);

    @Override
    public void handleEvent(Object event) throws GuacamoleException {
        System.out.println("Inside Handle Event");
        System.out.println("EVENT TYPE :: " + event.getClass().getName());
        if (event instanceof TunnelConnectEvent) {
            System.out.println("UUID :: " + String.valueOf(((TunnelConnectEvent) event)
              .getTunnel().getUUID()));
        }
        if (event instanceof TunnelCloseEvent) {
            System.out.println("UUID :: " + String.valueOf(((TunnelCloseEvent) event)
            .getTunnel().getUUID()));
            sendAPIRequest(event.getClass().getName(), String.valueOf(((TunnelCloseEvent) event).getTunnel().getUUID()));
        }
        
        // logger.info("received Guacamole event notification");
    }

    public void sendAPIRequest(String event_type, String UUID){
        String primary_url = "http://192.168.11.84:8000";
        JSONObject requestBody = new JSONObject().put("tunnel_id",UUID);


        try {
            HttpResponse<JsonNode> response = Unirest.post(primary_url+"/accountmanagement/web_rdp_tunnel_id")
            .header("Content-Type", "application/json")
            .body(requestBody)
            .asJson();
            if(response.getStatus() == 200) {
                System.out.println("Request to Primary successful");
            } else{
                System.out.println("Request to Primary Failed");
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

}