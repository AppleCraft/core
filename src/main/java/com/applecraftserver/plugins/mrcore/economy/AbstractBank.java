package com.applecraftserver.plugins.mrcore.economy;

import com.applecraftserver.plugins.mrcore.user.User;

import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;

public abstract class AbstractBank {
    private final Random random;
    private final UUID accountID;
    private BigInteger funds = BigInteger.ZERO;

    protected AbstractBank() {
        random = new Random(System.currentTimeMillis());
        accountID = new UUID(random.nextLong(), random.nextLong());
    }

    public UUID getID() {
        return this.accountID;
    }

    public TransactionResult.Deposit deposit(BigInteger cash) {
        this.funds = this.funds.add(cash);
        return TransactionResult.Deposit.SUCCESS;
    }

    public TransactionResult.Pay pay(final User payee, final BigInteger cash) {
        if (withdraw(cash) == TransactionResult.Withdraw.SUCCESS) {
            payee.getBankAccount().deposit(cash);
            return TransactionResult.Pay.SUCCESS;
        } else {
            return TransactionResult.Pay.INSUFFICIENT_FUNDS;
        }
    }

    public TransactionResult.Withdraw withdraw(BigInteger cash) {
        if (this.funds.compareTo(cash) >= 1) {
            this.funds = this.funds.subtract(cash);
            return TransactionResult.Withdraw.SUCCESS;
        } else {
            return TransactionResult.Withdraw.INSUFFICIENT_FUNDS;
        }
    }

    public void setFunds(BigInteger funds) {
        this.funds = funds;
    }

}
