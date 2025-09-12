package com.example.bankcards.controller;

import com.example.bankcards.dto.auth.AuthCommand;
import com.example.bankcards.dto.auth.AuthQuery;
import com.example.bankcards.dto.auth.JwtQuery;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.AuthService;
import com.example.bankcards.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Authentication",
        description = "Регистрация и аутентификация пользователей. Все endpoints публичные и не требуют JWT токена."
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создает нового пользователя в системе и возвращает JWT токен для автоматической аутентификации."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная регистрация. Возвращает JWT токен.",
                    content = @Content(
                            schema = @Schema(implementation = JwtQuery.class),
                            examples = @ExampleObject(
                                    value = "{\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные данные запроса: username уже занят, пароль слишком простой и т.д."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Данные для регистрации нового пользователя",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = AuthCommand.class),
                    examples = @ExampleObject(
                            value = """
                {
                  "username": "ivanov",
                  "password": "securePassword123",
                  "email": "ivanov@example.com",
                  "firstName": "Иван",
                  "lastName": "Иванов"
                }
                """
                    )
            )
    )
    @PostMapping("/signup")
    public JwtQuery registration(@RequestBody AuthCommand command){
        User user = authService.register(command);
        return new JwtQuery(jwtService.generateToken(user));
    }

    @Operation(
            summary = "Аутентификация пользователя",
            description = "Проверяет учетные данные пользователя и возвращает JWT токен для доступа к защищенным endpoints."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная аутентификация. Возвращает JWT токен.",
                    content = @Content(
                            schema = @Schema(implementation = JwtQuery.class),
                            examples = @ExampleObject(
                                    value = "{\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Неверные учетные данные: неправильный username или password"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Учетные данные для входа",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = AuthQuery.class),
                    examples = @ExampleObject(
                            value = """
                {
                  "username": "ivanov",
                  "password": "securePassword123"
                }
                """
                    )
            )
    )
    @PostMapping("/signin")
    public JwtQuery login(@RequestBody AuthQuery authQuery){
        User user = authService.authenticate(authQuery.getUsername(), authQuery.getPassword());
        return new JwtQuery(jwtService.generateToken(user));
    }
}
