package com.rbkmoney.fistful.magista.util;

import com.rbkmoney.fistful.cashflow.FinalCashFlowAccount;
import com.rbkmoney.fistful.cashflow.FinalCashFlowPosting;
import com.rbkmoney.fistful.cashflow.MerchantCashFlowAccount;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class CashFlowUtil {

    public static long getFistfulProviderFee(List<com.rbkmoney.fistful.cashflow.FinalCashFlowPosting> postings) {
        return getFistfulAmount(
                postings,
                posting -> posting.getSource().getAccountType().isSetSystem()
                        && posting.getDestination().getAccountType().isSetProvider()
        );
    }

    public static long getFistfulFee(List<com.rbkmoney.fistful.cashflow.FinalCashFlowPosting> postings) {
        return getFistfulAmount(
                postings,
                posting -> posting.getSource().getAccountType().isSetWallet()
                        && posting.getDestination().getAccountType().isSetSystem()
        );
    }

    public static long getFistfulAmount(
            List<com.rbkmoney.fistful.cashflow.FinalCashFlowPosting> postings,
            Predicate<com.rbkmoney.fistful.cashflow.FinalCashFlowPosting> filter
    ) {
        return postings.stream()
                .filter(filter)
                .map(posting -> posting.getVolume().getAmount())
                .reduce(0L, Long::sum);
    }

    public static Long computeAmount(List<FinalCashFlowPosting> finalCashFlow) {
        long amountSource = computeAmount(finalCashFlow, FinalCashFlowPosting::getSource);
        long amountDest = computeAmount(finalCashFlow, FinalCashFlowPosting::getDestination);
        return amountDest - amountSource;
    }

    private static long computeAmount(List<FinalCashFlowPosting> finalCashFlow,
                                      Function<FinalCashFlowPosting, FinalCashFlowAccount> func) {
        return finalCashFlow.stream()
                .filter(f -> isMerchantSettlement(func.apply(f).getAccountType()))
                .mapToLong(cashFlow -> cashFlow.getVolume().getAmount())
                .sum();
    }

    private static boolean isMerchantSettlement(com.rbkmoney.fistful.cashflow.CashFlowAccount cashFlowAccount) {
        return cashFlowAccount.isSetMerchant() &&
                cashFlowAccount.getMerchant() == MerchantCashFlowAccount.settlement;
    }
}
