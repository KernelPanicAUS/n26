package com.khalilt.n26.store;

import com.khalilt.n26.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class TransactionStore {

    protected Map<Long, Transaction> transactionMap;
    protected Map<String, List<Long>> typeMap;
    protected Map<Long, List<Long>> parentMap;

    protected ReentrantLock lock;

    public TransactionStore() {
        this.transactionMap = new HashMap<>();
        this.typeMap = new HashMap<>();
        this.parentMap = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public void push(Transaction txn) {
        lock.lock();

        try {

            this.transactionMap.put(txn.getId(), txn);
            addTxnIdToTypeMap(txn);

            if (txn.getParent_id() != null) {
                addTxnIdToParentMap(txn);
            }

        } finally {
            lock.unlock();
        }
    }

    public Transaction getTransactionById(Long id) {
        try {
            this.lock.lock();

            boolean exists = this.transactionMap.containsKey(id);

            if (exists) {
                return transactionMap.get(id);
            } else {
                throw new RuntimeException("Could not resolve txn_id: " + id);
            }
        } finally {

            this.lock.unlock();
        }
    }

    public List<Long> getTransactionsByType(String typeString) {

        try {
            this.lock.lock();
            List<Long> txnIds = this.typeMap.get(typeString);

            if (txnIds == null || txnIds.size() < 1) throw new RuntimeException("Type: " + typeString + " was not found.");

            return txnIds;
        } finally {
            this.lock.unlock();
        }
    }

    public List<Long> getChildIdsForParentId(Long parentId) {

        if (!doesParentIdHaveChildren(parentId)) {
            throw new RuntimeException("Txn_id: " + parentId + " does not have any child txns");
        }

        try {
            this.lock.lock();
            return this.parentMap.get(parentId);
        } finally {
            this.lock.unlock();
        }
    }

    public boolean doesParentIdHaveChildren(Long parentId) {
        try {
            this.lock.lock();
            List<Long> children =  this.parentMap.get(parentId);

            return !(children == null || children.size() < 1);
        } finally {
            this.lock.unlock();
        }
    }

    public void clearAll(){
        this.transactionMap = new HashMap<>();
        this.typeMap = new HashMap<>();
        this.parentMap = new HashMap<>();
    }

    /* #################################
     *          Internals
     ################################# */

    protected void addTxnIdToTypeMap(Transaction txn) {
        boolean doesTypeMapExist = this.typeMap.containsKey(txn.getType());

        if (!doesTypeMapExist) {
            List<Long> newList = new ArrayList<Long>();
            this.typeMap.put(txn.getType(), newList);
        }

        this.typeMap.get(txn.getType()).add(txn.getId());
    }

    protected void addTxnIdToParentMap(Transaction txn) {

        boolean doesParentIdListExist = this.parentMap.containsKey(txn.getParent_id());

        if (!doesParentIdListExist) {
            List<Long> newList = new ArrayList<Long>();
            this.parentMap.put(txn.getParent_id(), newList);
        }

        this.parentMap.get(txn.getParent_id()).add(txn.getId());
    }

}
