package org.apache.guacamole.event;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.event.TunnelCloseEvent;
import org.apache.guacamole.net.event.listener.Listener;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

/**
 * A Listener implementation intended to demonstrate basic use
 * of Guacamole's listener extension API.
 */
public class TutorialListener implements Listener {

    private static final String BASE_GUACAMOLE_URL = "http://localhost:8080/guacamole";
    private static final String SECURDEN_URL = "http://192.168.11.198:8000";

    // private static final Logger logger = 
    //      LoggerFactory.getLogger(TutorialListener.class);

    @Override
    public void handleEvent(Object event) throws GuacamoleException {
        // System.out.println("EVENT TYPE :: " + event.getClass().getName());
        // if (event instanceof TunnelConnectEvent) {
        //     System.out.println("Tunnel Connect Event UUID :: " + String.valueOf(((TunnelConnectEvent) event)
        //       .getTunnel().getUUID()));
        // }
        if (event instanceof TunnelCloseEvent) {
            System.out.println(" Tunnel Close Event  UUID " + String.valueOf(((TunnelCloseEvent) event)
            .getTunnel().getUUID()));
            String tunnel_id = String.valueOf(((TunnelCloseEvent) event).getTunnel().getUUID());
            String authtoken = getAuthtoken();
            String connectionID = getConnectionId(authtoken, tunnel_id);
            sendSecurdenRequest(connectionID, tunnel_id);
        }
        
        // logger.info("received Guacamole event notification");
    }

    public String sendRequests(String to_url, String method, String data, String params, String header_content){
        String return_response = "";
        
        try {

            // Set parameters
            if(params!=null &&  params.length() > 0){
                to_url+=params;
            }

            URL url = new URL(to_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            // Set Headers
            if(header_content!=null){
                connection.setRequestProperty("Content-Type", header_content);
            }

            // To Send Data - Set as true for receiving data
            connection.setDoOutput(true);

            //Set Request Body
            if(data != null){
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = data.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            System.out.println("CONNECTION STATUS -> " + connection.getResponseCode() + url);
            // if(connection.getResponseCode() != 200 && connection.getResponseCode() != 204){
            //     connection.disconnect();
            //     throw new Exception("Connection failed: Unable to connect to the server.");
            // }

            
            // Get Response Body
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return_response = response.toString();
            connection.disconnect();
        } 
        catch (Exception e) {
            e.printStackTrace();
        } 
        return return_response;   
    }

    public String getAuthtoken(){
        String url = BASE_GUACAMOLE_URL+"/api/tokens";
        String data = "username=guacadmin&password=guacadmin"; 
        String response = sendRequests(url, "POST", data, null, "application/x-www-form-urlencoded");
        JsonObject responseJson = JsonParser.parseString(response).getAsJsonObject();
        return responseJson.get("authToken").getAsString();
    }

    public String getConnectionId(String auth_token, String tunnel_id){
        String url = BASE_GUACAMOLE_URL+"/api/session/data/mysql/history/connections"+"?token="+auth_token;
        String params = "&contains=guac&order=-startDate";
        String response = sendRequests(url, "GET", null, params, null);
        String connection_id = null;
        JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();

        for (JsonElement each_response : jsonArray) {
            JsonObject each_response_object = each_response.getAsJsonObject();
            if(each_response_object.get("uuid").getAsString().equals(tunnel_id)){
                connection_id = each_response_object.get("connectionIdentifier").getAsString();
                break;
            }
        }
        return connection_id;
    }

    public void sendSecurdenRequest(String connection_id, String tunnel_id){
        String url = SECURDEN_URL +"/audit/guacamole_manage_active_remote_session";
        JsonObject json = new JsonObject();
        json.addProperty("connection_id", connection_id);
        json.addProperty("tunnel_id", tunnel_id);
        // JsonObject Jsondata = new JsonObject();
        // Jsondata.addProperty("GUACAMOLE_INPUT", json.toString());
        String data = "GUACAMOLE_INPUT="+json.toString();
        sendRequests(url, "POST", data, null, null);
    }

}