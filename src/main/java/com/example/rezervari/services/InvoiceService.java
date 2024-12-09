package com.example.rezervari.services;

import com.example.rezervari.entities.mysql.Invoice;
import com.example.rezervari.entities.mysql.Reservation;
import com.example.rezervari.mysql.InvoiceRepository;
import com.example.rezervari.mysql.ReservationRepository;
import com.example.rezervari.transaction.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    @Qualifier("mysqlDataSource")
    private DataSource mysqlDataSource;

    @Qualifier("postgresqlDataSource")
    @Autowired
    private DataSource postgresDataSource;

    @Autowired
    private TransactionManager transactionManager;

    public List<Invoice> getInvoicesByReservationId(Long reservationId) {
        return invoiceRepository.findByReservationId(reservationId);
    }

    /**
     * Fetches all invoices associated with reservations for a given user ID.
     * This operation is performed within a transaction.
     *
     * @param userId the ID of the user
     * @return a list of invoices for the user's reservations
     */
    public List<Invoice> getInvoicesByUserId(Long userId) {
        List<Invoice> invoices = new ArrayList<>();

        try (Connection mysqlConnection = mysqlDataSource.getConnection();
             Connection postgresConnection = postgresDataSource.getConnection()) {

            long transactionId = transactionManager.beginTransaction(mysqlConnection, postgresConnection);
            try {
                // Step 1: Fetch all reservations for the user
                List<Reservation> reservations = reservationRepository.findByUserId(userId);

                // Step 2: Fetch invoices for each reservation
                for (Reservation reservation : reservations) {
                    List<Invoice> reservationInvoices = invoiceRepository.findByReservationId(reservation.getId());
                    invoices.addAll(reservationInvoices);
                }

                // Commit transaction
                transactionManager.commitTransaction(transactionId);
            } catch (Exception e) {
                // Rollback transaction on failure
                transactionManager.rollbackTransaction(transactionId);
                throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error managing distributed transaction: " + e.getMessage(), e);
        }

        return invoices;
    }

    public void createInvoiceWithTransaction(Long reservationId, String details) {
        try (Connection mysqlConnection = mysqlDataSource.getConnection();
             Connection postgresConnection = postgresDataSource.getConnection()) {
            long transactionId = transactionManager.beginTransaction(mysqlConnection, postgresConnection);
            try {
                // Fetch reservation details
                Reservation reservation = reservationRepository.findById(reservationId)
                        .orElseThrow(() -> new RuntimeException("Reservation not found"));

                // Create and save invoice
                Invoice invoice = new Invoice();
                invoice.setReservationId(reservation.getId());
                invoice.setDetails(details);
                invoice.setIssueDate(java.time.LocalDate.now());
                invoiceRepository.save(invoice);

                // Commit transaction
                transactionManager.commitTransaction(transactionId);
            } catch (Exception e) {
                // Rollback transaction on failure
                transactionManager.rollbackTransaction(transactionId);
                throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error managing distributed transaction: " + e.getMessage(), e);
        }
    }
}