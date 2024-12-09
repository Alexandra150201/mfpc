package com.example.rezervari.services;

import com.example.rezervari.entities.postgres.User;
import com.example.rezervari.postgresql.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    // Method to create a new user
    public void createUser(String name, int loyaltyPoints) {
        User user = new User();
        user.setName(name);
        user.setLoyaltyPoints(loyaltyPoints);
        userRepository.save(user);
    }

    // Method to get a user by ID
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Method to update a user's loyalty points
    public void updateUserLoyaltyPoints(Long userId, int loyaltyPoints) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setLoyaltyPoints(loyaltyPoints);
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // New method to find a user by name or create a new one if not found
    public User createOrFindUser(String name) {
        // Try to find the user by name
        User existingUser = userRepository.findByName(name);
        if (existingUser != null) {
            // User found, return the existing user
            return existingUser;
        }

        // User not found, create a new user
        User newUser = new User();
        newUser.setName(name);
        newUser.setLoyaltyPoints(0); // Default loyalty points
        return userRepository.save(newUser);
    }
}
