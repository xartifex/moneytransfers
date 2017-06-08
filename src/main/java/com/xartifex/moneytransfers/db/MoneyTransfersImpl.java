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

    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(MAIN_PERSISTENCE_UNIT_NAME);
    private ThreadLocal<EntityManager> entityManagerTL = ThreadLocal.withInitial(()
            -> entityManagerFactory.createEntityManager());
    
    private EntityManager getEntityManager()
    {
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

    public Transaction send(long senderId, long receiverId, BigDecimal amount) throws MoneyTransfersException {
        try {
            if (amount.compareTo(BigDecimal.ZERO) != 1) {
                throw new IllegalAmountException("Amount must be positive!");
            }
            if (senderId == receiverId) {
                throw new SameUserException("Cannot send money to the same account: " + senderId);
            }
            getEntityManager().getTransaction().begin();
            Account sender = getEntityManager().find(Account.class, senderId);
            if (sender == null) {
                throw new AccountNotFoundException("The following sender is not found: " + senderId);
            }
            Account receiver = getEntityManager().find(Account.class, receiverId);
            if (receiver == null) {
                throw new AccountNotFoundException("The following receiver is not found: " + receiverId);
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

    @Override
    public Account get(long accountId) throws AccountNotFoundException {
        Account account = getEntityManager().find(Account.class, accountId);
        if (account == null) {
            throw new AccountNotFoundException("Account with the following id doesn't exist: " + accountId);
        }
        return account;
    }

    public void close() throws Exception {
        getEntityManager().close();
        entityManagerFactory.close();
        entityManagerTL.remove();
    }
}

