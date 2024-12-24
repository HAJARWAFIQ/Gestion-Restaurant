package org.example.reservationservice.controller;

import org.example.reservationservice.entite.Reservation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.reservationservice.entite.StatutReservation;
import org.example.reservationservice.service.ReservationService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;

import java.util.List;



@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservation API", description = "API pour gérer les réservations")
public class ReservationController {
    private static final Logger log = (Logger) LoggerFactory.getLogger(ReservationController.class);

    @Autowired
    private ReservationService reservationService;
    @PutMapping("/{reservationId}/statut")
    public ResponseEntity<?> updateReservationStatut(
            @RequestHeader("Authorization") String token,
            @PathVariable Long reservationId,
            @RequestParam Long adminId,
            @RequestParam String statut) {

        log.info("Admin ID: {}", adminId);
        log.info("Reservation ID: {}", reservationId);
        log.info("Statut: {}", statut);

        // Validation des paramètres
        if (adminId == null || reservationId == null || statut == null || statut.isEmpty()) {
            return ResponseEntity.badRequest().body("Admin ID, Reservation ID et Statut sont obligatoires.");
        }

        try {
            // Vérifiez que le statut est valide
            StatutReservation statutEnum = StatutReservation.valueOf(statut.toUpperCase());

            Reservation reservation = reservationService.updateReservationStatutByAdmin(token, adminId, reservationId, statutEnum.name());
            return ResponseEntity.ok(reservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Statut invalide : " + statut);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Récupérer un utilisateur par son ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        try {
            Object user = reservationService.getUserById(userId, token);
            if (user != null) {
                return ResponseEntity.ok(user);  // Si l'utilisateur est trouvé, renvoyer les données de l'utilisateur
            } else {
                return ResponseEntity.status(404).body("Utilisateur non trouvé");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("Erreur lors de la récupération de l'utilisateur");
        }
    }

    @Operation(summary = "Créer une réservation", description = "Permet à un client de créer une réservation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réservation créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Créneau non disponible ou données invalides")
    })


    // Créer une nouvelle réservation
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestParam Long userId,
                                                         @RequestParam Long tableId,
                                                         @RequestParam Long creneauId,
                                                         @RequestHeader("Authorization") String token) {
        try {
            Reservation newReservation = reservationService.createReservation(userId, tableId, creneauId, token);
            return ResponseEntity.ok(newReservation);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(null);  // Si une erreur survient (par exemple, créneau déjà réservé)
        }
    }

    @Operation(summary = "Récupérer les réservations d'un client", description = "Retourne toutes les réservations effectuées par un client spécifique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des réservations retournée avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucune réservation trouvée pour ce client")
    })


    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Reservation>> getReservationsByUserId(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token) {

        List<Reservation> reservations = reservationService.getReservationsByUserId(userId, token);

        if (reservations.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(reservations);
    }


    @Operation(summary = "Mettre à jour une réservation", description = "Permet de mettre à jour une réservation existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réservation mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Réservation ou créneau non trouvé")
    })
    // Mettre à jour une réservation
    @PutMapping("/{reservationId}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long reservationId,
                                                         @RequestParam Long userId,
                                                         @RequestHeader("Authorization") String token,
                                                         @RequestBody Reservation updatedReservation) {
        log.info("Requête reçue : reservationId={}, userId={}, token={}", reservationId, userId, token);
        log.info("Données reçues dans le corps : tableId={}, creneauId={}, statut={}",
                updatedReservation.getTableId(), updatedReservation.getCreneauId(), updatedReservation.getStatut());

        try {
            Reservation updated = reservationService.updateReservation(reservationId, userId, token, updatedReservation);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour : {}", e.getMessage());
            return ResponseEntity.badRequest().body(null); // Envoyer une réponse claire
        }
    }

    @Operation(summary = "Supprimer une réservation", description = "Supprime une réservation existante et libère le créneau.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Réservation supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    })
    // Supprimer une réservation
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<String> deleteReservation(@PathVariable Long reservationId, @RequestParam Long userId) {
        try {
            // Appel du service pour supprimer la réservation
            reservationService.deleteReservation(reservationId, userId);
            return ResponseEntity.ok("Réservation supprimée avec succès.");
        } catch (RuntimeException e) {
            // En cas d'erreur, renvoyer une réponse d'erreur
            return ResponseEntity.status(403).body(e.getMessage());  // 403 Forbidden si l'utilisateur tente de supprimer une réservation qui ne lui appartient pas
        }
    }


}
