package com.core.ledger.dtos;

import com.core.ledger.domain.Direction;

import java.math.BigInteger;
import java.util.UUID;

public record JournalEntryLine(UUID accountId, BigInteger amount, Direction direction) {
}
