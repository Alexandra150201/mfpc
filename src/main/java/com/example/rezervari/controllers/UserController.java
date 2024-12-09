package com.example.rezervari.controllers;

import com.example.rezervari.entities.postgres.User;
import com.example.rezervari.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/api/user")
    public ResponseEntity<User> createUser(@RequestBody UserRequest request) {
        try {
            User u =userService.createOrFindUser(request.getName());
            if (u==null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(u);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

//    @GetMapping("/api/users")
//    public ResponseEntity<List<User>> getAllUsers() {
//        try {
//            List<User> users = userService.getAllUsers();
//            return ResponseEntity.ok(users);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
//
//    @GetMapping("/{userId}")
//    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
//        try {
//            User user = userService.getUserById(userId);
//            return ResponseEntity.ok(user);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
//        }
//    }
//
//    @PutMapping("/{userId}")
//    public ResponseEntity<String> updateUserLoyaltyPoints(@PathVariable Long userId, @RequestBody UpdateLoyaltyPointsRequest request) {
//        try {
//            userService.updateUserLoyaltyPoints(userId, request.getLoyaltyPoints());
//            return ResponseEntity.ok("Loyalty points updated successfully!");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
//        }
//    }

    // DTO for creating a user
    public static class UserRequest {
        private String name;
        private int loyaltyPoints;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getLoyaltyPoints() {
            return loyaltyPoints;
        }

        public void setLoyaltyPoints(int loyaltyPoints) {
            this.loyaltyPoints = loyaltyPoints;
        }
    }

//    // DTO for updating loyalty points
//    public static class UpdateLoyaltyPointsRequest {
//        private int loyaltyPoints;
//
//        public int getLoyaltyPoints() {
//            return loyaltyPoints;
//        }
//
//        public void setLoyaltyPoints(int loyaltyPoints) {
//            this.loyaltyPoints = loyaltyPoints;
//        }
//    }
}
