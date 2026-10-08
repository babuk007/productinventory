package com.example.productinventory.repository;

import com.example.productinventory.model.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    List<InventoryTransaction> findByProductIdOrderByTimestampDesc(Long productId);
    List<InventoryTransaction> findTop20ByOrderByTimestampDesc();
}
