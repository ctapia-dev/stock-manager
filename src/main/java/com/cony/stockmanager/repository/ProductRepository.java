package com.cony.stockmanager.repository;

import com.cony.stockmanager.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySku(String sku);
    boolean existsByName(String name);
    Page<Product> findAllByActiveTrue(Pageable pageable);
    List<Product> findAllByActiveTrueAndStockLessThanEqual(Integer stock);
}