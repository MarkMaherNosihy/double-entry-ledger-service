package com.core.ledger.exceptions;

public class InsufficentFundsException extends RuntimeException {
    public InsufficentFundsException(String message) {
        super(message);
    }
}
