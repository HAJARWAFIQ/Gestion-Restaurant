package org.example.reservationservice.repository;

import org.example.reservationservice.entite.Creneau;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CreneauRepository extends JpaRepository<Creneau, Long> {
    Optional<Creneau> findByDateTime(LocalDateTime dateTime); // Recherche un créneau par date/heure
}

