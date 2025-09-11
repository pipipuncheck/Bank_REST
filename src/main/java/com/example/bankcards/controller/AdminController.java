package com.example.bankcards.controller;

import com.example.bankcards.dto.CardQuery;
import com.example.bankcards.dto.UserQuery;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final CardService cardService;

    @GetMapping("/users")
    public List<UserQuery> getAllUsers(){
        return userService.getAll();
    }

    @GetMapping("/cards")
    public Page<CardQuery> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Sort sorting = Sort.by(Sort.Order.desc("balance"));
        Pageable pageable = PageRequest.of(page, size, sorting);

        return cardService.getAllCards(pageable);
    }

    @PostMapping("/cards/create")
    public void createCard(@AuthenticationPrincipal UserDetails userDetails) {
        cardService.createCard(userDetails);
    }

    @PutMapping("/cards/{cardId}/block")
    public void blockCard(@AuthenticationPrincipal UserDetails userDetails,
                          @PathVariable Integer cardId) {
        cardService.blockCard(userDetails, cardId);
    }

    @PutMapping("/cards/{cardId}/activate")
    public void activateCard(@AuthenticationPrincipal UserDetails userDetails,
                             @PathVariable Integer cardId) {
        cardService.activateCard(userDetails, cardId);
    }

    @DeleteMapping("/cards/{cardId}/delete")
    public void deleteCard(@AuthenticationPrincipal UserDetails userDetails,
                           @PathVariable Integer cardId) {
        cardService.deleteCard(userDetails, cardId);
    }

    @PutMapping("/users/{userId}/delete")
    public void deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
    }
}
