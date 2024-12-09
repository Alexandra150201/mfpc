package com.example.rezervari.services;

import com.example.rezervari.entities.postgres.Reward;
import com.example.rezervari.postgresql.RewardRepository;
import com.example.rezervari.postgresql.UserRepository;
import com.example.rezervari.entities.postgres.User;
import com.example.rezervari.transaction.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

@Service
public class RewardService {

    @Autowired
    private RewardRepository rewardRepository;

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

    // Method to create a reward
    public void createReward(Long userId, String rewardType) {
        Reward reward = new Reward();
        reward.setUserId(userId);
        reward.setRewardType(rewardType);
        rewardRepository.save(reward);
    }

    /**
     * Retrieves all rewards for a user by their user ID, within a distributed transaction.
     * The method ensures consistency across both MySQL and PostgreSQL databases.
     */
    public List<Reward> getAllRewardsForUser(Long userId) {
        try (Connection mysqlConnection = mysqlDataSource.getConnection();
             Connection postgresConnection = postgresDataSource.getConnection()) {

            // Start distributed transaction
            long transactionId = transactionManager.beginTransaction(mysqlConnection, postgresConnection);

            try {
                // Find the user by userId
                Optional<User> userOptional = userRepository.findById(userId);
                if (userOptional.isEmpty()) {
                    throw new RuntimeException("User not found with ID: " + userId);
                }

                // Retrieve all rewards associated with the user
                List<Reward> rewards = rewardRepository.findByUserId(userId);

                // Commit the transaction (even though we are only reading here)
                transactionManager.commitTransaction(transactionId);

                // Return the list of rewards
                return rewards;
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
