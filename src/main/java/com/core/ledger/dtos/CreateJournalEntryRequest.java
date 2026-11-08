package com.core.ledger.dtos;

import java.util.List;

public record CreateJournalEntryRequest(String idempotencyKey, String description, List<JournalEntryLine> entries) {
}
