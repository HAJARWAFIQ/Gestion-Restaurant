package org.example.reservationservice.entite;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Creneau {
   

    public Long getId() {
        return id;
    }

    public Boolean getDisponible() {
        return disponible;
    }


    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "creneau", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();
    private LocalDateTime dateTime; // Date et heure du créneau

    private Boolean disponible = true; // Indique si le créneau est disponible
}
