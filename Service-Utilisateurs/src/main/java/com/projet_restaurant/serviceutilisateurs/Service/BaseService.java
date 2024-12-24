package com.projet_restaurant.serviceutilisateurs.Service;

import java.util.List;

public interface BaseService <UserDTO, Long> {
    UserDTO create(UserDTO dto);
    UserDTO update(Long id, UserDTO dto);
    UserDTO getById(Long id);
    List<UserDTO> getAll();
    void delete(Long id);
}
