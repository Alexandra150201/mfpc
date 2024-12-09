package com.example.rezervari.services;

import com.example.rezervari.entities.mysql.Payment;
import com.example.rezervari.entities.mysql.Reservation;
import com.example.rezervari.entities.mysql.Invoice;
import com.example.rezervari.mysql.InvoiceRepository;
import com.example.rezervari.mysql.PaymentRepository;
import com.example.rezervari.mysql.ReservationRepository;
import com.example.rezervari.transaction.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;



@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    @Qualifier("mysqlDataSource")
    private DataSource mysqlDataSource;

    @Qualifier("postgresqlDataSource")
    @Autowired
    private DataSource postgresDataSource;

    @Autowired
    private TransactionManager transactionManager;

    public void createPaymentForReservation(Long reservationId,double amount) {
        try (Connection mysqlConnection = mysqlDataSource.getConnection();
             Connection postgresConnection = postgresDataSource.getConnection()) {

            // Start distributed transaction
            long transactionId = transactionManager.beginTransaction(mysqlConnection, postgresConnection);

            try {
                // Find the reservation by ID
                Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);
                if (reservationOptional.isEmpty()) {
                    throw new RuntimeException("No reservation found with ID: " + reservationId);
                }

                // Create a new payment linked to the reservation
                Payment payment = new Payment();
                payment.setReservationId(reservationId);

                payment.setAmount(BigDecimal.valueOf(amount));

                // Handle all invoices associated with the reservation
                List<Invoice> invoices = invoiceRepository.findByReservationId(reservationId);
                double remainingAmount = amount;

                for (Invoice invoice : invoices) {
                    int invoiceAmount = Integer.parseInt(invoice.getDetails());
                    if (remainingAmount > invoiceAmount)
                    {
                        throw new RuntimeException("Payment amount exceeds total invoice amount for the reservation.");

                    }
                    if (remainingAmount == invoiceAmount) {
                        // If payment covers the full invoice, delete the invoice
                        payment.setStatus("full paid");
                        invoiceRepository.delete(invoice);
                        remainingAmount -= invoiceAmount;
                    } else {
                        // If payment partially covers the invoice, update the invoice
                        int updatedInvoiceAmount = invoiceAmount - (int) remainingAmount;
                        invoice.setDetails(String.valueOf(updatedInvoiceAmount));
                        payment.setStatus("partially paid");
                        invoiceRepository.save(invoice);
                        remainingAmount = 0;
                        break; // Exit the loop as the payment has been fully utilized
                    }
                }
                paymentRepository.save(payment);


                // Commit the transaction
                transactionManager.commitTransaction(transactionId);
            } catch (Exception e) {
                // Rollback the transaction in case of failure
                transactionManager.rollbackTransaction(transactionId);
                throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error managing distributed transaction: " + e.getMessage(), e);
        }
    }

}
