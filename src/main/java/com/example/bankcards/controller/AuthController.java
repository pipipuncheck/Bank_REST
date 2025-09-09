package com.example.bankcards.controller;

import com.example.bankcards.dto.auth.AuthCommand;
import com.example.bankcards.dto.auth.AuthQuery;
import com.example.bankcards.dto.auth.JwtQuery;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.AuthService;
import com.example.bankcards.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public JwtQuery registration(@RequestBody AuthCommand command){
        User user = authService.register(command);
        return new JwtQuery(jwtService.generateToken(user));
    }

    @PostMapping("/signin")
    public JwtQuery login(@RequestBody AuthQuery authQuery){
        User user = authService.authenticate(authQuery.getUsername(), authQuery.getPassword());
        return new JwtQuery(jwtService.generateToken(user));
    }
}
