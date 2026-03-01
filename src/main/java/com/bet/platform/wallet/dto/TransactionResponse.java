package com.bet.platform.wallet.dto;

import java.math.BigDecimal;

public record TransactionResponse(
        String type,
        BigDecimal amount,
        String status,
        String origin,
        String createdAt
) {}