package com.github.gnobroga.spothook_api.controller.api.v1.webhook;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.gnobroga.spothook_api.dto.request.ContactCreationWebhookRequestDTO;
import com.github.gnobroga.spothook_api.service.integration.hubspot.ContactWebhookEventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "HubSpot Contact Webhook Listener")
@Slf4j
@RestController
@RequestMapping("/api/v1/webhook/hubspot")
@RequiredArgsConstructor
public class HubSpotContactWebhookControllerV1 {

    private final ContactWebhookEventService contactWebhookEventService;

    @Operation(
        summary = "Recebe evento de criação de contato (webhook)",
        description = """
            Endpoint utilizado pelo HubSpot para envio de eventos de criação de contatos via webhook.
            Cada evento é persistido no banco de dados para posterior consulta e análise.
            Esse endpoint espera uma lista de eventos no corpo da requisição.
            """,
        tags = { "HubSpot Contact Webhook" },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Lista de eventos de criação de contato enviados pelo HubSpot",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ContactCreationWebhookRequestDTO.class))
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "Eventos processados com sucesso. Nenhum conteúdo retornado."
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Requisição malformada ou dados inválidos",
                content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Erro interno ao processar os eventos",
                content = @Content(mediaType = "application/json")
            )
        }
    )
    @PostMapping("/contacts/created")
    public ResponseEntity<Void> handleContactCreatedEvent(@RequestBody String rawEventsJson, HttpServletRequest request) {
        log.info("Received contact created webhook payload: {}", rawEventsJson);
        contactWebhookEventService.storeContactWebhookEventsFromRawJson(rawEventsJson, request);
        return ResponseEntity.noContent().build(); 
    }
}
