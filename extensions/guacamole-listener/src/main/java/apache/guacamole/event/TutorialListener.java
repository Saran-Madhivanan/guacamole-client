package org.apache.guacamole.event;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.event.listener.Listener;
import org.apache.guacamole.net.event.TunnelCloseEvent;
import org.apache.guacamole.net.event.TunnelConnectEvent;
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
        // System.out.println("Inside Handle Event");
        System.out.println("EVENT TYPE :: " + event.getClass().getName());
        if (event instanceof TunnelConnectEvent) {
            System.out.println("UUID :: " + String.valueOf(((TunnelConnectEvent) event)
              .getTunnel().getUUID()));
        }
        if (event instanceof TunnelCloseEvent) {
            System.out.println("UUID :: " + String.valueOf(((TunnelCloseEvent) event)
              .getTunnel().getUUID()));
        }

        // logger.info("received Guacamole event notification");
    }

}