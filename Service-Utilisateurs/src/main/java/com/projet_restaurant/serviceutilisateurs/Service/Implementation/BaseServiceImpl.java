package com.projet_restaurant.serviceutilisateurs.Service.Implementation;

import com.projet_restaurant.serviceutilisateurs.Dto.UserDTO;
import com.projet_restaurant.serviceutilisateurs.Entity.Role;
import com.projet_restaurant.serviceutilisateurs.Entity.User;
import com.projet_restaurant.serviceutilisateurs.Mapper.UserMapper;
import com.projet_restaurant.serviceutilisateurs.Repository.UserRepository;
import com.projet_restaurant.serviceutilisateurs.Service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BaseServiceImpl implements BaseService<UserDTO, Long> {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public BaseServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO create(UserDTO dto) {
        // Convertir le DTO en entité, sauvegarder et retourner le DTO
        User user = userMapper.toEntity(dto);
        // Si le rôle n'est pas fourni, le définir sur "ROLE_CLIENT" par défaut
        if (user.getRole() == null) {
            user.setRole(Role.CLIENT); // Rôle par défaut
        }
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDTO update(Long id, UserDTO dto) {
        // Vérifier si l'utilisateur existe, puis mettre à jour les données
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID : " + id);
        }

        User user = existingUser.get();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        // Ajouter d'autres champs nécessaires à mettre à jour

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserDTO getById(Long id) {
        // Récupérer l'utilisateur par ID et le convertir en DTO
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDTO> getAll() {
        // Récupérer tous les utilisateurs et les convertir en DTOs
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        // Vérifier si l'utilisateur existe et le supprimer
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID : " + id);
        }
        userRepository.deleteById(id);
    }
}
