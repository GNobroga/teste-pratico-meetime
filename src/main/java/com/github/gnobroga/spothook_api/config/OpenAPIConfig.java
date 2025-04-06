package com.github.gnobroga.spothook_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenAPIConfig {

    public static final String SECURITY_SCHEME_NAME = "bearerAuth";
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(apiInfo())
            .components(new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME, bearerSecurityScheme())
            ).addSecurityItem(new SecurityRequirement()
                .addList(SECURITY_SCHEME_NAME)
            );
    }

    private Info apiInfo() {
        return new Info()
            .title("SpotHook API")  
            .description("API para integração com HubSpot, gerenciamento de vendas e automações.")  
            .version("1.0")
            .contact(new Contact()
                .name("Gabriel Cardoso Girarde")  
                .email("gabrielcardosogirarde@gmail.com")  
                .url("https://www.linkedin.com/in/gabriel-cardoso-girarde"))  
            .license(new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT"));
    }

    private SecurityScheme bearerSecurityScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization");
    }
}
