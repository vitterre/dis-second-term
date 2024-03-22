package com.technokratos.agona.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record Account(UUID accountUuid, String firstName, String lastName) {
}
