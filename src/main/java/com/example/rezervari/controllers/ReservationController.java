package com.example.rezervari.controllers;

import com.example.rezervari.entities.mysql.Reservation;
import com.example.rezervari.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    /**
     * Endpoint for creating a reservation and user.
     * Accepts user details and reservation details in the request body.
     */
    @PostMapping
    public ResponseEntity<String> createReservation(@RequestBody ReservationRequest request) {
        try {
            // Call the service method to handle both user creation and reservation
            reservationService.createReservationAndUser(
                    request.getUserId(),
                    request.getName(), // User's name
                    request.getLoyaltyPoints(), // User's initial loyalty points
                    request.getRoomNumber(),
                    request.getCheckInDate(),
                    request.getCheckOutDate()
            );
            return ResponseEntity.ok("Reservation created successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    /**
     * Endpoint to get reservations for a user where the reservation ID does not appear in any invoice.
     * @param userId The ID of the user for whom to fetch reservations.
     * @return A list of reservations that do not have invoices.
     */
    @GetMapping("/with-invoices/{userId}")
    public ResponseEntity<List<Reservation>> getReservationsWithoutInvoices(@PathVariable Long userId) {
        try {
            List<Reservation> reservations = reservationService.getReservationsWithInvoicesTransactional(userId);
            System.out.println(reservations);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // DTO for request
    public static class ReservationRequest {
        private Long userId;  // User ID (might be used for reference or can be generated for a new user)
        private String name;  // Name of the user
        private int loyaltyPoints;  // Initial loyalty points for the user
        private String roomNumber;  // Room number for the reservation
        private LocalDate checkInDate;  // Check-in date for the reservation
        private LocalDate checkOutDate;  // Check-out date for the reservation

        // Getters and Setters
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

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

        public String getRoomNumber() {
            return roomNumber;
        }

        public void setRoomNumber(String roomNumber) {
            this.roomNumber = roomNumber;
        }

        public LocalDate getCheckInDate() {
            return checkInDate;
        }

        public void setCheckInDate(LocalDate checkInDate) {
            this.checkInDate = checkInDate;
        }

        public LocalDate getCheckOutDate() {
            return checkOutDate;
        }

        public void setCheckOutDate(LocalDate checkOutDate) {
            this.checkOutDate = checkOutDate;
        }
    }
}
