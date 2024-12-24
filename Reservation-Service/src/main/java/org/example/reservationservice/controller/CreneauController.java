package org.example.reservationservice.controller;

import org.example.reservationservice.entite.Creneau;
import org.example.reservationservice.repository.CreneauRepository;
import org.example.reservationservice.service.CreneauService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/creneaux")
public class CreneauController {

    @Autowired
    private CreneauService creneauService;
    @Autowired
    private CreneauRepository creneauRepository;

    // Créer un créneau
    @PostMapping("/creer")
    public String creerCreneau(@RequestParam String dateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime); // Parse la date et l'heure
        creneauService.creerCreneau(localDateTime);
        return "Créneau créé avec succès pour " + localDateTime;
    }

}
