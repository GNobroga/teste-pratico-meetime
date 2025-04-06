package com.github.gnobroga.spothook_api.controller.api.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.gnobroga.spothook_api.dto.response.ApiResponse;
import com.github.gnobroga.spothook_api.dto.response.AuthUrlResponseDTO;
import com.github.gnobroga.spothook_api.dto.response.TokenResponseDTO;
import com.github.gnobroga.spothook_api.service.OAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "HubSpot Authentication")
@RestController
@RequestMapping("/api/v1/auth/hubspot")
@RequiredArgsConstructor
public class HubSpotAuthAPIControllerV1{ 

    private final OAuthService oAuthService;
    
    @Operation(
        summary = "Gera a URL de autorização OAuth do HubSpot",
        description = """
            Retorna a URL que deve ser acessada para iniciar o processo de autenticação com o HubSpot (Authorization Code Flow).
            O usuário será redirecionado para o HubSpot, onde poderá conceder permissões ao aplicativo.
            Após a autorização, o HubSpot redirecionará para o redirect_uri configurado com um código de autorização.
            """,
        tags = { "HubSpot Authentication" },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "URL de autorização gerada com sucesso",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Erro ao gerar a URL de autorização",
                content = @Content(mediaType = "application/json")
            )
        }
    )
    @GetMapping("/authorize-url")
    public ResponseEntity<ApiResponse<AuthUrlResponseDTO>> getAuthorizationUrl() {
        return ResponseEntity.ok(new ApiResponse<AuthUrlResponseDTO>(oAuthService.generateAuthUrl()));
    }


    @Operation(
        summary = "Processa o callback OAuth",
        description = "Recebe o código de autorização e retorna o token de acesso",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Token de acesso gerado com sucesso",
                content = @Content(
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"status\":\"success\",\"data\":{\"access_token\":\"xyz\",\"refresh_token\":\"abc\",\"expires_in\":3600}}")
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "Acesso não autorizado"
            )
        },
        parameters = {
            @Parameter(
                name = "code",
                description = "Código de autorização recebido após o login",
                required = true,
                schema = @Schema(type = "string")
            )
        }
    )
    @GetMapping("/oauth-callback")
    public ResponseEntity<ApiResponse<TokenResponseDTO>> processOAuthCallback(@RequestParam String code) {
        return ResponseEntity.ok(new ApiResponse<>(oAuthService.exchangeAuthorizationCode(code)));
    }
}
