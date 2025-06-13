package com.Controller;

import com.Domain.User;
import com.DTO.UserDTO;
import com.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** CREATE */
    @PostMapping
    public ResponseEntity<User> create(@RequestBody UserDTO dto) {
        User toSave = new User(null, dto.getUserName(), dto.getPassword());
        User saved = userService.add(toSave);
        URI location = URI.create("/api/users/" + saved.getUserID());
        return ResponseEntity.created(location).body(saved);
    }

    /** GET ALL */
    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        List<User> users = userService.getAll();
        return users.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(users);
    }

    /** GET BY ID */
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable int id) {
        User found = userService.getById(id);
        return found != null
                ? ResponseEntity.ok(found)
                : ResponseEntity.notFound().build();
    }

    /** UPDATE */
    @PutMapping("/{id}")
    public ResponseEntity<User> update(
            @PathVariable int id,
            @RequestBody User updated) {
        User found = userService.getById(id);
        if (found == null) {
            return ResponseEntity.notFound().build();
        }
        userService.update(id, updated);
        User updatedEntity = userService.getById(id);
        return ResponseEntity.ok(updatedEntity);
    }

    /** DELETE */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        userService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody UserDTO dto) {
        Optional<User> user = userService.authenticate(dto.getUserName(), dto.getPassword());
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
