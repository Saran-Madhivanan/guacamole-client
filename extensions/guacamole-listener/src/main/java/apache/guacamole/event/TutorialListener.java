package org.apache.guacamole.event;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.event.TunnelCloseEvent;
import org.apache.guacamole.net.event.TunnelConnectEvent;
import org.apache.guacamole.net.event.listener.Listener;
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
            System.out.println("TunnelConnectEvent UUID :: " + String.valueOf(((TunnelConnectEvent) event)
              .getTunnel().getUUID()));
        }
        if (event instanceof TunnelCloseEvent) {
            System.out.println(" TunnelCloseEvent  UUID :: " + String.valueOf(((TunnelCloseEvent) event)
            .getTunnel().getUUID()));
            sendAPIRequest(String.valueOf(((TunnelCloseEvent) event).getTunnel().getUUID()));
        }
        
        // logger.info("received Guacamole event notification");
    }

    public void sendAPIRequest(String UUID){
        try {
            System.out.println("sendAPIRequest -->" + UUID);
            String jsonPayload = "{\"tunnel_id\": \"" + UUID + "\"}";
            URL url = new URL("http://192.168.11.84:8000/accountmanagement/web_rdp_tunnel_id");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);
            connection.disconnect();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}