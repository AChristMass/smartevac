package com.buildinnov.smartevac.plugin.indoor_localisation.services;


import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;




import java.util.logging.Logger;

/**
 *
 * Entity that receive location requests from clients (Android, ...)
 *
 * */
public class IndoorLocationServer  {

    final static Logger LOGGER = Logger.getLogger(IndoorLocationServer.class.getName());

    public IndoorLocationServer() {
        PusherOptions options = new PusherOptions().setCluster("smarevac_core_cluster");
        Pusher pusher = new Pusher("API_KEY",options);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange connectionStateChange) {
                LOGGER.info("State changed to "+connectionStateChange.getCurrentState()+" from "+connectionStateChange.getPreviousState());
            }

            @Override
            public void onError(String s, String s1, Exception e) {
                LOGGER.info("There was  a problem connecting !");
            }
        }, ConnectionState.ALL);

         Channel channel = pusher.subscribe("smarevac_core_IndoorLocation_channel");
         channel.bind("smarevac_core_IndoorLocation_positionVector", new SubscriptionEventListener() {
             @Override
             public void onEvent(String s, String s1, String s2) {
                 LOGGER.info("Received event with data"+s2);
             }
         });


         pusher.disconnect();
         pusher.connect();


    }


}
