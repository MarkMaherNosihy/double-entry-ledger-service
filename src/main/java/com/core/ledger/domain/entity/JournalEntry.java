package com.core.ledger.domain.entity;

import com.core.ledger.domain.Direction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "journal_entries")
public class JournalEntry {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;
    private String description;
    @Setter
    @Column(name = "reversal_of")
    private UUID reversalOf;
    private Instant createdAt = Instant.now();
    @Getter
    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL)
    private List<LedgerEntry> lines = new ArrayList<>();
    protected JournalEntry() {}
    public JournalEntry(String idempotencyKey, String description) {
        this.idempotencyKey = idempotencyKey;
        this.description = description;
    }
    public void addLine(Account account, Direction direction, BigInteger amount, BigInteger balanceAfter) {
        lines.add(new LedgerEntry(this, account, direction, amount, balanceAfter));
    }
    public void markReversalOf(UUID id){
        this.reversalOf = id;
    }


}
