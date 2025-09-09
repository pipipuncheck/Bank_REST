package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCommand;
import com.example.bankcards.dto.UserQuery;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cards")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final CardService cardService;

    @GetMapping
    public List<UserQuery> getAllUsers(){
        return userService.getAll();
    }

    @PostMapping("/create")
    public void createCard(@RequestBody CardCommand command){

        cardService.createCard(command);
    }
}
