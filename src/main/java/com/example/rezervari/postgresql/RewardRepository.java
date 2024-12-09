package com.example.rezervari.postgresql;

import com.example.rezervari.entities.postgres.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    // Custom query to find rewards by userId
    List<Reward> findByUserId(Long userId);
}
