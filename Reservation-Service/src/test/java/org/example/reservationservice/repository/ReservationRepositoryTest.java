package org.example.reservationservice.repository;

import org.example.reservationservice.entite.Creneau;
import org.example.reservationservice.entite.Reservation;
import org.example.reservationservice.entite.StatutReservation;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReservationRepositoryTest {
    @Autowired
    private ReservationRepository reservationRepository;

    private Reservation reservation1;
    private Reservation reservation2;


    @Test
    void findByTableId() {
        Long tableId = 2L;

        // Arrange
        Reservation reservation1 = new Reservation();
        reservation1.setTableId(1L);
        reservation1.setUserId(1L);
        reservation1.setStatut(StatutReservation.CONFIRMEE);
        reservationRepository.save(reservation1);

        Reservation reservation2 = new Reservation();
        reservation2.setTableId(1L);
        reservation2.setUserId(2L);
        reservation2.setStatut(StatutReservation.EN_ATTENTE);
        reservationRepository.save(reservation2);

        // Act
        List<Reservation> reservations = reservationRepository.findByTableId(tableId);

        // Assert
        assertNotNull(reservations);
        assertEquals(2, reservations.size());
    }


    @Test
    void findByUserId() {
        Reservation reservation = new Reservation();
        reservation.setTableId(3L);
        reservation.setUserId(4L);
        reservation.setStatut(StatutReservation.ANNULEE);
        reservationRepository.save(reservation);

        // Act
        List<Reservation> reservations = reservationRepository.findByUserId(4L);

        // Assert
        assertNotNull(reservations);
        assertEquals(1, reservations.size());
        assertEquals(4L, reservations.get(0).getUserId());
    }
    }



