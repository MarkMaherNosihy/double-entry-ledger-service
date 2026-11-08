package com.core.ledger.domain.entity;

import com.core.ledger.domain.Direction;
import jakarta.persistence.*;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {
    @Id
    private UUID id = UUID.randomUUID();
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "journal_entry_id")
    private JournalEntry journalEntry;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id")
    private Account account;
    @Enumerated(EnumType.STRING)
    private Direction direction;
    private BigInteger amount;
    private BigInteger balanceAfter;
    private Instant createdAt = Instant.now();
    protected LedgerEntry() {}
    public LedgerEntry(JournalEntry journal, Account account,
                       Direction direction, BigInteger amount, BigInteger balanceAfter) {
        this.journalEntry = journal;
        this.account = account;
        this.direction = direction;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }
}
