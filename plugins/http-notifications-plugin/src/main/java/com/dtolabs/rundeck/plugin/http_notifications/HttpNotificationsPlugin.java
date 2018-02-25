package com.dtolabs.rundeck.plugin.http_notifications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;

@Plugin(service="Notification",name="http-notification")
@PluginDescription(title="Notification Plugin", description="An plugin for Rundeck Notifications.")
public class HttpNotificationsPlugin implements NotificationPlugin{
	
	public static final Logger logger = Logger.getLogger(HttpNotificationsPlugin.class);
	
	private static final String METHOD_POST = "POST";
	private static final String METHOD_PUT = "PUT";
	private static final String METHOD_DELETE = "DELETE";
	private static final String METHOD_GET = "GET";
	private static final int HTTP_OK = 200;
	private static final String EXP_REG_VALID_URL = "^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\?=.-]*)*\\/?$";
	private static final int  READ_TIME_OUT = 60000;
	private static final int CONNECT_TIME_OUT = 60000;
	

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

    /**
     * Call a page depending on the type of method to call
     * 
     * @param trigger 
     * @param executionData
     * @param config
     * @return the flag with the answer successful or not  
     */
    public boolean postNotification(String trigger, Map executionData, Map config) {
    	
    	logger.debug("Calling postNotification");
    	
    	if(urlInput != null && (!urlInput.toUpperCase().startsWith("HTTP://", 0) || urlInput.toUpperCase().startsWith("HTTPS://"))) {
    		urlInput = "https://" + urlInput;
    	}
    	
    	logger.debug("getting the information from the URL: " + urlInput);
    	
    	if(METHOD_POST.equals(method) || METHOD_PUT.equals(method)) {
        	return callPullorPost();
        	
        } else if(METHOD_GET.equals(method) || METHOD_DELETE.equals(method)) {
        	return callGetOrDelete();
        	
        } else {
        	method = METHOD_GET;
        	return callGetOrDelete();
        	
        }
    }

    
    /**
     * call a page with the method GET or DELETE 
     * 
     * @return the flag with the answer successful or not
     */    
    public boolean callGetOrDelete(){
    	logger.debug("Calling callGetOrDelete");
        try{
        	
        	Pattern pat = Pattern.compile(EXP_REG_VALID_URL);
        	Matcher mat = pat.matcher(urlInput);
        	
        	if (!mat.matches()) {
        		logger.error("URL " + urlInput + " is not is valid");
        		return Boolean.FALSE;
            } 
        	        	
            URL url = new URL(urlInput);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(CONNECT_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);
            
            conn.connect();
                   
            if( HTTP_OK == conn.getResponseCode() ) {
            	return Boolean.TRUE;
            } else {
            	return Boolean.FALSE;
            }
            

        }catch (UnknownHostException ue){
        	logger.error("the host does not exist " + urlInput);
            return Boolean.FALSE;
            
        }catch ( MalformedURLException  me){
        	logger.error("Error ", me);
            return Boolean.FALSE;
        
        } catch (ProtocolException pe) {
        	logger.error("Efdrror ", pe);
            return Boolean.FALSE;
        
        } catch (SocketTimeoutException ste) {
        	logger.error("a TimeOut has occurred when send a notification");
            return Boolean.FALSE;
        
        } catch (IOException ioe) {
			logger.error("Error ", ioe);
            return Boolean.FALSE;
		}

    }

    
    /**
     * call a page with the method PULL or POST
     * 
     * @return the flag with the answer successful or not
     */
    public boolean callPullorPost(){
    	logger.debug("Calling callPullorPost");
        
        try{
        	
        	Pattern pat = Pattern.compile(EXP_REG_VALID_URL);
        	Matcher mat = pat.matcher(urlInput);
        	
        	if (!mat.matches()) {
        		logger.error("URL " + urlInput + " is not is valid");
        		return Boolean.FALSE;
            }       	
        	
	        URL url = new URL(urlInput);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod(method);
	        conn.setDoOutput(true);
	        conn.setConnectTimeout(CONNECT_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);
	        
	        byte[] out = body.getBytes(StandardCharsets.UTF_8);
	        	        conn.setFixedLengthStreamingMode(out.length);
	        if( contentType != null && !contentType.isEmpty() ) {
	        	conn.setRequestProperty("Content-Type", contentType);
	        } else {
	        	conn.setRequestProperty("Content-Type", "text/plain");
	        }
	        conn.connect();
	        
	        try (
	        	
	        	OutputStream os = conn.getOutputStream();
			        
		    ){
	        	os.write(out);	
	        	os.close();
	        }
	        
	        if( HTTP_OK == conn.getResponseCode() ) {
            	return Boolean.TRUE;
            } else {
            	return Boolean.FALSE;
            }
        
        }catch (UnknownHostException ue){
        	logger.error("the host does not exist " + urlInput);
            return Boolean.FALSE;
            
        }catch ( MalformedURLException  me){
        	logger.error("Error ", me);
            return Boolean.FALSE;
        
        } catch (ProtocolException pe) {
        	logger.error("Error ", pe);
            return Boolean.FALSE;
        
        } catch (SocketTimeoutException ste) {
        	logger.error("a TimeOut has occurred when send a notification");
            return Boolean.FALSE;
       
        } catch (IOException ioe) {
			logger.error("Error ", ioe);
            return Boolean.FALSE;
		}
        
    }
        

}
