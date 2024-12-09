package com.example.rezervari.controllers;

import com.example.rezervari.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*") // Allow CORS for all origins (customize as needed)
public class PaymentController {

    @Autowired
    private PaymentService paymentService; // Business logic layer


    @PostMapping("/{reservationId}/{amount}")
    public ResponseEntity<String> createPayment(@PathVariable Long reservationId, @PathVariable Double amount) {
        try {
            paymentService.createPaymentForReservation(reservationId, amount);
            return ResponseEntity.ok("Payment created successfully!");
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating payment: " + e.getMessage());
        }
    }

    // DTO for Payment Request
    public static class PaymentRequest {
        private Long reservationIdP; // Reservation ID
        private Double amountP;      // Payment amount

        // Getters and setters
        public Long getReservationIdP() {
            return reservationIdP;
        }

        public void setReservationIdP(Long reservationIdP) {
            this.reservationIdP = reservationIdP;
        }

        public Double getAmountP() {
            return amountP;
        }

        public void setAmountP(Double amountP) {
            this.amountP = amountP;
        }

        @Override
        public String toString() {
            return "PaymentRequest{reservationIdP=" + reservationIdP + ", amountP=" + amountP + "}";
        }
    }
}
