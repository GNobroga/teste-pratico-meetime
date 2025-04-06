package com.github.gnobroga.spothook_api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.gnobroga.spothook_api.model.entity.ContactWebhookEventEntity;

@Repository
public interface ContactWebhookEventRepository extends JpaRepository<ContactWebhookEventEntity, UUID> {}
