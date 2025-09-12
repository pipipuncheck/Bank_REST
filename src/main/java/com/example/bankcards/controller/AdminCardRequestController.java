package com.example.bankcards.controller;

import com.example.bankcards.dto.CardRequestQuery;
import com.example.bankcards.service.CardRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin Request Management", description = "Управление запросами пользователей (только для ADMIN)")
@RestController
@RequestMapping("/api/admin/requests")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminCardRequestController {

    private final CardRequestService cardRequestService;

    @Operation(
            summary = "Получить ожидающие запросы",
            description = "Возвращает страницу с запросами пользователей, ожидающими обработки."
    )
    @ApiResponse(responseCode = "200", description = "Список запросов успешно получен")
    @GetMapping
    public Page<CardRequestQuery> getPendingRequests(Pageable pageable) {
        return cardRequestService.getPendingRequests(pageable);
    }

    @Operation(
            summary = "Подтвердить запрос",
            description = "Подтверждает и выполняет запрос пользователя на операцию с картой."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запрос успешно подтвержден и выполнен"),
            @ApiResponse(responseCode = "404", description = "Запрос не найден"),
            @ApiResponse(responseCode = "400", description = "Запрос уже обработан или недействителен")
    })
    @PutMapping("/{requestId}/approve")
    public void approveRequest(
            @Parameter(description = "ID запроса для подтверждения", example = "1")
            @PathVariable Integer requestId) {
        cardRequestService.approveRequest(requestId);
    }

    @Operation(
            summary = "Отклонить запрос",
            description = "Отклоняет запрос пользователя на операцию с картой."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запрос успешно отклонен"),
            @ApiResponse(responseCode = "404", description = "Запрос не найден")
    })
    @PutMapping("/{requestId}/reject")
    public void rejectRequest(
            @Parameter(description = "ID запроса для отклонения", example = "1")
            @PathVariable Integer requestId) {
        cardRequestService.rejectRequest(requestId);
    }
}