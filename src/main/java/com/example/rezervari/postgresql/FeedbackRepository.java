package com.example.rezervari.postgresql;

import com.example.rezervari.entities.postgres.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
