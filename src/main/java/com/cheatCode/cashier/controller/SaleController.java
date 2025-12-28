package com.cheatCode.cashier.controller;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cheatCode.cashier.model.api_request.JSaleCreateRequestModel;
import com.cheatCode.cashier.model.db_model.saleModel;
import com.cheatCode.cashier.service.SaleServie;

@RestController
@RequestMapping("/api/v1/sales")
public class SaleController {

    @Autowired
    private SaleServie saleService;

    @PostMapping
    public ResponseEntity<saleModel> createSale(@RequestBody JSaleCreateRequestModel request) {
        saleModel newSale = saleService.createSale(request);
        return new ResponseEntity<>(newSale, HttpStatus.CREATED);
    }
}


