package com.applecraftserver.plugins.mrcore.economy;

public enum TransactionResult {
    ;

    public enum Deposit {
        SUCCESS("Funds deposited successfully.");

        Deposit (String msg) {

        }
    }

    public enum Pay {
        SUCCESS("Transferred successfully."),
        INSUFFICIENT_FUNDS("Insufficient funds in payer's account.");

        Pay (String msg) {

        }
    }

    public enum Withdraw {
        SUCCESS("Funds withdrawn successfully."),
        INSUFFICIENT_FUNDS("Insufficient funds in account.");

        Withdraw (String msg) {

        }
    }
}
