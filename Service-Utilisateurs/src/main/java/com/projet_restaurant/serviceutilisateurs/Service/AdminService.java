package com.projet_restaurant.serviceutilisateurs.Service;

import com.projet_restaurant.serviceutilisateurs.Dto.UserDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminService extends BaseService<UserDTO, Long> {
    Page<UserDTO> getUsers(int page, int size, String sortBy, String direction); // Obtenir une liste paginée
    List<UserDTO> searchUsers(String keyword); // Rechercher des utilisateurs par mot-clé

}
