package com.xartifex.moneytransfers.db;

import com.xartifex.moneytransfers.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
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

    private final EntityManagerFactory entityManagerFactory = Persistence.
            createEntityManagerFactory(MAIN_PERSISTENCE_UNIT_NAME);
    private final ThreadLocal<EntityManager> entityManagerTL = ThreadLocal.withInitial(() -> {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        logger.debug("new EntityManager created: " + entityManager);
        return entityManager;
    });

    private EntityManager getEntityManager() {
        return entityManagerTL.get();
    }

    public MoneyTransfersImpl(Collection<Account> accounts) {
        getEntityManager().getTransaction().begin();
        int i = 0;
        for (Account account : accounts) {
            getEntityManager().persist(account);
            if ((i++ % 10000) == 0) {
                getEntityManager().flush();
                getEntityManager().clear();
            }
        }
        getEntityManager().getTransaction().commit();
    }

    public Transaction send(final Long senderId, final Long receiverId, BigDecimal amount) throws MoneyTransfersException {
        Long firstLock = senderId;
        Long secondLock = receiverId;
        if (senderId > receiverId) {
            firstLock = receiverId;
            secondLock = senderId;
        }
        synchronized (firstLock) {
            synchronized (secondLock) {
                try {
                    amount = amount.setScale(Account.SCALE, BigDecimal.ROUND_HALF_EVEN);
                    if (amount.compareTo(BigDecimal.ZERO) != 1) {
                        throw new IllegalAmountException("Amount must be positive!");
                    }
                    if (senderId == receiverId) {
                        throw new SameUserException("Cannot send money to the same account: " + senderId);
                    }
                    getEntityManager().getTransaction().begin();
                    getEntityManager().clear();
                    Account sender = getEntityManager().find(Account.class, senderId);
                    if (sender == null) {
                        throw new AccountNotFoundException("The following sender is not found: " + senderId);
                    } else {
                        getEntityManager().lock(sender, LockModeType.PESSIMISTIC_WRITE);
                    }
                    Account receiver = getEntityManager().find(Account.class, receiverId);
                    if (receiver == null) {
                        throw new AccountNotFoundException("The following receiver is not found: " + receiverId);
                    } else {
                        getEntityManager().lock(receiver, LockModeType.PESSIMISTIC_WRITE);
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
                    getEntityManager().persist(transaction);
                    getEntityManager().getTransaction().commit();
                    logger.debug("Account {} sent amount of {} to Account {}. Transaction id {}. Date {}.",
                            senderId, amount, receiverId, transaction.getId(), transaction.getDate());
                    return transaction;
                } catch (MoneyTransfersException e) {
                    logger.error("Invalid sending conditions", e);
                    if (getEntityManager().getTransaction().isActive()) {
                        getEntityManager().getTransaction().rollback();
                    }
                    throw e;
                } catch (Throwable unexpected) {
                    logger.error("Unable to send money", unexpected);
                    getEntityManager().getTransaction().rollback();
                    throw new MoneyTransfersException("Unable to send money", unexpected);
                }
            }
        }
    }

    @Override
    public Account get(long accountId) throws AccountNotFoundException {
        getEntityManager().clear();
        Account account = getEntityManager().find(Account.class, accountId);
        if (account == null) {
            throw new AccountNotFoundException("Account with the following id doesn't exist: " + accountId);
        }
        return account;
    }

    public void close() throws Exception {
        getEntityManager().clear();
        getEntityManager().close();
        entityManagerFactory.close();
        entityManagerTL.remove();
    }
}

