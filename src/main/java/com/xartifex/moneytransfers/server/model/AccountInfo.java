package com.xartifex.moneytransfers.server.model;

import java.util.Date;

/**
 * author: xartifex
 * since: 02.06.2017
 */
public class AccountInfo extends BaseResponseEntity {
    private long id;
    private String name;
    private String balance;
    private Date time;

    public AccountInfo() {}

    public AccountInfo(long id, String name, String balance, Date time) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "AccountInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balance='" + balance + '\'' +
                ", time=" + time +
                '}';
    }
}
