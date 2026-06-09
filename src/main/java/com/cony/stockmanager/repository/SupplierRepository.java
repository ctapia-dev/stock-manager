package com.cony.stockmanager.repository;

import com.cony.stockmanager.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByName(String name);
    List<Supplier> findAllByActiveTrue();
}