package com.cheatCode.cashier.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cheatCode.cashier.model.db_model.saleModel;

public interface SaleRepository extends JpaRepository<saleModel, Integer> {
    
}
