package com.wcms.core.event;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

    UUID eventId();

    String eventType();

    Instant occurredAt();
}
