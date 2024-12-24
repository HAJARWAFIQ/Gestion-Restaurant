package com.projet_restaurant.serviceutilisateurs.Service;

import com.projet_restaurant.serviceutilisateurs.Dto.UserDTO;

public interface ClientService extends BaseService<UserDTO, Long>{
    UserDTO getCurrentUser();
    void changePassword(Long userId, String oldPassword, String newPassword);
    void resetPassword(String email);
}
