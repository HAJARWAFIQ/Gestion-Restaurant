package org.example.reservationservice.service;

import org.example.reservationservice.entite.Creneau;
import org.example.reservationservice.repository.CreneauRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class CreneauServiceTest {
    @Mock
    private CreneauRepository creneauRepository;

    @InjectMocks
    private CreneauService creneauService;

    @Test
    void creerCreneau() {
        // Arrange : Préparer les données et le comportement attendu
        LocalDateTime dateTime = LocalDateTime.now();
        Creneau creneau = new Creneau();
        creneau.setDateTime(dateTime);
        creneau.setDisponible(true);

        // Configurer le mock pour retourner l'objet simulé
        when(creneauRepository.save(any(Creneau.class))).thenReturn(creneau);

        // Act : Appeler la méthode à tester
        Creneau result = creneauService.creerCreneau(dateTime);

        // Assert : Vérifier le comportement et les résultats
        assertNotNull(result, "Le résultat ne doit pas être null");
        assertEquals(dateTime, result.getDateTime(), "Les dates doivent correspondre");
        // Vérifier que la méthode save a été appelée une fois
        verify(creneauRepository).save(any(Creneau.class));
    }
    }
