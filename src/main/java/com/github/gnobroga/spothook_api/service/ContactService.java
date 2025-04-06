package com.github.gnobroga.spothook_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.gnobroga.spothook_api.model.ContactPayload;

public interface ContactService<T extends ContactPayload> {
   JsonNode createContact(T payload, String accessToken);
}
