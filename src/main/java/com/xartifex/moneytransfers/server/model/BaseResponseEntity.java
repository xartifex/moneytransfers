package com.xartifex.moneytransfers.server.model;

/**
 * author: xartifex
 * since: 03.06.2017
 */
public class BaseResponseEntity {
    protected int statusCode;

    public BaseResponseEntity() {
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
