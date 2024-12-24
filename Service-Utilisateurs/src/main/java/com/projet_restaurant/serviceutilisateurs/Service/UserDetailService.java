package com.projet_restaurant.serviceutilisateurs.Service;

import com.projet_restaurant.serviceutilisateurs.Repository.UserRepository;
import com.projet_restaurant.serviceutilisateurs.Entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository repository;

    public UserDetailService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Optional<User> userOptional = repository.findByUsername(identifier);
        if (!userOptional.isPresent()) {
            userOptional = repository.findByEmail(identifier); // Optionnel : aussi chercher par email si l'utilisateur n'est pas trouvé par username
            if (!userOptional.isPresent()) {
                throw new UsernameNotFoundException("User not found with identifier: " + identifier);
            }
        }
        User u = userOptional.get();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(u.getRole().toString()));

        return new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(), authorities);
    }
}
