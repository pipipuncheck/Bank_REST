package com.example.bankcards.security;


import com.example.bankcards.dto.auth.AuthCommand;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.exception.InvalidDataException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User authenticate(String username, String password){

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (passwordEncoder.matches(password, user.getPassword()))
            return user;

        throw new InvalidDataException("Wrong password");
    }

    public User register(AuthCommand command){

        Optional<User> userDB = userRepository.findUserByUsername(command.getUsername());

        if(userDB.isPresent())
            throw new InvalidDataException("User with this login already exists");

        User user = User.builder()
                .username(command.getUsername())
                .email(command.getEmail())
                .password(passwordEncoder.encode(command.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        return user;
    }

}
