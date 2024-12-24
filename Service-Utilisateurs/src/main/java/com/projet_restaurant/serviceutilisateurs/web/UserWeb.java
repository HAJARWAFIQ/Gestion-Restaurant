package com.projet_restaurant.serviceutilisateurs.web;
        import com.projet_restaurant.serviceutilisateurs.Dto.UserDTO;
        import com.projet_restaurant.serviceutilisateurs.Service.BaseService;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
       // import org.springframework.security.access.prepost.PreAuthorize;
        import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserWeb  {

    private final BaseService<UserDTO, Long> baseService;

    @Autowired
    public UserWeb(BaseService<UserDTO, Long> baseService , PasswordEncoder passwordEncoder) {
        this.baseService = baseService;
    }
    @PostMapping()
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = baseService.create(userDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    /*@PreAuthorize("hasRole('ADMIN')")*/
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = baseService.update(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDTO = baseService.getById(id);
        return ResponseEntity.ok(userDTO);
    }
    /*@PreAuthorize("hasRole('ADMIN')")*/
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> userDTOs = baseService.getAll();
        return ResponseEntity.ok(userDTOs);
    }
    /*@PreAuthorize("hasRole('ADMIN')")*/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        baseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
