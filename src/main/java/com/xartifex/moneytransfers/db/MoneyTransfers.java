package com.xartifex.moneytransfers.db;

import com.xartifex.moneytransfers.exceptions.AccountNotFoundException;
import com.xartifex.moneytransfers.exceptions.MoneyTransfersException;

import java.math.BigDecimal;

/**
 * author: xartifex
 * since: 01.06.2017
 */
public interface MoneyTransfers extends AutoCloseable{
    Transaction send(long senderId, long receiverId, BigDecimal amount) throws MoneyTransfersException;
    Account get(long accountId) throws AccountNotFoundException;
}
