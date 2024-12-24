package org.example.reservationservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.example.reservationservice.client.UserRestFeign;
import org.example.reservationservice.controller.ReservationController;
import org.example.reservationservice.entite.Creneau;
import org.example.reservationservice.entite.Reservation;
import org.example.reservationservice.entite.StatutReservation;
import org.example.reservationservice.repository.CreneauRepository;
import org.example.reservationservice.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private static final Logger log = (Logger) LoggerFactory.getLogger(ReservationController.class);
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CreneauRepository creneauRepository;
    @Autowired
    private UserRestFeign userRestFeign;// Injection du client Feign
    @Autowired
    private RabbitTemplate rabbitTemplate;
    // verifier le role de user est Admin pour update status
    private boolean isUserAdmin(String token, Long userId) {
        // Ajouter "Bearer" si nécessaire
        if (!token.startsWith("Bearer ")) {
            token = "Bearer " + token;
        }

        log.info("Token utilisé pour Feign Client : {}", token);

        // Appel au service utilisateur via Feign Client
        ResponseEntity<Object> response = userRestFeign.getUserById(token, userId);

        if (response.getStatusCode().is2xxSuccessful()) {
            // Analyse des données utilisateur pour vérifier le rôle
            Map<String, Object> userDetails = (Map<String, Object>) response.getBody();
            String role = (String) userDetails.get("role");
            log.info("Rôle de l'utilisateur récupéré : {}", role);
            return "ADMIN".equalsIgnoreCase(role);
        } else {
            log.error("Erreur lors de l'appel au service utilisateur : {}", response.getStatusCode());
            throw new RuntimeException("Impossible de récupérer les détails de l'utilisateur : " + userId);
        }
    }



    @Transactional
    public Reservation updateReservationStatutByAdmin(String token, Long adminId, Long reservationId, String statut) {
        log.info("Admin ID: {}", adminId);
        log.info("Reservation ID: {}", reservationId);
        log.info("Statut: {}", statut);

        // Vérifier si l'utilisateur est un admin
        boolean isAdmin = isUserAdmin(token,adminId );
        log.info("Is Admin: {}", isAdmin);

        if (!isAdmin) {
            throw new RuntimeException("Seuls les admins peuvent mettre à jour le statut des réservations.");
        }

        // Récupérer la réservation
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        log.info("Reservation trouvée : {}", reservation);

        // Mettre à jour le statut
        try {
            StatutReservation statutEnum = StatutReservation.valueOf(statut.toUpperCase());
            reservation.setStatut(statutEnum);
            reservationRepository.save(reservation);
            log.info("Statut mis à jour pour la réservation ID: {}", reservationId);

            // Créer et envoyer la notification
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("userId", reservation.getUserId());
            notificationData.put("reservationId", reservation.getId());
            notificationData.put("statut", statutEnum.name());

            String jsonMessage = new ObjectMapper().writeValueAsString(notificationData);

            rabbitTemplate.convertAndSend("notificationQueue", jsonMessage);
            log.info("Message publié dans RabbitMQ : {}", jsonMessage);


            return reservation;
        } catch (IllegalArgumentException e) {
            log.error("Statut invalide : {}", statut);
            throw new RuntimeException("Statut invalide : " + statut);
        } catch (JsonProcessingException e) {
            log.error("Erreur de conversion JSON : {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la conversion en JSON : " + e.getMessage());
        }
    }




    // Méthode pour récupérer les informations de l'utilisateur
    public Object getUserById(Long userId, String token) {
        System.out.println("Attempting to get user with ID: " + userId + " using token: " + token);

        // Appel Feign pour récupérer les informations de l'utilisateur
        ResponseEntity<Object> response = userRestFeign.getUserById(token, userId);

        System.out.println("Status Code from Feign response: " + response.getStatusCodeValue());
        Object userDto = response.getBody();

        if (userDto == null) {
            System.out.println("Error: Received null UserDto");
        } else {
            System.out.println("Successfully retrieved UserDto for user ID " + userId);
        }
        return userDto;
    }

    public Reservation createReservation(Long userId, Long tableId, Long creneauId, String token) {

        // Étape 1 : Vérifier l'utilisateur via Feign avec le token
        Object userDto = userRestFeign.getUserById(token, userId);
        if (userDto == null) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID : " + userId);
        }

        // Vérifier si le créneau existe
        Creneau creneau = creneauRepository.findById(creneauId)
                .orElseThrow(() -> new RuntimeException("Créneau non trouvé avec ID : " + creneauId));

        // Vérifier si le créneau est déjà réservé pour la table donnée
        List<Reservation> existingReservations = reservationRepository.findByTableId(tableId);
        for (Reservation reservation : existingReservations) {
            if (reservation.getCreneauId().equals(creneauId) && !reservation.isTableDisponible()) {
                throw new RuntimeException("La table " + tableId + " est déjà réservée pour ce créneau.");
            }
        }

        // Créer une nouvelle réservation
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);  // Associer l'ID de l'utilisateur
        reservation.setClientDetails(userDto); // Associer les détails complets récupérés de l'utilisateur
        reservation.setTableId(tableId);
        reservation.setCreneauId(creneauId);
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setTableDisponible(false); // La table devient indisponible

        // Sauvegarder la réservation
        Reservation savedReservation = reservationRepository.save(reservation);

        // Mettre à jour le créneau pour indiquer qu'il n'est plus disponible
        creneau.getReservations().add(savedReservation);
        creneau.setDisponible(false);
        creneauRepository.save(creneau);

        return savedReservation;
    }





    public List<Reservation> getReservationsByUserId(Long userId, String token) {
        // Étape 1 : Récupérer toutes les réservations associées à cet userId
        List<Reservation> reservations = reservationRepository.findByUserId(userId);

        // Étape 2 : Remplir le champ clientDetails en appelant le microservice utilisateur
        return reservations.stream().map(reservation -> {
            // Appel au microservice pour récupérer les détails utilisateur
            ResponseEntity<Object> userResponse = userRestFeign.getUserById(token, reservation.getUserId());
            // Extraire le corps de la réponse et le mettre dans clientDetails
            Map<String, Object> userDto = (Map<String, Object>) userResponse.getBody();
            reservation.setClientDetails(userDto); // Associer les détails récupérés
            return reservation;
        }).collect(Collectors.toList());
    }



    public Reservation updateReservation(Long reservationId, Long userId, String token, Reservation updatedReservation) {
        // Étape 1 : Valider l'utilisateur via le service utilisateur
        Object userDto = userRestFeign.getUserById(token, userId);
        if (userDto == null) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID : " + userId);
        }

        // Étape 2 : Trouver la réservation existante
        Reservation existingReservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        // Assurez-vous que clientDetails est initialisé
        if (existingReservation.getClientDetails() == null) {
            existingReservation.setClientDetails(userDto);
        }

        // Vérifier que la réservation appartient au client
        if (!existingReservation.getUserId().equals(userId)) {
            throw new RuntimeException("Vous ne pouvez pas modifier une réservation qui ne vous appartient pas.");
        }

        // Mise à jour des autres champs
        if (updatedReservation.getCreneauId() != null && !updatedReservation.getCreneauId().equals(existingReservation.getCreneauId())) {
            // Logique pour mettre à jour le créneau
            Creneau newCreneau = creneauRepository.findById(updatedReservation.getCreneauId())
                    .orElseThrow(() -> new RuntimeException("Nouveau créneau non trouvé"));
            existingReservation.setCreneauId(newCreneau.getId());
        }

        if (updatedReservation.getTableId() != null && !updatedReservation.getTableId().equals(existingReservation.getTableId())) {
            existingReservation.setTableId(updatedReservation.getTableId());
        }

        if (updatedReservation.getStatut() != null) {
            existingReservation.setStatut(updatedReservation.getStatut());
        }

        return reservationRepository.save(existingReservation);
    }



    public void deleteReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (!reservation.getUserId().equals(userId)) {
            throw new RuntimeException("Vous ne pouvez pas supprimer une réservation qui ne vous appartient pas.");
        }

        reservationRepository.delete(reservation);
    }



}



