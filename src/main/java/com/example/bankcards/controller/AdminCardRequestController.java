package com.example.bankcards.controller;

import com.example.bankcards.dto.CardRequestQuery;
import com.example.bankcards.service.CardRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/requests")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminCardRequestController {

    private final CardRequestService cardRequestService;

    @GetMapping
    public Page<CardRequestQuery> getPendingRequests(Pageable pageable) {
        return cardRequestService.getPendingRequests(pageable);
    }

    @PutMapping("/{requestId}/approve")
    public void approveRequest(@PathVariable Integer requestId) {
        cardRequestService.approveRequest(requestId);
    }

    @PutMapping("/{requestId}/reject")
    public void rejectRequest(@PathVariable Integer requestId) {
        cardRequestService.rejectRequest(requestId);
    }
}