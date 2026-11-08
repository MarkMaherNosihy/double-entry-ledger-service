package com.core.ledger.service;

import com.core.ledger.domain.Direction;
import com.core.ledger.domain.entity.Account;
import com.core.ledger.domain.entity.JournalEntry;
import com.core.ledger.domain.entity.LedgerEntry;
import com.core.ledger.dtos.CreateJournalEntryRequest;
import com.core.ledger.dtos.JournalEntryLine;
import com.core.ledger.exceptions.UnbalancedJournalException;
import com.core.ledger.repository.AccountRepository;
import com.core.ledger.repository.JournalEntryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AccountRepository accountRepository;
    private final JournalEntryRepository journalEntryRepository;


    @Transactional
    public JournalEntry post(CreateJournalEntryRequest request){
        //Check Idempotency key if available return the entry associated with it.
        Optional<JournalEntry> existing = journalEntryRepository.getEntryWithIdemKey(request.idempotencyKey());
        if(existing.isPresent()){
            return existing.get();
        }

        //Check if the entry is balanced
        boolean balanced = isBalanced(request.entries());
        if(!balanced) throw new UnbalancedJournalException("Credits and Debits are not balanced");

        //Lock and get accounts to prevent race conditions
        Set<UUID> ids = request.entries().stream().map((l)-> l.accountId()).collect(Collectors.toSet());
        List<Account> accounts = accountRepository.lockAndGetAccountByIdIn(ids);
        Map<UUID, Account> accountMap = accounts.stream().collect(Collectors.toMap(account -> account.getId(), Function.identity()));
        if(accounts.size() != ids.size()){
            throw new IllegalArgumentException("Unknown account in journal");
        }

        //Check cross currency
        validateSingleCurrency(accounts);

        //Add Journal entry
        JournalEntry journal = new JournalEntry(request.idempotencyKey(),  request.description());
        for(JournalEntryLine entry : request.entries()){
            Account account = accountMap.get(entry.accountId());
            BigInteger newBalance = account.apply(entry.direction(), entry.amount());
            journal.addLine(account,entry.direction(), entry.amount(), newBalance);
        }
        return journalEntryRepository.save(journal);
    }


    public void validateSingleCurrency(List<Account> accounts){
        long distinct = accounts.stream().map(acc->acc.getCurrency()).distinct().count();
        if(distinct > 1 ){
            throw new IllegalArgumentException("Cross-currency journals are not supported");
        }
    }
    public boolean isBalanced(List<JournalEntryLine> lines){
        if (lines == null || lines.size() < 2) {
            throw new IllegalArgumentException("A journal needs at least 2 lines");
        }
        BigInteger debit = BigInteger.ZERO;
        BigInteger credit = BigInteger.ZERO;

        for(JournalEntryLine line : lines){
            if(line.amount().compareTo(BigInteger.ZERO) < 0){
                throw new IllegalArgumentException("Amounts must be positive");
            }
            if(line.direction() == Direction.CREDIT) {
                credit = credit.add(line.amount());
            }else{
                debit = debit.add(line.amount());
            }
        }
        return debit.compareTo(credit) == 0;
    }
}
