package com.xartifex.moneytransfers.server.model;

import java.util.Date;
import java.util.UUID;

/**
 * author: xartifex
 * since: 03.06.2017
 */
public class InvalidSendOrder extends BaseResponseEntity {
    private long senderId;
    private long receiverId;
    private String amount;
    private String error;

    public InvalidSendOrder(SendOrder sendOrder) {
        this.senderId = sendOrder.getSenderId();
        this.receiverId = sendOrder.getReceiverId();
        this.amount = sendOrder.getAmount();
    }

    public InvalidSendOrder() {
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "InvalidSendOrder{" +
                "senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", amount='" + amount + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
