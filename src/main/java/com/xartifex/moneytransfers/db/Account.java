package com.xartifex.moneytransfers.db;

import org.hibernate.annotations.GenericGenerator;
import java.math.BigDecimal;
import javax.persistence.*;

/**
 * author: xartifex
 * since: 01.06.2017
 */
@Entity
@Table( name = "ACCOUNTS" )
public class Account {
    private Long id;
    private String name;
    private BigDecimal balance;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name="accounts_generator", sequenceName = "accounts_seq", allocationSize=50)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(nullable = false)
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        return id.equals(account.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
