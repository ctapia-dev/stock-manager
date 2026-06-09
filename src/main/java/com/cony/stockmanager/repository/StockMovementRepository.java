package com.cony.stockmanager.repository;

import com.cony.stockmanager.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    Page<StockMovement> findAllByProductId(Long productId, Pageable pageable);

    List<StockMovement> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}