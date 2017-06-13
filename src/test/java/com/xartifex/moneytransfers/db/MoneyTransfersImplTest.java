package com.xartifex.moneytransfers.db;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * author: xartifex
 * since: 01.06.2017
 */

public class MoneyTransfersImplTest {
    private static final BigDecimal TWENTY = BigDecimal.valueOf(20);
    private static final BigDecimal NINETY = BigDecimal.valueOf(90);
    private static final long ACCOUNT1 = 1L;
    private static final long ACCOUNT2 = 2L;
    private static MoneyTransfers moneyTransfers;

    @BeforeClass
    public static void setUp() {
        Account account1 = new Account();
        account1.setBalance(BigDecimal.valueOf(100));
        account1.setName("Account1");

        Account account2 = new Account();
        account2.setBalance(BigDecimal.valueOf(10));
        account2.setName("Account2");

        List<Account> accountList = new ArrayList<>();
        accountList.add(account1);
        accountList.add(account2);
        moneyTransfers = new MoneyTransfersImpl(accountList);
    }

    @Test
    public void testSendAndGetCorrectResults() throws Exception {
        moneyTransfers.send(ACCOUNT1, ACCOUNT2, BigDecimal.TEN);
        Assert.assertEquals(NINETY, moneyTransfers.get(ACCOUNT1).getBalance().setScale(0));
        Assert.assertEquals(TWENTY, moneyTransfers.get(ACCOUNT2).getBalance().setScale(0));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (moneyTransfers != null) {
            moneyTransfers.close();
        }
    }
}