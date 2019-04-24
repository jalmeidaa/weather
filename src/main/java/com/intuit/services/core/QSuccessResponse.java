package com.intuit.services.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class QSuccessResponse extends QResponse
{
  Object response = null;
  private static Gson gson = new GsonBuilder().serializeNulls().create();
  
  public QSuccessResponse() {
    super(true);
  }
  
  public QSuccessResponse(Object response) {
    super(true);
    this.response = response;
  }
  
  public Object getResponse() {
    return response;
  }
  
  public com.google.gson.JsonElement getAsJsonObject() {
    return gson.toJsonTree(response);
  }
}
