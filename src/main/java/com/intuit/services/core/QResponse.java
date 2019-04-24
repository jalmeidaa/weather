package com.intuit.services.core;

public abstract class QResponse {
  Boolean status = Boolean.valueOf(true);
  
  public QResponse(boolean status) {
    this.status = Boolean.valueOf(status);
  }
}
