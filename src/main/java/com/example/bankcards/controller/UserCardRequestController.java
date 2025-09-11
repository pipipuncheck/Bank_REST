package com.example.bankcards.controller;

import com.example.bankcards.entity.RequestType;
import com.example.bankcards.service.CardRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/card-requests")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class UserCardRequestController {

    private final CardRequestService cardRequestService;

    @PostMapping("/block/{cardId}")
    public void requestBlockCard(@AuthenticationPrincipal UserDetails userDetails,
                                 @PathVariable Integer cardId) {
        cardRequestService.createRequest(userDetails, cardId, RequestType.BLOCK);
    }

    @PostMapping("/unblock/{cardId}")
    public void requestUnblockCard(@AuthenticationPrincipal UserDetails userDetails,
                                   @PathVariable Integer cardId) {
        cardRequestService.createRequest(userDetails, cardId, RequestType.UNBLOCK);
    }

    @PostMapping("/delete/{cardId}")
    public void requestDeleteCard(@AuthenticationPrincipal UserDetails userDetails,
                                  @PathVariable Integer cardId) {
        cardRequestService.createRequest(userDetails, cardId, RequestType.DELETE);
    }

    @PostMapping("/create")
    public void requestCreateCard(@AuthenticationPrincipal UserDetails userDetails) {
        cardRequestService.createRequest(userDetails, null, RequestType.CREATE);
    }
}
