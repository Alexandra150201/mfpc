package com.example.rezervari.mysql;

import com.example.rezervari.entities.mysql.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByReservationId(Long reservationId);
}
