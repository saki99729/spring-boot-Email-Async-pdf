package com.cheatCode.cashier.model.db_model;

import org.antlr.v4.runtime.misc.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;



@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sales")
public class saleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @NotNull
    @Column(name = "item_name", nullable = false)
    private String itemName;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "price", nullable = false)
    private Double price;

    @NotNull
    @Column(name = "total", nullable = false)
    private Double total;
    
    // Explicit getters to satisfy tools/environments that don't process Lombok
    public Integer getId() { return this.id; }
    public String getItemName() { return this.itemName; }
    public Integer getQuantity() { return this.quantity; }
    public Double getPrice() { return this.price; }
    public Double getTotal() { return this.total; }

    // Explicit setters
    public void setId(Integer id) { this.id = id; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setPrice(Double price) { this.price = price; }
    public void setTotal(Double total) { this.total = total; }
}
