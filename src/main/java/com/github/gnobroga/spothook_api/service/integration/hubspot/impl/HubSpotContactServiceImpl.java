package com.github.gnobroga.spothook_api.service.integration.hubspot.impl;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.gnobroga.spothook_api.config.HubSpotContactConfig;
import com.github.gnobroga.spothook_api.dto.request.CreateHubspotContactRequestDTO;
import com.github.gnobroga.spothook_api.service.ContactService;
import com.github.gnobroga.spothook_api.service.exception.ApiException;
import com.github.gnobroga.spothook_api.service.exception.ApiExceptionType;
import com.github.gnobroga.spothook_api.service.integration.hubspot.http.HubSpotHttpClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubSpotContactServiceImpl implements ContactService<CreateHubspotContactRequestDTO> {
    
    private final HubSpotContactConfig hubSpotContactConfig;

    private final HubSpotHttpClient hubSpotHttpClient;

    private final ObjectMapper objectMapper;

    @Override
    public JsonNode createContact(CreateHubspotContactRequestDTO record, String accessToken) {
        if (Objects.isNull(accessToken)) {
            log.warn("Attempted to create contact without an access token");
            throw new ApiException(ApiExceptionType.MISSING_ACCESS_TOKEN);
        }

        if (!StringUtils.hasText(record.getEmail())) {
            log.warn("Attempted to create contact without required field: email");
            throw new ApiException(ApiExceptionType.MISSING_REQUIRED_FIELD_TEMPLATE, "email");
        }

        log.info("Starting HubSpot contact creation for email: {}", record.getEmail());

        ObjectNode objectNode = buildObjectNodeForCreateContact(record);

        JsonNode response = hubSpotHttpClient.postWithAccessToken(
            hubSpotContactConfig.getApiUrl(),
            accessToken,
            objectNode,
            JsonNode.class
        );

        log.info("Successfully created contact in HubSpot");

        return response;
    }

    private ObjectNode buildObjectNodeForCreateContact(CreateHubspotContactRequestDTO record) {
        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode arrayProperties = objectMapper.createArrayNode();
        rootNode.set("properties", arrayProperties);
        arrayProperties.add(createPropertyNode("email", record.getEmail()));
        arrayProperties.add(createPropertyNode("firstName", record.getFirstName()));
        arrayProperties.add(createPropertyNode("lastName", record.getLastName()));
        arrayProperties.add(createPropertyNode("phone", record.getPhone()));
        arrayProperties.add(createPropertyNode("company", record.getCompany()));
        arrayProperties.add(createPropertyNode("website", record.getWebsite()));
        return rootNode;
    }

    private JsonNode createPropertyNode(String property, String value) {
        ObjectNode propertyNode = objectMapper.createObjectNode();
        propertyNode.put("property", property);
        propertyNode.put("value", value);
        return propertyNode;
    }
}
