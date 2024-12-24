package com.projet_restaurant.serviceutilisateurs.Dto;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    private String password;
    private String email;

    // Getters et Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
