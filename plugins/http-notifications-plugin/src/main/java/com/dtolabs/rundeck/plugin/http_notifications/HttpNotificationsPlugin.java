package com.dtolabs.rundeck.plugin.http_notifications;

import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import java.util.*;
import java.io.*;
import java.net.*;

@Plugin(service="Notification",name="http-notification")
@PluginDescription(title="Notification Plugin", description="An plugin for Rundeck Notifications.")
public class HttpNotificationsPlugin implements NotificationPlugin{

    @PluginProperty(name = "urlInput",title = "Http-Notification",description = "Test Nicole")
    private String urlInput;

    public HttpNotificationsPlugin(){

    }

    public boolean postNotification(String trigger, Map executionData, Map config) {
        System.err.printf("Trigger %s fired for %s, configuration: %s\n",trigger,executionData,config);
        System.err.printf("Nicole test Local field example is: %s\n",urlInput);

        return methodGet();
    }

    public boolean methodGet(){
        System.err.println("Calling methodGet");
        try{
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlInput);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                System.err.println("Processing line " + line);
                result.append(line);
            }
            rd.close();
        
            System.err.println(result.toString());
            return Boolean.TRUE;

        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
            return Boolean.FALSE;
        }

    }

}
