package com.ua.clear.controller;

import com.ua.clear.dto.UserDto;
import com.ua.clear.model.User;
import com.ua.clear.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserRepository userRepository;

    @Value("${user.minimum.age:18}")
    private int minimumAge;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid UserDto userDTO) {
        LocalDate today = LocalDate.now();
        if (Period.between(userDTO.getBirthDate(), today).getYears() < minimumAge) {
            return ResponseEntity.badRequest().body(null);
        }

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setBirthDate(userDTO.getBirthDate());
        user.setAddress(userDTO.getAddress());
        user.setPhoneNumber(userDTO.getPhoneNumber());

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserDto userDTO) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getFirstName() != null) {
            user.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            user.setLastName(userDTO.getLastName());
        }
        if (userDTO.getBirthDate() != null) {
            if (userDTO.getBirthDate().isAfter(LocalDate.now())) {
                return ResponseEntity.badRequest().build();
            }
            user.setBirthDate(userDTO.getBirthDate());
        }
        user.setAddress(userDTO.getAddress());
        user.setPhoneNumber(userDTO.getPhoneNumber());

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<User>> searchUsers(
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to) {
        if (from.isAfter(to)) {
            return ResponseEntity.badRequest().build();
        }

        Collection<User> users = userRepository.findAll();
        Collection<User> filteredUsers = new ArrayList<>();

        for (User user : users) {
            LocalDate birthDate = user.getBirthDate();
            if (birthDate.isAfter(from) && birthDate.isBefore(to)) {
                filteredUsers.add(user);
            }
        }

        return ResponseEntity.ok(filteredUsers);
    }
}