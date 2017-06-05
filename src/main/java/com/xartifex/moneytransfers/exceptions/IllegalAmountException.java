package com.xartifex.moneytransfers.exceptions;

/**
 * author: xartifex
 * since: 02.06.2017
 */
public class IllegalAmountException extends MoneyTransfersException {
    public IllegalAmountException(String message) {
        super(message);
    }
}
