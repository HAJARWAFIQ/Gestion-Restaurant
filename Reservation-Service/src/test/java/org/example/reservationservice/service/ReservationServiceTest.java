package org.example.reservationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.reservationservice.client.UserRestFeign;
import org.example.reservationservice.entite.Creneau;
import org.example.reservationservice.entite.Reservation;
import org.example.reservationservice.entite.StatutReservation;
import org.example.reservationservice.repository.CreneauRepository;
import org.example.reservationservice.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {


    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRestFeign userRestFeign;

    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private CreneauRepository creneauRepository;
    @Test
    void updateReservationStatutByAdmin()throws JsonProcessingException {
        Long adminId = 1L;
        Long reservationId = 2L;
        String statut = "CONFIRMEE";
        String token = "validToken";


        Reservation mockReservation = new Reservation();
        mockReservation.setId(reservationId);
        mockReservation.setUserId(123L);
        mockReservation.setStatut(StatutReservation.EN_ATTENTE);

        when(userRestFeign.getUserById(eq("Bearer " + token), eq(adminId)))
                .thenReturn(ResponseEntity.ok(Map.of("role", "ADMIN")));
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(mockReservation));
        when(reservationRepository.save(ArgumentMatchers.any())).thenReturn(mockReservation);

        // Act
        Reservation updatedReservation = reservationService.updateReservationStatutByAdmin(token, adminId, reservationId, statut);

        // Assert
        assertNotNull(updatedReservation);
        assertEquals(StatutReservation.CONFIRMEE, updatedReservation.getStatut());
        verify(rabbitTemplate, times(1)).convertAndSend(eq("notificationQueue"), anyString());

    }




    @Test
    void createReservation() {

        // Données de test
        Long userId = 1L;
        Long tableId = 2L;
        Long creneauId = 3L;
        String token = "Bearer validToken";

        // Mock des réponses des dépendances
        Object userDto = Map.of("id", userId, "name", "John Doe");
        Creneau creneau = new Creneau();
        creneau.setId(creneauId);
        creneau.setDisponible(true);
        Reservation mockReservation = new Reservation();
        mockReservation.setId(100L);

        // Mock Feign Client pour récupérer l'utilisateur
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(userDto, HttpStatus.OK);
        when(userRestFeign.getUserById(token, userId)).thenReturn(responseEntity);
        // Mock du repository pour trouver un créneau
        when(creneauRepository.findById(creneauId)).thenReturn(Optional.of(creneau));

        // Mock pour vérifier les réservations existantes
        when(reservationRepository.findByTableId(tableId)).thenReturn(Collections.emptyList());

        // Mock pour sauvegarder la réservation
        when(reservationRepository.save(ArgumentMatchers.any())).thenReturn(mockReservation);

        // Appel de la méthode
        Reservation result = reservationService.createReservation(userId, tableId, creneauId, token);

        // Assertions
        assertNotNull(result);
        assertEquals(mockReservation.getId(), result.getId());
        verify(userRestFeign).getUserById(token, userId);
        verify(creneauRepository).findById(creneauId);
        verify(reservationRepository).save(Mockito.any(Reservation.class));
    }

    @Test
    void getReservationsByUserId() {
        // Données de test
        Long userId = 1L;
        String token = "Bearer some-token";

        // Simulation des données utilisateur
        Map<String, Object> userDetails = Map.of("id", userId, "name", "John Doe");
        ResponseEntity<Object> userResponse = ResponseEntity.ok(userDetails); // ResponseEntity contenant le Map

        // Simulation des réservations
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setClientDetails(userDetails); // Assurez-vous que clientDetails contient toutes les informations de l'utilisateur

        List<Reservation> reservations = List.of(reservation);

        // Mock des appels
        when(reservationRepository.findByUserId(userId)).thenReturn(reservations);
        when(userRestFeign.getUserById(token, userId)).thenReturn(userResponse);

        // Appel de la méthode à tester
        List<Reservation> result = reservationService.getReservationsByUserId(userId, token);

        // Vérifications
        assertNotNull(result);
        assertEquals(1, result.size());

        // Accédez au corps de la réponse et vérifiez l'ID de l'utilisateur
        Map<String, Object> clientDetails = (Map<String, Object>) result.get(0).getClientDetails();
        assertEquals(userId, clientDetails.get("id"));

        // Vérifier les appels aux mocks
        verify(reservationRepository, times(1)).findByUserId(userId);
        verify(userRestFeign, times(1)).getUserById(token, userId);
    }
    @Test
    void updateReservation() {
        // Données de test
        Long reservationId = 1L;
        Long userId = 1L;
        String token = "Bearer validToken";

        // Réservation existante
        Reservation existingReservation = new Reservation();
        existingReservation.setId(reservationId);
        existingReservation.setUserId(userId);
        existingReservation.setTableId(1L);
        existingReservation.setCreneauId(1L);
        existingReservation.setStatut(StatutReservation.EN_ATTENTE);

        // Mise à jour
        Reservation updatedReservation = new Reservation();
        updatedReservation.setTableId(2L); // Nouvelle table
        updatedReservation.setCreneauId(2L); // Nouveau créneau
        updatedReservation.setStatut(StatutReservation.CONFIRMEE); // Nouveau statut

        // DTO de l'utilisateur récupéré via Feign Client
        Object userDto = Map.of("id", userId, "name", "John Doe");

        // Nouveau créneau
        Creneau newCreneau = new Creneau();
        newCreneau.setId(2L);

        // Simuler les réponses des dépendances
        when(userRestFeign.getUserById(token, userId)).thenReturn(new ResponseEntity<>(userDto, HttpStatus.OK));
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(existingReservation));
        when(creneauRepository.findById(updatedReservation.getCreneauId())).thenReturn(Optional.of(newCreneau));
        when(reservationRepository.save(Mockito.any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // Appel de la méthode
        Reservation result = reservationService.updateReservation(reservationId, userId, token, updatedReservation);

        // Vérifications
        assertNotNull(result);
        assertEquals(updatedReservation.getTableId(), result.getTableId());
        assertEquals(updatedReservation.getCreneauId(), result.getCreneauId());
        assertEquals(updatedReservation.getStatut(), result.getStatut());
        verify(userRestFeign).getUserById(token, userId);
        verify(reservationRepository).findById(reservationId);
        verify(creneauRepository).findById(updatedReservation.getCreneauId());
        verify(reservationRepository).save(Mockito.any(Reservation.class));
    }

    @Test
    void deleteReservation() {    // Données de test
        Long reservationId = 1L;
        Long userId = 123L;

        // Création d'une réservation mockée
        Reservation reservation = new Reservation();
        reservation.setId(reservationId);
        reservation.setUserId(userId);

        // Comportement du mock pour findById
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // Appel de la méthode à tester
        reservationService.deleteReservation(reservationId, userId);

        // Vérification que la méthode delete a bien été appelée
        verify(reservationRepository, times(1)).delete(reservation);
    }
}