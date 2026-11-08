package com.core.ledger.domain.entity;

import com.core.ledger.domain.AccountType;
import com.core.ledger.domain.Direction;
import com.core.ledger.exceptions.InsufficentFundsException;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String code;
    private String name;
    @Enumerated(EnumType.STRING)
    private AccountType type;
    @Column(length = 3)
    private String currency;
    private BigInteger balance = BigInteger.ZERO;
    private boolean allowNegative;
    @Getter
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<LedgerEntry> lines = new ArrayList<>();

    protected Account() {}
    public Account(String code, String name, AccountType type, String currency, boolean allowNegative) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.name = name;
        this.type = type;
        this.currency = currency;
        this.allowNegative = allowNegative;
    }
    /** Applies a debit/credit and returns the new balance. */
    public BigInteger apply(Direction direction, BigInteger amount) {
        BigInteger delta = (direction == type.normalSide()) ? amount : amount.negate();
        BigInteger newBalance = balance.add(delta);
        if (newBalance.compareTo(BigInteger.ZERO) < 0 && !allowNegative) {
            throw new InsufficentFundsException(code);
        }
        this.balance = newBalance;
        return newBalance;
    }
}
