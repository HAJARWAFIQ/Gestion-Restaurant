package com.projet_restaurant.servicegateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private RsaConfig rsaConfig;
    public SecurityConfig(RsaConfig rsaConfig) {
        this.rsaConfig = rsaConfig;
    }
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(auth -> auth.pathMatchers("/SERVICE-UTILISATEURS/login").permitAll())
                .authorizeExchange(auth -> auth.pathMatchers("/SERVICE-UTILISATEURS/api/v1/users").permitAll())
               // .authorizeExchange(auth -> auth.pathMatchers("/SERVICE-COMMANDES/").permitAll())
                .authorizeExchange(auth -> auth.pathMatchers("/MENU-SERVICE/graphql").permitAll()/*.hasAnyAuthority("ADMIN", "CLIENT")*/)
                .authorizeExchange(auth -> auth.anyExchange().authenticated())
                         .oauth2ResourceServer(oauth2 -> oauth2.jwt());
                         return http.build();
                         }


    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        NimbusReactiveJwtDecoder jwtDecoder = NimbusReactiveJwtDecoder.withPublicKey(rsaConfig.publicKey()).build();

        return new ReactiveJwtDecoder() {
            @Override
            public Mono<Jwt> decode(String token) throws JwtException {
                return jwtDecoder.decode(token)
                        .doOnNext(jwt -> {
                            System.out.println("Decoded JWT: " + jwt.getClaims());
                            System.out.println("Scope: " + jwt.getClaimAsString("scope"));
                        })
                        .doOnError(error -> System.err.println("Error decoding JWT: " + error.getMessage()));
            }
        };
    }

}
