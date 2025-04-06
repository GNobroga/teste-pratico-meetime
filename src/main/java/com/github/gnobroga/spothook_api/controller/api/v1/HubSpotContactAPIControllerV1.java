package com.github.gnobroga.spothook_api.controller.api.v1;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.gnobroga.spothook_api.dto.request.CreateHubspotContactRequestDTO;
import com.github.gnobroga.spothook_api.dto.response.ApiResponse;
import com.github.gnobroga.spothook_api.model.entity.ContactWebhookEventEntity;
import com.github.gnobroga.spothook_api.service.ContactService;
import com.github.gnobroga.spothook_api.service.integration.hubspot.ContactWebhookEventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "HubSpot Contacts")
@RequestMapping("/api/v1/contact/hubspot")
@RestController
@RequiredArgsConstructor
public class HubSpotContactAPIControllerV1 {

    private final ContactService<CreateHubspotContactRequestDTO> contactService;

    private final ContactWebhookEventService contactWebhookEventService;
    

    @Operation(
        summary = "Cria um novo contato no HubSpot",
        description = "Este endpoint cria um novo contato no HubSpot, recebendo os dados do contato e o token de acesso.",
        security = @SecurityRequirement(name = "bearerAuth"), 
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Contato criado com sucesso",
                content = @Content(
                    schema = @Schema(implementation = ApiResponse.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Requisição inválida, dados do contato incorretos"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "Token de acesso inválido ou expirado"
            )
        }
    )
    @PostMapping("/create-contact")
    public ResponseEntity<ApiResponse<JsonNode>> createContact(@RequestBody @Valid CreateHubspotContactRequestDTO createHubspotContactRequestDTO, @RequestAttribute String accessToken) {
        var result = contactService.createContact(createHubspotContactRequestDTO, accessToken);
        return ResponseEntity.ok(new ApiResponse<>(result));
    }


    @Operation(
        summary = "Lista eventos de criação de contato (webhooks)",
        description = """
            Retorna todos os eventos de criação de contatos recebidos via webhook do HubSpot.
            Os eventos são armazenados em banco e podem incluir informações como ID do contato, data do evento, tipo de assinatura, entre outros.
            """,
        tags = { "HubSpot Contact Webhook" },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Lista de eventos retornada com sucesso",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Erro interno ao buscar os eventos",
                content = @Content(mediaType = "application/json")
            )
        }
    )
    @GetMapping("/events/contact-created")
    public ResponseEntity<ApiResponse<List<ContactWebhookEventEntity>>> getAllContactCreatedEvents() {
        List<ContactWebhookEventEntity> events = contactWebhookEventService.getAllContactWebhookEvents();
        return ResponseEntity.ok(new ApiResponse<>(events));
    }
}
