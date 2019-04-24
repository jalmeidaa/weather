package com.intuit.services.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class QErrorResponse extends QResponse
{
  Error error = null;
  private static Gson gson = new GsonBuilder().serializeNulls().create();
  
  public static class Error {
    int errorCode = -1;
    String errorMessage = "";
    Object errorDetails = null;
    
    public Error(int errorCode, String errorMessage) {
      this.errorCode = errorCode;
      this.errorMessage = errorMessage;
    }
    
    public Error(int errorCode, String errorMessage, Object errorDetails) {
      this(errorCode, errorMessage);
      this.errorDetails = errorDetails;
    }
  }
  
  public QErrorResponse(Error error) {
    super(false);
    this.error = error;
  }
  
  public Error getResponse() {
    return error;
  }
  
  public com.google.gson.JsonElement getAsJsonObject() {
    return gson.toJsonTree(error);
  }
}
