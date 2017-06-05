package com.xartifex.moneytransfers.exceptions;

/**
 * author: xartifex
 * since: 01.06.2017
 */
public class MoneyTransfersException extends Exception {
    public MoneyTransfersException(String message, Throwable cause) {
        super(message, cause);
    }
    public MoneyTransfersException(String message) {
        super(message);
    }
}
