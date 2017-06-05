package com.xartifex.moneytransfers.server.model;

import java.util.Date;
import java.util.UUID;

/**
 * author: xartifex
 * since: 02.06.2017
 */
public class SendOrder extends BaseResponseEntity {
    private UUID id;
    private long senderId;
    private long receiverId;
    private String amount;
    private SendOrderStatus status;
    private Date date;

    public SendOrder(long senderId, long receiverId, String amount) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.date = new Date();
    }

    public SendOrder(long senderId, long receiverId, String amount, SendOrderStatus status) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.status = status;
        this.date = new Date();
    }

    public SendOrder() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public SendOrderStatus getStatus() {
        return status;
    }

    public void setStatus(SendOrderStatus status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "SendOrder{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", amount='" + amount + '\'' +
                ", status=" + status +
                ", date=" + date +
                ", statusCode=" + statusCode +
                '}';
    }
}
