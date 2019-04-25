package com.intuit.services.api;

import com.intuit.services.core.JsonTransformer;
import com.intuit.services.modules.ElasticSearchClient;
import com.intuit.services.modules.WeatherBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static spark.Spark.*;

public class WeatherServer {
    private static final Logger logger = LoggerFactory.getLogger(WeatherServer.class);
    private static final HashMap<String, String> map = new HashMap<String, String>() {{
        put("admin", "admin");
    }};
    WeatherBO weatherBO = null;
    ElasticSearchClient elasticSearchClient = null;

    public WeatherServer() {
        weatherBO = new WeatherBO();
        elasticSearchClient = new ElasticSearchClient();
    }

    //lsof -i :7080
    //scp -i Desktop/Intuit/intuit.pem Desktop/Intuit/weather/build/libs/weather-all.jar ec2-user@ec2-52-41-190-120.us-west-2.compute.amazonaws.com:~
    //ssh -i Desktop/Intuit/intuit.pem ec2-user@ec2-52-41-190-120.us-west-2.compute.amazonaws.com
    public static void main(String[] args) {

        WeatherServer weatherServer = new WeatherServer();
        port(7080);
        logger.info("Serivce running on post 7080");

        before((request, response) -> {
            String partnerId = request.headers("partnerId");
            String secretKey = request.headers("secretKey");

            String dbPassword = map.get(partnerId);
            if (!(secretKey != null && secretKey.equals(dbPassword))) {
                halt(401, "Authentication required");
            }
        });

        post("/api/weather", "application/json", (request, response) -> {
            return weatherServer.weatherBO.create(request.body());
        }, new JsonTransformer());

        get("/api/weather/city/:city/:country", "application/json", (request, response) -> {
            return weatherServer.elasticSearchClient.getTemperatureByCity(request.params(":city"), request.params(":country"));
        }, new JsonTransformer());

        get("/api/weather/gps/:lon/:lat", "application/json", (request, response) -> {
            return weatherServer.elasticSearchClient.getTemperatureByGPS(request.params(":lon"), request.params(":lat"));
        }, new JsonTransformer());

        get("/api/weather/max", "application/json", (request, response) -> {
            return weatherServer.elasticSearchClient.getTemperatureMaxMin("max");
        }, new JsonTransformer());

        get("/api/weather/min", "application/json", (request, response) -> {
            return weatherServer.elasticSearchClient.getTemperatureMaxMin("min");
        }, new JsonTransformer());

        get("/api/weather/max_temp_by_time/:time", "application/json", (request, response) -> {
            return weatherServer.elasticSearchClient.getMaxTemperatureByTime(request.params(":time"),"desc");
        }, new JsonTransformer());

        get("/api/weather/min_temp_by_time/:time", "application/json", (request, response) -> {
            return weatherServer.elasticSearchClient.getMaxTemperatureByTime(request.params(":time"),"asc");
        }, new JsonTransformer());

    }
}
