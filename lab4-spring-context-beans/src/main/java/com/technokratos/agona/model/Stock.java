package com.technokratos.agona.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record Stock(UUID stockUuid, String isin, String ticker, BigDecimal averagePrice) {
}
