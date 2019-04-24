package com.intuit.services.modules;

import com.google.gson.*;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ElasticSearchClient {

    final static Logger logger = LoggerFactory.getLogger(ElasticSearchClient.class);

    final static String weatherIndex = "search-weather-prod-44h5kuzu2yqaekvovd6esfdlle.us-west-2.es.amazonaws.com";
    final static String nameIndex = "weather*";

    RestHighLevelClient client;

    public ElasticSearchClient() {
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(weatherIndex, 443, "https")));
    }

    public JsonObject getTemperatureByGPS(String param1, String param2) {
        JsonObject result = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        SearchResponse searchResponse = null;
        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            searchSourceBuilder.query(QueryBuilders.termQuery("city.coord.lon", param1));

            SearchRequest searchRequest = new SearchRequest(nameIndex);
            searchRequest.source(searchSourceBuilder);

            searchResponse = client.search(searchRequest);

            if (searchResponse != null) {
                JsonArray array = new JsonArray();
                for (SearchHit searchHit : searchResponse.getHits()) {
                    String res = searchHit.getSourceAsString();
                    JsonElement temp = jsonParser.parse(res);
                    if (temp.getAsJsonObject().get("city").getAsJsonObject().get("coord").getAsJsonObject().get("lat").getAsString().equalsIgnoreCase(param2)) {
                        array.add(temp.getAsJsonObject());
                    }
                }
                result.add("response", array);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public JsonObject getTemperatureByCity(String param1, String param2) {
        JsonObject result = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        SearchResponse searchResponse = null;
        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            searchSourceBuilder.query(QueryBuilders.matchQuery("city.name", param1));

            SearchRequest searchRequest = new SearchRequest(nameIndex);
            searchRequest.source(searchSourceBuilder);

            searchResponse = client.search(searchRequest);

            if (searchResponse != null) {
                JsonArray array = new JsonArray();
                for (SearchHit searchHit : searchResponse.getHits()) {
                    String res = searchHit.getSourceAsString();
                    JsonElement temp = jsonParser.parse(res);
                    if (temp.getAsJsonObject().get("city").getAsJsonObject().get("country").getAsString().equalsIgnoreCase(param2)) {
                        array.add(temp.getAsJsonObject());
                    }
                }
                result.add("response", array);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public JsonObject getTemperatureMaxMin(String sortOrder) {
        JsonObject result = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        SearchResponse searchResponse = null;
        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            if (sortOrder.equalsIgnoreCase("max")) {
                searchSourceBuilder.query(QueryBuilders.matchAllQuery())
                        .sort(SortBuilders.fieldSort("data.main.temp").order(SortOrder.DESC)).size(1);
            } else if (sortOrder.equalsIgnoreCase("min")) {
                searchSourceBuilder.query(QueryBuilders.matchAllQuery())
                        .sort(SortBuilders.fieldSort("data.main.temp").order(SortOrder.ASC)).size(1);
            } else {
                searchSourceBuilder.query(QueryBuilders.matchAllQuery())
                        .sort(SortBuilders.fieldSort("data.main.temp")).size(1);
            }

            SearchRequest searchRequest = new SearchRequest(nameIndex);
            searchRequest.source(searchSourceBuilder);

            searchResponse = client.search(searchRequest);
            double temperature = Integer.MIN_VALUE;
            if (searchResponse != null) {
                JsonArray array = new JsonArray();
                for (SearchHit searchHit : searchResponse.getHits()) {
                    String res = searchHit.getSourceAsString();
                    JsonElement temp = jsonParser.parse(res);
                    array.add(temp.getAsJsonObject().get("city").getAsJsonObject());
                    for (JsonElement el : temp.getAsJsonObject().get("data").getAsJsonArray()) {
                        double tempValue = el.getAsJsonObject().get("main").getAsJsonObject().get("temp").getAsDouble();
                        if (tempValue > temperature) {
                            temperature = tempValue;
                        }
                    }
                    temperature = Math.round((temperature - 273.15) * 100.0) / 100.0;
                    array.get(0).getAsJsonObject().add("temp", new JsonPrimitive(temperature + " °C"));
                }
                result.add("response", array);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public JsonObject getMaxTemperatureByTime(String time, String odr) {
        JsonObject result = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        SearchResponse searchResponse = null;
        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            SortOrder order = SortOrder.ASC;

            if (odr.equalsIgnoreCase("desc")) {
                order = SortOrder.DESC;
            }
            searchSourceBuilder.query(QueryBuilders.matchQuery("data.dt_txt", time))
                    .sort(SortBuilders.fieldSort("data.main.temp").order(order)).size(1);

            String[] includeFields = new String[]{"city.name", "data.main", "data.dt", "data.dt_txt"};
            searchSourceBuilder.fetchSource(includeFields, new String[]{});

            SearchRequest searchRequest = new SearchRequest(nameIndex);
            searchRequest.source(searchSourceBuilder);

            searchResponse = client.search(searchRequest);

            if (searchResponse != null) {
                JsonArray array = new JsonArray();
                for (SearchHit searchHit : searchResponse.getHits()) {
                    String res = searchHit.getSourceAsString();
                    JsonElement temp = jsonParser.parse(res);
                    for (JsonElement el : temp.getAsJsonObject().get("data").getAsJsonArray()) {
                        if (el.getAsJsonObject().get("dt_txt").getAsString().equalsIgnoreCase(time)) {
                            array.add(el);
                            break;
                        }
                    }
                    array.get(0).getAsJsonObject().add("city", temp.getAsJsonObject().get("city").getAsJsonObject());
                }
                result.add("response", array);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}

