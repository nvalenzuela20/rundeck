package com.dtolabs.rundeck.plugin.http_notifications;

import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import java.util.*;

@Plugin(service="Notification",name="http-notification")
@PluginDescription(title="Notification Plugin", description="An plugin for Rundeck Notifications.")
public class HttpNotificationPlugin implements NotificationPlugin{

    @PluginProperty(name = "url",title = "Http-Notification",description = "Test")
    private String url;

    public HttpNotificationPlugin(){

    }

    public boolean postNotification(String trigger, Map executionData, Map config) {
        System.err.printf("Trigger %s fired for %s, configuration: %s\n",trigger,executionData,config);
        System.err.printf("Nicole test Local field example is: %s\n",url);
        return true;
    }

}