package org.example.reservationservice.controller;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.reservationservice.entite.Reservation;
import org.example.reservationservice.entite.StatutReservation;
import org.example.reservationservice.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @MockitoBean
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private Reservation reservation;

    public ReservationControllerTest() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void updateReservationStatut() throws Exception {
        // Données de test
        Long reservationId = 1L;
        Long adminId = 2L;
        String token = "Bearer valid-token";
        String statut = "CONFIRMEE";

        // Création de la réservation simulée après mise à jour du statut
        Reservation updatedReservation = new Reservation();
        updatedReservation.setId(reservationId);
        updatedReservation.setStatut(StatutReservation.valueOf(statut));

        // Simulation du service pour mettre à jour le statut de la réservation
        when(reservationService.updateReservationStatutByAdmin(eq(token), eq(adminId), eq(reservationId), eq(statut.toUpperCase())))
                .thenReturn(updatedReservation);

        // Effectuer la requête PUT et vérifier la réponse
        mockMvc.perform(put("/api/reservations/{reservationId}/statut", reservationId)
                        .header("Authorization", token)
                        .param("adminId", adminId.toString())
                        .param("statut", statut))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedReservation.getId()))
                .andExpect(jsonPath("$.statut").value(updatedReservation.getStatut().toString()));
    }


    @Test
    void createReservation()throws Exception {
        Long userId = 1L;
        Long tableId = 2L;
        Long creneauId = 3L;
        String token = "Bearer valid-token";

        // Simulation de la réponse du service
        Reservation expectedReservation = new Reservation(/* paramètres */);
        when(reservationService.createReservation(userId, tableId, creneauId, token))
                .thenReturn(expectedReservation);

        mockMvc.perform(post("/api/reservations")
                        .param("userId", userId.toString())
                        .param("tableId", tableId.toString())
                        .param("creneauId", creneauId.toString())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedReservation.getId()))
                .andExpect(jsonPath("$.tableId").value(expectedReservation.getTableId()));
    }

    @Test
    void getReservationsByUserId()  throws Exception {// Données de test
    Long userId = 2L;
    String token = "Bearer valid-token";

    // Création d'une liste de réservations simulées
    Reservation reservation1 = new Reservation();
    reservation1.setId(1L);
    reservation1.setTableId(3L);
    reservation1.setCreneauId(4L);
    reservation1.setStatut(StatutReservation.valueOf("CONFIRMEE"));

    Reservation reservation2 = new Reservation();
    reservation2.setId(2L);
    reservation2.setTableId(5L);
    reservation2.setCreneauId(6L);
    reservation2.setStatut(StatutReservation.valueOf("EN_ATTENTE"));

    List<Reservation> reservations = Arrays.asList(reservation1, reservation2);

    // Simulation du service retournant les réservations pour un utilisateur donné
    when(reservationService.getReservationsByUserId(eq(userId), eq(token))).thenReturn(reservations);

    // Effectuer la requête GET et vérifier la réponse
    mockMvc.perform(get("/api/reservations/users/{userId}", userId)
                    .header("Authorization", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(reservation1.getId()))
            .andExpect(jsonPath("$[0].tableId").value(reservation1.getTableId()))
            .andExpect(jsonPath("$[0].creneauId").value(reservation1.getCreneauId()))
            .andExpect(jsonPath("$[0].statut").value(reservation1.getStatut().toString()))
            .andExpect(jsonPath("$[1].id").value(reservation2.getId()))
            .andExpect(jsonPath("$[1].tableId").value(reservation2.getTableId()))
            .andExpect(jsonPath("$[1].creneauId").value(reservation2.getCreneauId()))
            .andExpect(jsonPath("$[1].statut").value(reservation2.getStatut().toString()));
}


    @Test
    void updateReservation()throws Exception {
        // Préparer les données de test
        Long reservationId = 1L;
        Long userId = 2L;
        String token = "Bearer valid-token";

        Reservation updatedReservation = new Reservation();
        updatedReservation.setId(reservationId);
        updatedReservation.setTableId(3L);
        updatedReservation.setCreneauId(4L);
        updatedReservation.setStatut(StatutReservation.valueOf("CONFIRMEE"));

        // Simulation de la réponse du service
        when(reservationService.updateReservation(eq(reservationId), eq(userId), eq(token), any(Reservation.class)))
                .thenReturn(updatedReservation);

        // Effectuer la requête et vérifier les résultats
        mockMvc.perform(put("/api/reservations/{reservationId}", reservationId)
                        .param("userId", userId.toString())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "tableId": 3,
                            "creneauId": 4,
                            "statut": "TERMINEE"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedReservation.getId()))
                .andExpect(jsonPath("$.tableId").value(updatedReservation.getTableId()))
                .andExpect(jsonPath("$.creneauId").value(updatedReservation.getCreneauId()))
                .andExpect(jsonPath("$.statut").value(updatedReservation.getStatut().toString().trim())); // Assurez-vous que les espaces sont pris en compte
    }

    @Test
    void deleteReservation() throws Exception {
        // Données de test
        Long reservationId = 1L;
        Long userId = 2L;
        String token = "Bearer valid-token";

        // Simulation de la suppression de la réservation via le service
        doNothing().when(reservationService).deleteReservation(eq(reservationId), eq(userId));

        // Effectuer la requête DELETE et vérifier la réponse
        mockMvc.perform(delete("/api/reservations/{reservationId}", reservationId)
                        .param("userId", userId.toString())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().string("Réservation supprimée avec succès."));
    }
    }
