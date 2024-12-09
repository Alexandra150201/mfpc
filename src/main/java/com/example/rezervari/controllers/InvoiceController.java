package com.example.rezervari.controllers;

import com.example.rezervari.entities.mysql.Invoice;
import com.example.rezervari.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

//    // Endpoint to create a new invoice
//    @PostMapping
//    public ResponseEntity<String> createInvoice(@RequestBody InvoiceRequest request) {
//        try {
//            // Call the updated service method to create an invoice
//            invoiceService.createInvoiceWithTransaction(request.getReservationId(), request.getDetails());
//            return ResponseEntity.ok("Invoice created successfully!");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
//        }
//    }

    // Endpoint to get invoices by reservation ID (use path variable)
    @GetMapping("/{reservationId}")
    public ResponseEntity<List<Invoice>> getInvoicesByReservationId(@PathVariable Long reservationId) {
        try {
            List<Invoice> invoices = invoiceService.getInvoicesByReservationId(reservationId);
            if (invoices.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // New endpoint to get invoices by user ID (use path variable)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Invoice>> getInvoicesByUserId(@PathVariable Long userId) {
        try {
            List<Invoice> invoices = invoiceService.getInvoicesByUserId(userId);
            if (invoices.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

//    // DTO for request to create an invoice
//    public static class InvoiceRequest {
//        private Long reservationId;
//        private String details;
//
//        public Long getReservationId() {
//            return reservationId;
//        }
//
//        public void setReservationId(Long reservationId) {
//            this.reservationId = reservationId;
//        }
//
//        public String getDetails() {
//            return details;
//        }
//
//        public void setDetails(String details) {
//            this.details = details;
//        }
//    }
}
