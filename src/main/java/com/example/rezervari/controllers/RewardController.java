package com.example.rezervari.controllers;

import com.example.rezervari.entities.postgres.Reward;
import com.example.rezervari.services.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {
    @Autowired
    private RewardService rewardService;

    @PostMapping
    public ResponseEntity<String> createReward(@RequestBody RewardRequest request) {
        try {
            rewardService.createReward(request.getUserId(), request.getRewardType());
            return ResponseEntity.ok("Reward created successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    // Endpoint to get all rewards for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reward>> getAllRewardsForUser(@PathVariable Long userId) {
        try {
            List<Reward> rewards = rewardService.getAllRewardsForUser(userId);
            if (rewards.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(rewards);
            }
            return ResponseEntity.ok(rewards);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // DTO for request
    public static class RewardRequest {
        private Long userId;
        private String rewardType;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getRewardType() {
            return rewardType;
        }

        public void setRewardType(String rewardType) {
            this.rewardType = rewardType;
        }
    }
}
