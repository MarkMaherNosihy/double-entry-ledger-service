package com.core.ledger.repository;

import com.core.ledger.domain.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {

    @Query("select a from JournalEntry a where a.idempotencyKey = :idempotencyKey" )
    Optional<JournalEntry> getEntryWithIdemKey(@Param("idempotencyKey") String idempotencyKey);
}
