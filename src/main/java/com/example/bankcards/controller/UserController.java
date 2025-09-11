package com.example.bankcards.controller;

import com.example.bankcards.dto.CardQuery;
import com.example.bankcards.dto.TransferCommand;
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

import java.math.BigDecimal;


@RestController
@RequestMapping("/api/user/cards")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CardService cardService;


    @GetMapping
    public Page<CardQuery> getUserCards(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Sort sorting = Sort.by(Sort.Order.desc("balance"));
        Pageable pageable = PageRequest.of(page, size, sorting);

        if (search != null && !search.trim().isEmpty()) {
            return cardService.searchUserCards(userDetails, search, pageable);
        } else {
            return cardService.getUserCards(userDetails, pageable);
        }
    }

    @PutMapping("/transfer/from/{fromCardId}/to/{toCardId}")
    public void transfer(@AuthenticationPrincipal UserDetails userDetails,
                         @PathVariable Integer fromCardId,
                         @PathVariable Integer toCardId,
                         @RequestBody TransferCommand command){
        userService.transfer(userDetails, fromCardId, toCardId, command);
    }

    @GetMapping("/balance/{cardId}")
    public BigDecimal checkBalance(@AuthenticationPrincipal UserDetails userDetails,
                                   @PathVariable Integer cardId){
        return cardService.getBalance(userDetails, cardId);
    }

    @GetMapping("/{cardId}")
    public CardQuery getCardById(@AuthenticationPrincipal UserDetails userDetails,
                                 @PathVariable Integer cardId){
        return cardService.getById(userDetails, cardId);
    }
}
