package com.core.ledger.dtos;

import java.util.UUID;

public record ReverseRequest(
        UUID journalId
) {
}
