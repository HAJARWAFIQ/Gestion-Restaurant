package com.projet_restaurant.servicecommandes.Service.Implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.projet_restaurant.servicecommandes.Entity.Order;
import com.projet_restaurant.servicecommandes.Entity.OrderItem;
import com.projet_restaurant.servicecommandes.Entity.OrderStatus;
import com.projet_restaurant.servicecommandes.Repository.OrderRepository;
import com.projet_restaurant.servicecommandes.Repository.OrderItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private final RabbitTemplate rabbitTemplate;

    public OrderService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // Créer une nouvelle commande pour un utilisateur
    @Transactional
    public Order createOrder(Long userId, List<OrderItem> items) {
        System.out.println("Début de la création de commande pour userId: " + userId);

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);
        System.out.println("Nouvelle commande initialisée avec statut: " + order.getStatus());

        List<OrderItem> managedItems = new ArrayList<>();
        for (OrderItem item : items) {
            System.out.println("Traitement de l'item: " + item.getProductName());

            OrderItem newItem = new OrderItem(); // Crée une nouvelle instance
            newItem.setIdProduct(item.getIdProduct());
            newItem.setProductName(item.getProductName());
            newItem.setPrice(item.getPrice());
            newItem.setQuantity(item.getQuantity());
            newItem.setOrder(order); // Associe à la commande
            managedItems.add(newItem);

            System.out.println("Item créé et associé à la commande: " + newItem.getProductName());
        }

        order.setItems(managedItems);
        System.out.println("Tous les items ont été associés à la commande. Total items: " + managedItems.size());

        // Sauvegarde la commande avec ses items
        Order savedOrder = orderRepository.save(order);
        System.out.println("Commande sauvegardée avec ID: " + savedOrder.getId());

        // Publier la commande dans RabbitMQ ou autres opérations
        envoyerCommande(savedOrder);
        System.out.println("Commande publiée pour traitement asynchrone.");

        return savedOrder;
    }


    // Ajouter un article à une commande existante
    @Transactional
    public Order addItemToOrder(Long userId, Long orderId, OrderItem item) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized user");
        }

        order.getItems().add(item);
        return orderRepository.save(order);
    }

    // Marquer la commande comme payée
    @Transactional
    public Order payForOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.PAID);
        return orderRepository.save(order);
    }

    // Récupérer une commande
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    }


    public void envoyerCommande(Order commande) {
        System.out.println("Envoi de la commande à RabbitMQ : " + commande);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Ajouter le module pour gérer LocalDateTime

        try {
            // Convertir la commande en JSON
            String commandeJson = objectMapper.writeValueAsString(commande);

            // Envoi à RabbitMQ
            rabbitTemplate.convertAndSend("commande.queue", commandeJson);
            System.out.println("Commande envoyée avec succès.");
        } catch (JsonProcessingException e) {
            // Log de l'erreur et gestion fine de l'exception
            System.err.println("Erreur de conversion de la commande en JSON : " + e.getMessage());
            throw new RuntimeException("Erreur lors de la conversion de la commande en JSON", e);
        } catch (Exception e) {
            // Gestion des erreurs RabbitMQ (si RabbitMQ est inaccessible par exemple)
            System.err.println("Erreur lors de l'envoi de la commande à RabbitMQ : " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de la commande à RabbitMQ", e);
        }
    }

}
