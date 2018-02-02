package com.dtolabs.rundeck.plugin.http_notifications;

import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

@Plugin(service="Notification",name="http-notification")
@PluginDescription(title="Notification Plugin", description="An plugin for Rundeck Notifications.")
public class HttpNotificationsPlugin implements NotificationPlugin{
	
	private static final String METHOD_POST="POST";
	private static final String METHOD_PUT="PUT";
	private static final String METHOD_DELETE="DELETE";
	private static final String METHOD_GET="GET";
	private static final int HTTP_OK=200;

    @PluginProperty(name = "urlInput",title = "url",description = "Complete URL to which send the notification. example: http://machine1/notification")
    private String urlInput;
    
    @PluginProperty(name = "method",title = "http method",description = "The http method that should be used. Options: POST, PUT, GET, DELETE")
    private String method;
    
    @PluginProperty(name = "body",title = "http body",description = "(optional) Valid for POST or PUT methods. Content to be sent as body.")
    private String body;
    
    @PluginProperty(name = "contentType",title = "content type",description = "(optional) Valid for POST or PUT methods. Indicates the content type of the body..")
    private String contentType;

    public HttpNotificationsPlugin(){

    }

    public boolean postNotification(String trigger, Map executionData, Map config) {
        if(METHOD_POST.equals(method) || METHOD_PUT.equals(method)) {
        	return methodBody();
        } else if(METHOD_GET.equals(method) || METHOD_DELETE.equals(method)) {
        	return methodNoBody();
        } else {
        	//unknown method?
        	method = METHOD_GET;
        	return methodNoBody();
        }
    }

    public boolean methodNoBody(){
        System.err.println("Calling methodNoBody");
        try{
            URL url = new URL(urlInput);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            
            
            StringBuilder result = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        
            System.err.println(result.toString());//TODO:remove
            
            if( HTTP_OK == conn.getResponseCode() ) {
            	return Boolean.TRUE;
            } else {
            	return Boolean.FALSE;
            }
            

        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
            return Boolean.FALSE;
        } finally {
        	//TODO: safely close objects
        }

    }

    public boolean methodBody(){
        System.err.println("Calling methodBody");
        
        try{
	        URL url = new URL(urlInput);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod(method);
	        conn.setDoOutput(true);
	        
	        //write request
	        byte[] out = body.getBytes(StandardCharsets.UTF_8);
	        	        conn.setFixedLengthStreamingMode(out.length);
	        if( contentType != null && !contentType.isEmpty() ) {
	        	conn.setRequestProperty("Content-Type", contentType);
	        } else {
	        	conn.setRequestProperty("Content-Type", "text/plain");
	        }
	        conn.connect();
	        
	        OutputStream os = conn.getOutputStream();
	        os.write(out);
	        os.close();
	        
	        //now read a response
	        StringBuilder result = new StringBuilder();
	        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        
            System.err.println(result.toString());//TODO:remove
	        
	        if( HTTP_OK == conn.getResponseCode() ) {
            	return Boolean.TRUE;
            } else {
            	return Boolean.FALSE;
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return Boolean.FALSE;
        } finally {
        	//TODO: safely close objects
        }
    }

}
