package com.xartifex.moneytransfers.server.model;

/**
 * author: xartifex
 * since: 03.06.2017
 */
public class ServerError extends BaseResponseEntity{
    private String error;

    public ServerError() {
    }

    public ServerError(String error, int statusCode) {
        this.error = error;
        this.statusCode = statusCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
