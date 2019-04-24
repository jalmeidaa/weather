package com.intuit.services.modules;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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

    public JsonObject getTemperature(String param1, String param2) {
        JsonObject result = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchQuery("city.name",param1));
//            searchSourceBuilder.query(QueryBuilders.matchQuery("city.country",param2));


            SearchRequest searchRequest = new SearchRequest(nameIndex);
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = null;

            searchResponse = client.search(searchRequest);

            if (searchResponse != null) {
                JsonArray array = new JsonArray();
                for (SearchHit searchHit : searchResponse.getHits()) {
                    String res = searchHit.getSourceAsString();
                    JsonElement temp = jsonParser.parse(res);
                    array.add(temp.getAsJsonObject());
                }
                result.add("response", array);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}

