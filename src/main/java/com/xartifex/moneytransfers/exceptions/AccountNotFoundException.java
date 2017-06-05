package com.xartifex.moneytransfers.exceptions;

/**
 * author: xartifex
 * since: 01.06.2017
 */
public class AccountNotFoundException extends MoneyTransfersException{
    public AccountNotFoundException(String message) {
        super(message);
    }
}
