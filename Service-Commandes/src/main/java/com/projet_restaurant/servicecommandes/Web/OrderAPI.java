package com.projet_restaurant.servicecommandes.Web;

import com.projet_restaurant.servicecommandes.Dto.OrderRequest;
import com.projet_restaurant.servicecommandes.Entity.Order;
import com.projet_restaurant.servicecommandes.Entity.OrderItem;
import com.projet_restaurant.servicecommandes.Service.Implementation.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderAPI {
    @Autowired
    private OrderService orderService;

    // Créer une commande pour un utilisateur
    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestParam Long userId , @RequestBody OrderRequest orderRequest) {
        // Affichage dans la console des données reçues
        System.out.println("Requête reçue pour la création de commande - userId: " + userId);
        System.out.println("Items de la commande: " + orderRequest.getItems());

        // Appel au service pour créer la commande
        Order order = orderService.createOrder(userId, orderRequest.getItems());

        // Affichage dans la console de la commande créée
        System.out.println("Commande créée avec succès - ID: " + order.getId());
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    // Ajouter un article à une commande
    @PostMapping("/{orderId}/items")
    public ResponseEntity<Order> addItemToOrder(
            @RequestParam Long userId,
            @PathVariable Long orderId,
            @RequestBody OrderItem orderItem) {
        Order updatedOrder = orderService.addItemToOrder(userId, orderId, orderItem);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    // Récupérer une commande par ID
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrder(orderId);
        if (order == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    // Payer une commande
    @PutMapping("/{orderId}/pay")
    public ResponseEntity<Order> payForOrder(@PathVariable Long orderId) {
        Order paidOrder = orderService.payForOrder(orderId);
        if (paidOrder == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(paidOrder, HttpStatus.OK);
    }

    // Récupérer toutes les commandes pour un utilisateur
   /* @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        if (orders.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    */
}
