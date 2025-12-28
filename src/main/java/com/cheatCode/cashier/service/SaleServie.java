package com.cheatCode.cashier.service;

    
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cheatCode.cashier.Repository.SaleRepository;
import com.cheatCode.cashier.model.api_request.JSaleCreateRequestModel;
import com.cheatCode.cashier.model.db_model.saleModel;

@Service
public class SaleServie {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    EmailService emailService;

    public saleModel createSale(JSaleCreateRequestModel request) {
        saleModel sale = new saleModel();
        sale.setItemName(request.getItemName());
        sale.setQuantity(request.getQuantity());
        sale.setPrice(request.getPrice());
        sale.setTotal(request.getQuantity() * request.getPrice());
        saleModel savedSale = saleRepository.save(sale);
        emailService.sendEmail(request.getEmail(), "Thank you for purchasing", savedSale);

        return savedSale;
    }
}


