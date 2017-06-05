package com.xartifex.moneytransfers.exceptions;

/**
 * author: xartifex
 * since: 01.06.2017
 */
public class InsufficientFundsException extends MoneyTransfersException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
