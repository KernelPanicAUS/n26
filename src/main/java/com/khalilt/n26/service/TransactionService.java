package com.khalilt.n26.service;

import com.khalilt.n26.model.Transaction;
import com.khalilt.n26.store.TransactionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class TransactionService {

    @Autowired
    private TransactionStore transactionStore;

    public void createTxn(Long txnId, Map payload) {

        if (!payload.containsKey("amount")) { throw new RuntimeException("Amount was not specified"); }
        if (!payload.containsKey("type")) { throw new RuntimeException("Type was not specified"); }

        String type = (String) payload.get("type");
        Long amount = new Long((Integer) payload.get("amount"));
        Long parent_id = null;

        if (payload.containsKey("parent_id")) {
            parent_id = new Long((Integer) payload.get("parent_id"));
        }

        Transaction txn = new Transaction(type, amount, parent_id, txnId);

        transactionStore.push(txn);
    }

    public Transaction fetchTransaction(Long txnId) {
        return this.transactionStore.getTransactionById(txnId);
    }

    public Long getSumForTransactionId(Long txnId) {

        Transaction txn = this.transactionStore.getTransactionById(txnId);
        Long sum = txn.getAmount();

        if (this.transactionStore.doesParentIdHaveChildren(txn.getId())) {

            List<Long> children = this.transactionStore.getChildIdsForParentId(txn.getId());

            for (Long child: children) {
                sum += this.fetchTransaction(child).getAmount();
            }
        }

        return sum;
    }

    public List<Long> fetchTxnIdsByType(String typeString) {

        List<Long> result = this.transactionStore.getTransactionsByType(typeString);

        if (result.isEmpty()) {
            return Collections.emptyList();
        } else {
            return result;
        }
    }
}
