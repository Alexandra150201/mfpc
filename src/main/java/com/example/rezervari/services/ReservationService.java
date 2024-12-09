package com.example.rezervari.services;

import com.example.rezervari.entities.mysql.Invoice;
import com.example.rezervari.entities.mysql.Reservation;
import com.example.rezervari.entities.postgres.Reward;
import com.example.rezervari.entities.postgres.User;
import com.example.rezervari.mysql.InvoiceRepository;
import com.example.rezervari.mysql.ReservationRepository;
import com.example.rezervari.postgresql.RewardRepository;
import com.example.rezervari.postgresql.UserRepository;
import com.example.rezervari.transaction.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private InvoiceRepository invoiceRepository; // Inject InvoiceRepository

    @Autowired
    @Qualifier("mysqlDataSource")
    private DataSource mysqlDataSource;

    @Qualifier("postgresqlDataSource")
    @Autowired
    private DataSource postgresDataSource;

    @Autowired
    private TransactionManager transactionManager;

    /**
     * Creates a user and reservation in MySQL and PostgreSQL, with a distributed transaction.
     * After reservation creation, generates an invoice with details as a random number
     * divided by the user's loyalty points.
     */
    public void createReservationAndUser(Long userId, String name, int loyaltyPoints, String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        try (Connection mysqlConnection = mysqlDataSource.getConnection();
             Connection postgresConnection = postgresDataSource.getConnection()) {

            // Start distributed transaction
            long transactionId = transactionManager.beginTransaction(mysqlConnection, postgresConnection);

            try {
                // Check if the user already exists by name
                User user = userRepository.findByName(name);
                if (user != null) {
                    // Increase loyalty points for the existing user
                    user.setLoyaltyPoints(user.getLoyaltyPoints() + loyaltyPoints);
                } else {
                    // Create a new user if it doesn't exist
                    loyaltyPoints = 0;
                    user = new User();
                    user.setName(name);
                    user.setLoyaltyPoints(loyaltyPoints);
                    userRepository.save(user);
                }

                // Create the reservation in MySQL
                Reservation reservation = new Reservation();
                reservation.setUserId(user.getId());
                reservation.setRoomNumber(roomNumber);
                reservation.setCheckInDate(checkIn);
                reservation.setCheckOutDate(checkOut);
                reservationRepository.save(reservation);

                // Update the user's loyalty points in PostgreSQL after reservation creation
                user.setLoyaltyPoints(user.getLoyaltyPoints() + 10);  // Add 10 additional loyalty points
                userRepository.save(user);

                // Check if the user's loyalty points are a multiple of 50 and create a reward
                if (user.getLoyaltyPoints() % 50 == 0) {
                    Reward reward = new Reward();
                    reward.setUserId(user.getId());
                    reward.setRewardType("Loyalty Bonus");
                    reward.setIssueDate(LocalDate.now());
                    rewardRepository.save(reward);
                }
                else {

                    // Generate an invoice for the reservation
                    Invoice invoice = new Invoice();
                    invoice.setReservationId(reservation.getId());

                    // Calculate invoice details
                    Random random = new Random();
                    int randomAmount = random.nextInt(701) + 100; // Random number between 100 and 800

                    if (user.getLoyaltyPoints() > 0) {
                        int calculatedAmount = randomAmount / user.getLoyaltyPoints();
                        invoice.setDetails(String.valueOf(calculatedAmount));
                    } else {
                        invoice.setDetails(String.valueOf(randomAmount)); // Default to random amount if no loyalty points
                    }
                    invoice.setIssueDate(LocalDate.now());
                    invoiceRepository.save(invoice); // Save the invoice to MySQL
                }

                // Commit the distributed transaction
                transactionManager.commitTransaction(transactionId);
            } catch (Exception e) {
                // Rollback the transaction in case of failure
                transactionManager.rollbackTransaction(transactionId);
                throw new RuntimeException("Distributed transaction failed: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error managing distributed transaction: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves reservations for a specific user ID where the reservation ID
     * does not appear in the invoices list, using TransactionManager.
     *
     * @param userId The user ID for whom to retrieve reservations.
     * @return A list of reservations with corresponding invoices.
     */
    public List<Reservation> getReservationsWithInvoicesTransactional(Long userId) {
        try (Connection mysqlConnection = mysqlDataSource.getConnection();
             Connection postgresConnection = postgresDataSource.getConnection()) {

            // Start distributed transaction
            long transactionId = transactionManager.beginTransaction(mysqlConnection, postgresConnection);

            try {
                // Fetch all reservations for the user
                List<Reservation> userReservations = reservationRepository.findByUserId(userId);

                // Fetch all reservation IDs in the invoices
                List<Long> invoiceReservationIds = invoiceRepository.findAll()
                        .stream()
                        .map(Invoice::getReservationId)
                        .collect(Collectors.toList());

                // Filter reservations that do not have invoices
                List<Reservation> reservationsWithoutInvoices = userReservations.stream()
                        .filter(reservation -> invoiceReservationIds.contains(reservation.getId()))
                        .collect(Collectors.toList());

                // Commit the transaction
                transactionManager.commitTransaction(transactionId);

                return reservationsWithoutInvoices;
            } catch (Exception e) {
                // Rollback the transaction in case of failure
                transactionManager.rollbackTransaction(transactionId);
                throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error managing transaction: " + e.getMessage(), e);
        }
    }
}
