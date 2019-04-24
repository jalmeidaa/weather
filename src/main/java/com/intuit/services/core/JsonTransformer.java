package com.intuit.services.core;

import com.google.gson.GsonBuilder;

public class JsonTransformer implements spark.ResponseTransformer {
  public JsonTransformer() {}
  
  private static GsonBuilder gsonBuilder = new GsonBuilder();
  private static com.google.gson.Gson gson = getGson();
  
  private static com.google.gson.Gson getGson() {
    gsonBuilder.serializeNulls();
    return gsonBuilder.create();
  }
  
  public String render(Object model) {
    return gson.toJson(model);
  }
}
