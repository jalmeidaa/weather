
package com.intuit.services.api;

import com.intuit.services.modules.WeatherBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherServer {
    private static final Logger logger = LoggerFactory.getLogger(WeatherServer.class);
    WeatherBO weatherBO = null;

    public WeatherServer(){
        weatherBO = new WeatherBO();
    }

    public static void main(String[] args) {
        WeatherServer weatherServer = new WeatherServer();


    }
}
