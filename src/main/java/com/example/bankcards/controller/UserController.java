package com.example.bankcards.controller;

import com.example.bankcards.dto.CardQuery;
import com.example.bankcards.dto.TransferCommand;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "User Operations", description = "Операции пользователя с картами")
@RestController
@RequestMapping("/api/user/cards")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CardService cardService;

    @Operation(
            summary = "Получить карты пользователя",
            description = "Возвращает страницу с картами текущего пользователя. " +
                    "Поддерживает поиск по номеру карты и пагинацию."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список карт успешно получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - требуется аутентификация")
    })
    @GetMapping
    public Page<CardQuery> getUserCards(
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "Номер страницы (начинается с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Количество элементов на странице", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Поисковый запрос по номеру карты", example = "1234")
            @RequestParam(required = false) String search) {

        Sort sorting = Sort.by(Sort.Order.desc("balance"));
        Pageable pageable = PageRequest.of(page, size, sorting);

        if (search != null && !search.trim().isEmpty()) {
            return cardService.searchUserCards(userDetails, search, pageable);
        } else {
            return cardService.getUserCards(userDetails, pageable);
        }
    }

    @Operation(
            summary = "Перевод между картами",
            description = "Выполняет перевод средств между картами одного пользователя. " +
                    "Обе карты должны принадлежать текущему пользователю и быть активными."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Перевод успешно выполнен"),
            @ApiResponse(responseCode = "400", description = "Недостаточно средств или ошибка валидации"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - карта не принадлежит пользователю")
    })
    @PutMapping("/transfer/from/{fromCardId}/to/{toCardId}")
    public void transfer(@AuthenticationPrincipal UserDetails userDetails,

                         @Parameter(description = "ID карты отправителя", example = "1")
                         @PathVariable Integer fromCardId,

                         @Parameter(description = "ID карты получателя", example = "2")
                         @PathVariable Integer toCardId,

                         @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Данные для перевода")
                         @RequestBody TransferCommand command){
        userService.transfer(userDetails, fromCardId, toCardId, command);
    }

    @Operation(
            summary = "Проверить баланс карты",
            description = "Возвращает текущий баланс указанной карты пользователя."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Баланс успешно получен"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - карта не принадлежит пользователю")
    })
    @GetMapping("/balance/{cardId}")
    public BigDecimal checkBalance(@AuthenticationPrincipal UserDetails userDetails,

                                   @Parameter(description = "ID карты для проверки баланса", example = "1")
                                   @PathVariable Integer cardId){
        return cardService.getBalance(userDetails, cardId);
    }

    @Operation(
            summary = "Получить информацию о карте",
            description = "Возвращает полную информацию о конкретной карте пользователя."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Информация о карте успешно получена"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - карта не принадлежит пользователю")
    })
    @GetMapping("/{cardId}")
    public CardQuery getCardById(@AuthenticationPrincipal UserDetails userDetails,

                                 @Parameter(description = "ID карты", example = "1")
                                 @PathVariable Integer cardId){
        return cardService.getById(userDetails, cardId);
    }
}
