package org.example.reservationservice.entite;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    private Long userId;
    public Object getClientDetails() {
        return clientDetails;
    }

    public void setClientDetails(Object clientDetails) {
        this.clientDetails = clientDetails;
    }

    @Transient // Ce champ n'est pas stocké dans la base de données
    private Object clientDetails; // Pour stocker temporairement les données de l'utilisateur récupérées

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }
    private boolean tableDisponible = true;

    public boolean isTableDisponible() {
        return tableDisponible;
    }

    public void setTableDisponible(boolean tableDisponible) {
        this.tableDisponible = tableDisponible;
    }


    private Long tableId;

    public StatutReservation getStatut() {
        return statut;
    }

    public void setStatut(StatutReservation statut) {
        this.statut = statut;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Creneau getCreneau() {
        return creneau;
    }

    public void setCreneau(Creneau creneau) {
        this.creneau = creneau;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCreneauId() {
        return creneauId;
    }

    public void setCreneauId(Long creneauId) {
        this.creneauId = creneauId;
    }




    private Long creneauId; // ID du créneau (lié à la table Creneau)

    @ManyToOne()
    @JoinColumn(name = "creneauId", referencedColumnName = "id", insertable = false, updatable = false)
    // Clé étrangère vers la table Creneau
    @JsonIgnore
    private Creneau creneau; // Le créneau associé

    @Enumerated(EnumType.STRING)
    private StatutReservation statut = StatutReservation.EN_ATTENTE;

    private LocalDateTime createdAt = LocalDateTime.now();
}
