package com.example.rezervari.services;

import com.example.rezervari.entities.postgres.Feedback;
import com.example.rezervari.entities.postgres.User;
import com.example.rezervari.postgresql.FeedbackRepository;
import com.example.rezervari.postgresql.UserRepository;
import com.example.rezervari.transaction.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("mysqlDataSource")
    private DataSource mysqlDataSource;

    @Qualifier("postgresqlDataSource")
    @Autowired
    private DataSource postgresDataSource;

    @Autowired
    private TransactionManager transactionManager;

    public void createFeedbackAndUpdateUser(Long userId, String comments, int rating) {
        try (Connection mysqlConnection = mysqlDataSource.getConnection();
             Connection postgresConnection = postgresDataSource.getConnection()) {
            long transactionId = transactionManager.beginTransaction(mysqlConnection, postgresConnection);
            try {
                // Create feedback
                Feedback feedback = new Feedback();
                feedback.setUserId(userId);
                feedback.setComments(comments);
                feedback.setRating(rating);
                feedbackRepository.save(feedback);

                // Update user's loyalty points
                User user = userRepository.findById(userId).orElseThrow();
                user.setLoyaltyPoints(user.getLoyaltyPoints() + 5);
                userRepository.save(user);

                transactionManager.commitTransaction(transactionId);
            } catch (Exception e) {
                transactionManager.rollbackTransaction(transactionId);
                throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error managing distributed transaction: " + e.getMessage(), e);
        }
    }
}
