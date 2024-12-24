package com.projet_restaurant.serviceutilisateurs.Mapper;

import com.projet_restaurant.serviceutilisateurs.Dto.UserDTO;
import com.projet_restaurant.serviceutilisateurs.Entity.User;
import com.projet_restaurant.serviceutilisateurs.Entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    public UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(user.getId(), user.getUsername(), user.getPassword(), user.getEmail(), user.getRole());
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        String encodedPassword = passwordEncoder.encode(dto.getPassword()); // Encoder le mot de passe ici
        return new User(dto.getId(), dto.getUsername(), encodedPassword,dto.getEmail(), dto.getRole()); // Utilise la liste des commandes du DTO);

    }
}

