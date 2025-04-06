package com.github.gnobroga.spothook_api.model.entity;

import java.util.UUID;

import com.github.gnobroga.spothook_api.dto.request.ContactCreationWebhookRequestDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contact_webhook_event")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContactWebhookEventEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Long eventId;
    private Long subscriptionId;
    private Long portalId;
    private Long appId;
    private Long occurredAt;
    private String subscriptionType;
    private Integer attemptNumber;
    private Long objectId;
    private String changeFlag;
    private String changeSource;

    public ContactWebhookEventEntity(ContactCreationWebhookRequestDTO event) {
        this.eventId = event.eventId();
        this.subscriptionId = event.subscriptionId();
        this.portalId = event.portalId();
        this.appId = event.appId();
        this.occurredAt = event.occurredAt();
        this.subscriptionType = event.subscriptionType();
        this.attemptNumber = event.attemptNumber();
        this.objectId = event.objectId();
        this.changeFlag = event.changeFlag();
        this.changeSource = event.changeSource();
    }
    
}
