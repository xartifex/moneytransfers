package com.xartifex.moneytransfers.server;

/**
 * author: xartifex
 * since: 02.06.2017
 */


import com.xartifex.moneytransfers.db.Account;
import com.xartifex.moneytransfers.db.MoneyTransfersImpl;
import com.xartifex.moneytransfers.db.Transaction;
import com.xartifex.moneytransfers.exceptions.AccountNotFoundException;
import com.xartifex.moneytransfers.exceptions.MoneyTransfersException;
import com.xartifex.moneytransfers.server.model.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBLayerProcessing extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(DBLayerProcessing.class);

    private MoneyTransfersImpl moneyTransfers;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.executeBlocking(future -> {
            //todo: make initialization configurable
            List<Account> accountList = new ArrayList<>();
            for (int i = 1; i < 101; i++) {
                Account account = new Account();
                account.setName(String.valueOf(i));
                account.setBalance(BigDecimal.valueOf(10 * i));
                accountList.add(account);
            }
            moneyTransfers = new MoneyTransfersImpl(accountList);

            vertx.eventBus().<JsonObject>consumer("db", msg -> {
                JsonObject jsonObject = msg.body();
                switch (RequestType.valueOf(jsonObject.getString(Const.REQUEST_TYPE_KEY))) {
                    case SEND_ORDER: {
                        jsonObject.remove(Const.REQUEST_TYPE_KEY);
                        SendOrder sendOrder = jsonObject.mapTo(SendOrder.class);
                        //todo: implement logic for IN_PROGRESS order
                        try {
                            Transaction transaction = moneyTransfers.send(sendOrder.getSenderId(),
                                    sendOrder.getReceiverId(), new BigDecimal(sendOrder.getAmount()));
                            sendOrder.setId(transaction.getId());
                            sendOrder.setDate(transaction.getDate());
                            sendOrder.setStatus(SendOrderStatus.COMPLETED);
                            sendOrder.setStatusCode(Response.Status.OK.getStatusCode());
                            msg.reply(JsonObject.mapFrom(sendOrder));
                        } catch (NumberFormatException | MoneyTransfersException e) {
                            InvalidSendOrder invalidSendOrder = new InvalidSendOrder(sendOrder);
                            invalidSendOrder.setError(e.getMessage());
                            invalidSendOrder.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
                            msg.reply(JsonObject.mapFrom(invalidSendOrder).put(Const.INVALID_SEND_ORDER_KEY, "true"));
                        }
                        break;
                    }
                    case ACCOUNT_INFO: {
                        try {
                            long id = Long.valueOf(jsonObject.getString(Const.ACCOUNT_ID_KEY));
                            Account account = moneyTransfers.get(id);
                            AccountInfo accountInfo = new AccountInfo(account.getId(),
                                    account.getName(),
                                    account.getBalance().stripTrailingZeros().toPlainString(),
                                    new Date());
                            accountInfo.setStatusCode(Response.Status.OK.getStatusCode());
                            msg.reply(JsonObject.mapFrom(accountInfo));
                        } catch (AccountNotFoundException | NumberFormatException e2) {
                            msg.reply(null);
                        }
                        break;
                    }
                    default:
                        throw new IllegalStateException();
                }
            });
            future.complete();
        }, res -> {
            if (res.succeeded()) {
                logger.info("Backend initialized!");
                startFuture.complete();
            } else {
                logger.error("Failed to initialize backend: ", res.cause());
                startFuture.failed();
            }
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        moneyTransfers.close();
        stopFuture.complete();
    }
}