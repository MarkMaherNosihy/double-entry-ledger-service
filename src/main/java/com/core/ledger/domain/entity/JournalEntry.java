package com.core.ledger.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

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
    @Column(name = "reversal_of")
    private UUID reversalOf;
    private Instant createdAt = Instant.now();
    @Getter
    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL)
    private List<LedgerEntry> lines = new ArrayList<>();
    protected JournalEntry() {}
    public JournalEntry(String idempotencyKey, String description) {
        this.id = UUID.randomUUID();
        this.idempotencyKey = idempotencyKey;
        this.description = description;
    }
    public void addLine(LedgerEntry line) { lines.add(line); }

}
