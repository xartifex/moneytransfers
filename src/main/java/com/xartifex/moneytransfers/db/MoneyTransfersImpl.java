package com.xartifex.moneytransfers.db;

import com.xartifex.moneytransfers.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.Collection;

/**
 * author: xartifex
 * since: 01.06.2017
 */
public class MoneyTransfersImpl implements MoneyTransfers, AutoCloseable {
    public static final String MAIN_PERSISTENCE_UNIT_NAME = "moneytransfers";

    private final Logger logger = LoggerFactory.getLogger(MoneyTransfersImpl.class);

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    public MoneyTransfersImpl() {
        entityManagerFactory = Persistence.createEntityManagerFactory(MAIN_PERSISTENCE_UNIT_NAME);
        entityManager = entityManagerFactory.createEntityManager();
    }

    public MoneyTransfersImpl(Collection<Account> accounts) {
        this();
        entityManager.getTransaction().begin();
        int i = 0;
        for (Account account : accounts) {
            entityManager.persist(account);
            if ((i++ % 10000) == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.getTransaction().commit();
    }

    public Transaction send(long senderId, long receiverId, BigDecimal amount) throws MoneyTransfersException {
        try {
            if (amount.compareTo(BigDecimal.ZERO) != 1) {
                throw new IllegalAmountException("Amount must be positive!");
            }
            if (senderId == receiverId) {
                throw new SameUserException("Cannot send money to the same account: " + senderId);
            }
            entityManager.getTransaction().begin();
            Account sender = entityManager.find(Account.class, senderId);
            if (sender == null) {
                throw new AccountNotFoundException("The following sender is not found: " + senderId);
            }
            Account receiver = entityManager.find(Account.class, receiverId);
            if (receiver == null) {
                throw new AccountNotFoundException("The following receiver is not found: " + receiver);
            }
            BigDecimal senderBalance = sender.getBalance();
            BigDecimal subtractedBalance = senderBalance.subtract(amount);
            if (subtractedBalance.compareTo(BigDecimal.ZERO) == -1) {
                throw new InsufficientFundsException("Sender has insufficient funds to send the following amount: " + amount);
            }
            sender.setBalance(subtractedBalance);
            receiver.setBalance(receiver.getBalance().add(amount));
            Transaction transaction = new Transaction();
            transaction.setSender(sender);
            transaction.setReceiver(receiver);
            transaction.setAmount(amount);
            entityManager.persist(transaction);
            entityManager.getTransaction().commit();
            logger.debug("Account {} sent amount of {} to Account {}. Transaction id {}. Date {}.",
                    senderId, amount, receiverId, transaction.getId(), transaction.getDate());
            return transaction;
        } catch (MoneyTransfersException e) {
            logger.error("Invalid sending conditions", e);
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        } catch (Throwable unexpected) {
            logger.error("Unable to send money", unexpected);
            entityManager.getTransaction().rollback();
            throw new MoneyTransfersException("Unable to send money", unexpected);
        }
    }

    @Override
    public Account get(long accountId) throws AccountNotFoundException {
        Account account = entityManager.find(Account.class, accountId);
        if (account == null) {
            throw new AccountNotFoundException("Account with the following id doesn't exist: " + accountId);
        }
        return account;
    }

    public void close() throws Exception {
        entityManager.close();
        entityManagerFactory.close();
    }
}

