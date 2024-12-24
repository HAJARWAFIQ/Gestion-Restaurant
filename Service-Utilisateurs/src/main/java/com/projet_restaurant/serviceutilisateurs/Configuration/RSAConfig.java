package com.projet_restaurant.serviceutilisateurs.Configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties("rsa")
public record RSAConfig(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
}
