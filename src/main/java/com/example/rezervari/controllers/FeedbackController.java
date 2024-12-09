package com.example.rezervari.controllers;

import com.example.rezervari.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<String> createFeedback(@RequestBody FeedbackRequest request) {
        try {
            // Call the updated method in FeedbackService
            feedbackService.createFeedbackAndUpdateUser(request.getUserId(), request.getComments(), request.getRating());
            return ResponseEntity.ok("Feedback created and user loyalty points updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // DTO for request
    public static class FeedbackRequest {
        private Long userId;
        private String comments;
        private int rating;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }
    }
}
