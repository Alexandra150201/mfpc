package com.example.rezervari.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TransactionManager {
    // Stochează conexiunile tranzacțiilor active
    private Map<Long, TransactionContext> activeTransactions = new HashMap<>();

    // ID unic pentru tranzacții
    private long transactionIdCounter = 0;

    /**
     * Începe o tranzacție nouă.
     *
     * @param mysqlConnection      Conexiunea la baza de date MySQL
     * @param postgresConnection   Conexiunea la baza de date PostgreSQL
     * @return ID-ul tranzacției
     */
    public synchronized long beginTransaction(Connection mysqlConnection, Connection postgresConnection) throws SQLException {
        // Configurează conexiunile în modul tranzacțional
        mysqlConnection.setAutoCommit(false);
        postgresConnection.setAutoCommit(false);

        // Creează un nou context pentru tranzacție
        long transactionId = ++transactionIdCounter;
        TransactionContext context = new TransactionContext(transactionId, mysqlConnection, postgresConnection);
        activeTransactions.put(transactionId, context);

        System.out.println("Transaction started: ID = " + transactionId);
        return transactionId;
    }

    /**
     * Commit pentru tranzacție.
     *
     * @param transactionId ID-ul tranzacției
     */
    public synchronized void commitTransaction(long transactionId) throws SQLException {
        TransactionContext context = activeTransactions.get(transactionId);

        if (context == null) {
            throw new IllegalStateException("Transaction not found: ID = " + transactionId);
        }

        try {
            // Commit pentru ambele baze de date
            context.getMysqlConnection().commit();
            context.getPostgresConnection().commit();
            System.out.println("Transaction committed: ID = " + transactionId);
        } catch (SQLException e) {
            System.err.println("Error during commit, rolling back: ID = " + transactionId);
            rollbackTransaction(transactionId);
            throw e;
        } finally {
            closeTransaction(transactionId);
        }
    }

    /**
     * Rollback pentru tranzacție.
     *
     * @param transactionId ID-ul tranzacției
     */
    public synchronized void rollbackTransaction(long transactionId) throws SQLException {
        TransactionContext context = activeTransactions.get(transactionId);

        if (context == null) {
            throw new IllegalStateException("Transaction not found: ID = " + transactionId);
        }

        try {
            // Rollback pentru ambele baze de date
            context.getMysqlConnection().rollback();
            context.getPostgresConnection().rollback();
            System.out.println("Transaction rolled back: ID = " + transactionId);
        } finally {
            closeTransaction(transactionId);
        }
    }

    /**
     * Închide conexiunile tranzacției și șterge din mapă.
     *
     * @param transactionId ID-ul tranzacției
     */
    private void closeTransaction(long transactionId) {
        TransactionContext context = activeTransactions.remove(transactionId);

        if (context != null) {
            try {
                context.getMysqlConnection().close();
                context.getPostgresConnection().close();
            } catch (SQLException e) {
                System.err.println("Error closing connections: " + e.getMessage());
            }
        }
    }
}
