package org.example.reservationservice.service;

import org.example.reservationservice.entite.Creneau;
import org.example.reservationservice.repository.CreneauRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CreneauService {
    @Autowired
    private CreneauRepository creneauRepository;

    // Méthode pour créer un créneau
    public Creneau creerCreneau(LocalDateTime dateTime) {
        Creneau creneau = new Creneau();
        creneau.setDateTime(dateTime);
        creneau.setDisponible(true); // Par défaut, le créneau est disponible
        return creneauRepository.save(creneau);
    }

    // Méthode pour vérifier si un créneau est disponible
   /* public Optional<Creneau> trouverCreneauDisponible(LocalDateTime dateTime) {
        return creneauRepository.findByDateTime(dateTime);
    }

    // Méthode pour réserver un créneau (le marquer comme indisponible)
    /* public String reserverCreneau(LocalDateTime dateTime) {
       // Optional<Creneau> creneau = trouverCreneauDisponible(dateTime);

        if (creneau.isPresent() && creneau.get().getDisponible()) {
            Creneau creneauToReserve = creneau.get();
            creneauToReserve.setDisponible(false); // Le créneau devient réservé (indisponible)
            creneauRepository.save(creneauToReserve);
            return "Créneau réservé avec succès pour " + dateTime;
        } else {
            return "Créneau non disponible pour la date et l'heure demandées.";
        }
    }*/

}
