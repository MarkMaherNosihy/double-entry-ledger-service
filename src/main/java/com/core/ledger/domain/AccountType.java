package com.core.ledger.domain;

public enum AccountType {
    ASSET,
    LIABILITY,
    REVENUE,
    EQUITY,
    EXPENSE;

    public Direction normalSide(){
        return switch (this) {
            case ASSET, EXPENSE -> Direction.DEBIT;
            case LIABILITY, REVENUE, EQUITY -> Direction.CREDIT;
        };
    }
}
