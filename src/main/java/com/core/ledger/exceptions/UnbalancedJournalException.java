package com.core.ledger.exceptions;

import java.math.BigInteger;

public class UnbalancedJournalException extends RuntimeException {
    public UnbalancedJournalException(String message) {
        super(message);
    }
}
