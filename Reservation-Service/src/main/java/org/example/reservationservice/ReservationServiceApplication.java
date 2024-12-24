package org.example.reservationservice;

import org.example.reservationservice.middleware.CorsConfig;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
@EnableFeignClients
@Import(CorsConfig.class)
public class ReservationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class, args);
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:4200")); // Origine autorisée
        configuration.setAllowedMethods(Collections.singletonList("*")); // Toutes les méthodes HTTP
        configuration.setAllowedHeaders(Collections.singletonList("*")); // Tous les en-têtes
        configuration.setAllowCredentials(true); // Autoriser l'envoi des cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Appliquer les CORS à toutes les routes
        return source;
    }



}
