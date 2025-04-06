package com.github.gnobroga.spothook_api.dto.request;

public record ContactCreationWebhookRequestDTO(
    Long appId,
    Long eventId,
    Long subscriptionId,
    Long portalId,
    Long occurredAt,
    String subscriptionType,
    Integer attemptNumber,
    Long objectId,
    String changeSource,
    String changeFlag
) {}