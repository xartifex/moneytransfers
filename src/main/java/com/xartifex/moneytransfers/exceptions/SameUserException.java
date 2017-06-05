package com.xartifex.moneytransfers.exceptions;

/**
 * author: xartifex
 * since: 02.06.2017
 */
public class SameUserException extends MoneyTransfersException {
    public SameUserException(String message) {
        super(message);
    }
}
