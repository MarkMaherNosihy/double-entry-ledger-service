CREATE TABLE accounts (
    id UUID PRIMARY KEY ,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(16) NOT NULL CHECK(type IN ('ASSET', 'LIABILITY', 'EQUITY', 'REVENUE', 'EXPENSE')),
    currency VARCHAR(3) NOT NULL,
    balance NUMERIC(38, 0) NOT NULL DEFAULT 0,
    allow_negative BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE journal_entries(
    id UUID PRIMARY KEY ,
    idempotency_key VARCHAR(128) NOT NULL UNIQUE,
    description VARCHAR(500),
    reversal_of UUID REFERENCES journal_entries(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE ledger_entries(
    id UUID PRIMARY KEY ,
    journal_entry_id UUID NOT NULL REFERENCES journal_entries(id),
    account_id UUID NOT NULL REFERENCES accounts(id),
    direction VARCHAR(6) NOT NULL CHECK( direction IN ('DEBIT', 'CREDIT')),
    amount NUMERIC(38, 0) NOT NULL CHECK (amount > 0),
    balance_after NUMERIC(38, 0) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);