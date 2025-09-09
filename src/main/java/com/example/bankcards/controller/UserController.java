package com.example.bankcards.controller;

import com.example.bankcards.dto.CardQuery;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/cards")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CardService cardService;

    @GetMapping
    public List<CardQuery> getAll(@AuthenticationPrincipal UserDetails userDetails){
        return cardService.getAll(userDetails.getUsername());
    }
}
