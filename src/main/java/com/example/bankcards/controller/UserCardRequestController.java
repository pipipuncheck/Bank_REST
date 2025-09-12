package com.example.bankcards.controller;

import com.example.bankcards.entity.RequestType;
import com.example.bankcards.service.CardRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Card Requests", description = "Запросы пользователя на операции с картами")
@RestController
@RequestMapping("/api/user/card-requests")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class UserCardRequestController {

    private final CardRequestService cardRequestService;

    @Operation(
            summary = "Запрос на блокировку карты",
            description = "Создает запрос на блокировку карты. Требует подтверждения администратором."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запрос на блокировку создан"),
            @ApiResponse(responseCode = "400", description = "Карта уже заблокирована или недействительна"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - карта не принадлежит пользователю")
    })
    @PostMapping("/block/{cardId}")
    public void requestBlockCard(@AuthenticationPrincipal UserDetails userDetails,

                                 @Parameter(description = "ID карты для блокировки", example = "1")
                                 @PathVariable Integer cardId) {
        cardRequestService.createRequest(userDetails, cardId, RequestType.BLOCK);
    }

    @Operation(
            summary = "Запрос на разблокировку карты",
            description = "Создает запрос на разблокировку карты. Требует подтверждения администратором."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запрос на разблокировку создан"),
            @ApiResponse(responseCode = "400", description = "Карта не заблокирована или недействительна"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - карта не принадлежит пользователю")
    })
    @PostMapping("/unblock/{cardId}")
    public void requestUnblockCard(@AuthenticationPrincipal UserDetails userDetails,

                                   @Parameter(description = "ID карты для разблокировки", example = "1")
                                   @PathVariable Integer cardId) {
        cardRequestService.createRequest(userDetails, cardId, RequestType.UNBLOCK);
    }

    @Operation(
            summary = "Запрос на удаление карты",
            description = "Создает запрос на удаление карты. Требует подтверждения администратором."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запрос на удаление создан"),
            @ApiResponse(responseCode = "400", description = "Карта недействительна для удаления"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - карта не принадлежит пользователю")
    })
    @PostMapping("/delete/{cardId}")
    public void requestDeleteCard(@AuthenticationPrincipal UserDetails userDetails,

                                  @Parameter(description = "ID карты для удаления", example = "1")
                                  @PathVariable Integer cardId) {
        cardRequestService.createRequest(userDetails, cardId, RequestType.DELETE);
    }

    @Operation(
            summary = "Запрос на создание карты",
            description = "Создает запрос на создание новой карты. Требует подтверждения администратором."
    )
    @ApiResponse(responseCode = "200", description = "Запрос на создание карты создан")
    @PostMapping("/create")
    public void requestCreateCard(@AuthenticationPrincipal UserDetails userDetails) {
        cardRequestService.createRequest(userDetails, null, RequestType.CREATE);
    }
}
