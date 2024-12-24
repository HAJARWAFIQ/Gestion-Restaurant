package org.example.reservationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.reservationservice.repository.CreneauRepository;
import org.example.reservationservice.service.CreneauService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/*
@WebMvcTest(CreneauController.class)
class CreneauControllerTest {

    public CreneauControllerTest() {
        MockitoAnnotations.openMocks(this);
    }
    @MockitoBean
    private CreneauService creneauService;

    @InjectMocks
    private CreneauController creneauController;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CreneauRepository creneauRepository;

    @Test
    void creerCreneau() throws Exception {
        // Données de test
        String dateTime = "2024-12-22T14:30:00";  // Exemple de date et heure
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime);

        // Simuler la logique du service
       when(creneauService.creerCreneau(localDateTime)).thenReturn("Créneau créé avec succès pour " + localDateTime);

         Effectuer la requête POST et vérifier la réponse
        mockMvc.perform(post("/api/creneaux/creer")
                        .param("dateTime", dateTime))
                .andExpect(status().isOk())
                .andExpect(content().string("Créneau créé avec succès pour " + localDateTime));
    }
    }*/