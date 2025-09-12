package com.example.bankcards.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Server")
                ))
                .info(new Info()
                        .title("Bank Cards Management API")
                        .description("""
                        ## Система управления банковскими картами
                        
                        ### Возможности:
                        - **Администратор**: Управление картами и пользователями
                        - **Пользователь**: Просмотр карт, переводы, запросы операций
                        """)
                        .version("1.0.0")
                );
    }
}
