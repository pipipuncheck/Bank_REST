package com.example.bankcards.controller;

import com.example.bankcards.dto.CardQuery;
import com.example.bankcards.dto.UserQuery;
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

import java.util.List;

@Tag(name = "Admin Management", description = "Управление пользователями и картами (только для ADMIN)")
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final CardService cardService;

    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает список всех зарегистрированных пользователей системы"
    )
    @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен")
    @GetMapping("/users")
    public List<UserQuery> getAllUsers(){
        return userService.getAll();
    }

    @Operation(
            summary = "Получить все карты с пагинацией",
            description = "Возвращает страницу со всеми картами в системе, отсортированными по балансу (по убыванию)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список карт успешно получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - требуется роль ADMIN")
    })
    @GetMapping("/cards")
    public Page<CardQuery> getAllCards(
            @Parameter(description = "Номер страницы (начинается с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Количество элементов на странице", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Sort sorting = Sort.by(Sort.Order.desc("balance"));
        Pageable pageable = PageRequest.of(page, size, sorting);

        return cardService.getAllCards(pageable);
    }

    @Operation(
            summary = "Создать новую карту",
            description = "Создает новую банковскую карту для текущего пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Карта успешно создана"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - требуется роль ADMIN")
    })
    @PostMapping("/cards/create")
    public void createCard(@AuthenticationPrincipal UserDetails userDetails) {
        cardService.createCard(userDetails);
    }

    @Operation(
            summary = "Заблокировать карту",
            description = "Блокирует указанную карту. Карта становится недоступной для операций."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Карта успешно заблокирована"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - требуется роль ADMIN")
    })
    @PutMapping("/cards/{cardId}/block")
    public void blockCard(@AuthenticationPrincipal UserDetails userDetails,

                          @Parameter(description = "ID карты для блокировки", example = "1")
                          @PathVariable Integer cardId) {
        cardService.blockCard(userDetails, cardId);
    }

    @Operation(
            summary = "Активировать карту",
            description = "Активирует ранее заблокированную карту. Карта снова становится доступной для операций."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Карта успешно активирована"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - требуется роль ADMIN")
    })
    @PutMapping("/cards/{cardId}/activate")
    public void activateCard(@AuthenticationPrincipal UserDetails userDetails,

                             @Parameter(description = "ID карты для активации", example = "1")
                             @PathVariable Integer cardId) {
        cardService.activateCard(userDetails, cardId);
    }

    @Operation(
            summary = "Удалить карту",
            description = "Полностью удаляет карту из системы. Операция необратима."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Карта успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - требуется роль ADMIN")
    })
    @DeleteMapping("/cards/{cardId}/delete")
    public void deleteCard(@AuthenticationPrincipal UserDetails userDetails,

                           @Parameter(description = "ID карты для удаления", example = "1")
                           @PathVariable Integer cardId) {
        cardService.deleteCard(userDetails, cardId);
    }

    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя из системы вместе со всеми его картами."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь успешно удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - требуется роль ADMIN")
    })
    @PutMapping("/users/{userId}/delete")
    public void deleteUser(
            @Parameter(description = "ID пользователя для удаления", example = "1")
            @PathVariable Integer userId) {
        userService.deleteUser(userId);
    }
}
