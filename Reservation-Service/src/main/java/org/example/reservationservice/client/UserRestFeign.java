package org.example.reservationservice.client;


import org.example.reservationservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name ="SERVICE-UTILISATEURS",url = "http://localhost:8082/api/v1/users" ,  configuration = FeignConfig.class)
public interface UserRestFeign {
    @GetMapping("/{id}")
    ResponseEntity<Object> getUserById(@RequestHeader("Authorization") String token,
                                       @PathVariable("id") Long id);
}


