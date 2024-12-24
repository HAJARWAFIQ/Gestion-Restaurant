package com.projet_restaurant.servicecommandes.Dto;

import com.projet_restaurant.servicecommandes.Entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public class OrderRequest {
    private Long userId;
    private List<OrderItem> items;
    private BigDecimal totalPrice;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
