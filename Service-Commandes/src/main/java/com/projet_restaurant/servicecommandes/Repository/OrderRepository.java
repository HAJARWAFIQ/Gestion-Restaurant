package com.projet_restaurant.servicecommandes.Repository;

import com.projet_restaurant.servicecommandes.Entity.Order;
import com.projet_restaurant.servicecommandes.Entity.OrderStatus;
import com.rabbitmq.client.impl.LongStringHelper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
    Order findByUserIdAndStatus(Long userId, OrderStatus status);

}
