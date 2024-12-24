package org.example.reservationservice.repository;

import org.example.reservationservice.entite.Creneau;
import org.example.reservationservice.entite.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository  extends JpaRepository<Reservation, Long> {

    List<Reservation> findByTableId(Long tableId);
    public Reservation findFirstByCreneauAndTableIdNot(Creneau creneau, Long tableId);
    List<Reservation> findByUserId(Long userId);


    // Vérifier si une table est disponible
    @Query("SELECT r FROM Reservation r WHERE r.tableId = :idTable AND r.tableDisponible = true")
    Optional<Reservation> findAvailableTable(@Param("tableId") Long idTable);

    // Marquer une table comme réservée
    @Modifying
    @Query("UPDATE Reservation r SET r.tableDisponible = false WHERE r.tableId = :idTable")
    void reserveTable(@Param("tableId") Long idTable);

    // Libérer une table
    @Modifying
    @Query("UPDATE Reservation r SET r.tableDisponible = true WHERE r.tableId = :idTable")
    void releaseTable(@Param("tableId") Long idTable);


}
