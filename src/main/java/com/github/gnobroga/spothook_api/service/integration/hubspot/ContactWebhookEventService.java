package com.github.gnobroga.spothook_api.service.integration.hubspot;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gnobroga.spothook_api.config.HubSpotAuthConfig;
import com.github.gnobroga.spothook_api.config.HubSpotContactConfig;
import com.github.gnobroga.spothook_api.dto.request.ContactCreationWebhookRequestDTO;
import com.github.gnobroga.spothook_api.model.entity.ContactWebhookEventEntity;
import com.github.gnobroga.spothook_api.repository.ContactWebhookEventRepository;
import com.github.gnobroga.spothook_api.service.exception.ApiException;
import com.github.gnobroga.spothook_api.service.exception.ApiExceptionType;
import com.github.gnobroga.spothook_api.util.HashUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Data
public class ContactWebhookEventService {

    private final static String SIGNATURE_HEADER = "X-Hubspot-Signature";

    private final ContactWebhookEventRepository contactWebhookEventRepository;

    private final HubSpotAuthConfig hubSpotAuthConfig;

    private final HubSpotContactConfig hubSpotContactConfig;

    private final ObjectMapper objectMapper;

    @Transactional
    public void storeContactWebhookEventsFromRawJson(String rawEventsJson, HttpServletRequest request) {
        
        var requestSignature = request.getHeader(SIGNATURE_HEADER);
        
        log.info("Processing HubSpot contact webhook. Signature header: {}", requestSignature);

        if (requestSignature == null) {
                throw new ApiException(ApiExceptionType.SIGNATURE_HEADER_MISSING, SIGNATURE_HEADER);
        }


        if (!isValidHubspotSignature(requestSignature, hubSpotAuthConfig.getClientSecret(), rawEventsJson)) {
            throw new ApiException(ApiExceptionType.INVALID_SIGNATURE, SIGNATURE_HEADER);
        }

        ContactCreationWebhookRequestDTO[] events = {};

        try {
            events = objectMapper.readValue(rawEventsJson, ContactCreationWebhookRequestDTO[].class);
        } catch (JsonProcessingException ex) {
            log.warn("Ignoring webhook: invalid JSON payload", ex);
            return;
        }

        if (events.length == 0) {
            log.info("No contact webhook events to store.");
            return;
        }

        contactWebhookEventRepository.saveAll(
            Arrays.stream(events)
            .map(ContactWebhookEventEntity::new)
            .toList()
        );

        log.info("Stored {} contact webhook events successfully.", events.length);
    }

    private boolean isValidHubspotSignature(String requestSignature, String clientSecret, String requestBody) {
        var expectedSignature = HashUtils.sha256Hex((clientSecret + requestBody).strip());
        return expectedSignature.equals(requestSignature);
    }

    public List<ContactWebhookEventEntity> getAllContactWebhookEvents() {
        return contactWebhookEventRepository.findAll();
    }
}