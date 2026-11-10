package com.core.ledger.controller;


import com.core.ledger.domain.Direction;
import com.core.ledger.domain.entity.JournalEntry;
import com.core.ledger.dtos.*;
import com.core.ledger.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class TransactionsController {

    final private TransactionService transactionService;

    @PostMapping("/transfers")
    public ResponseEntity<TransferResponse> transfer(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                                     @Valid @RequestBody TransferRequest req){
        //T
        CreateJournalEntryRequest journalReq = new CreateJournalEntryRequest(idempotencyKey, req.description(),
                List.of(
                        new JournalEntryLine(req.fromAccountId(), req.amount(), Direction.DEBIT),
                        new JournalEntryLine(req.toAccountId(), req.amount(), Direction.CREDIT)
                ));
        JournalEntry entry = transactionService.post(journalReq);
        TransferResponse response = new TransferResponse(entry.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reverse")
    public ResponseEntity<TransferResponse> reverse(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                                     @Valid @RequestBody ReverseRequest req){

        JournalEntry entry = transactionService.reverse( req.journalId(), idempotencyKey);
        TransferResponse response = new TransferResponse(entry.getId());
        return ResponseEntity.ok(response);
    }




}
