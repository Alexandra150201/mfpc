package com.example.rezervari.mysql;

import com.example.rezervari.entities.mysql.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
