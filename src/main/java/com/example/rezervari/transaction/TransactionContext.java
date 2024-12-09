package com.example.rezervari.transaction;

import java.sql.Connection;

public class TransactionContext {
    private long transactionId;
    private Connection mysqlConnection;
    private Connection postgresConnection;

    public TransactionContext(long transactionId, Connection mysqlConnection, Connection postgresConnection) {
        this.transactionId = transactionId;
        this.mysqlConnection = mysqlConnection;
        this.postgresConnection = postgresConnection;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public Connection getMysqlConnection() {
        return mysqlConnection;
    }

    public Connection getPostgresConnection() {
        return postgresConnection;
    }
}
