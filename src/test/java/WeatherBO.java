import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.intuit.services.modules.ElasticSearchClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class WeatherBO {
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = new Gson();
    ElasticSearchClient elasticSearchClient;
    String cityName = "Antarctica";
    String cityName1 = "Nong Bua Daeng";

    @Before
    public void setUp() {
        elasticSearchClient = new ElasticSearchClient();
        gsonBuilder.serializeNulls();
        gson = gsonBuilder.create();
    }

    @Test
    public void runTest() throws Exception {
        TestGetTemperatureMin();
        TestGetTemperatureMax();
    }

    public void TestGetTemperatureMin() throws Exception {
        String json = String.format("{\"response\": [{\"name\": \"%s\"}]}", cityName);
        JsonObject returnObj = gson.fromJson(json, JsonObject.class);

        Map<String, String[]> map = new HashMap<>();

        JsonObject rtnObj = elasticSearchClient.getTemperatureMaxMin("min");

        if (rtnObj.getAsJsonObject().get("response").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString().equalsIgnoreCase(cityName)) {
            Assert.assertTrue("Passed", true);
        }else{
            Assert.fail("Failed");
        }
    }

    public void TestGetTemperatureMax() throws Exception {
        String json = String.format("{\"response\": [{\"name\": \"%s\"}]}", cityName1);
        JsonObject returnObj = gson.fromJson(json, JsonObject.class);

        Map<String, String[]> map = new HashMap<>();

        JsonObject rtnObj = elasticSearchClient.getTemperatureMaxMin("max");

        if (rtnObj.getAsJsonObject().get("response").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString().equalsIgnoreCase(cityName1)) {
            Assert.assertTrue("Passed", true);
        }else{
            Assert.fail("Failed");
        }
    }
}

