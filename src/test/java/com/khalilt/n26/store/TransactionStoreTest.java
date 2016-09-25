package com.khalilt.n26.store;

import com.khalilt.n26.model.Transaction;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TransactionStoreTest {

    private TransactionStore store = new TransactionStore();

    @Test
    public void testShouldInstantiateCorrectly() {
        TransactionStore store = new TransactionStore();

        assertNotNull(store.transactionMap);
        assertNotNull(store.typeMap);
        assertNotNull(store.parentMap);
        assertNotNull(store.lock);
    }

    @Test
    public void testShouldSuccessfullyPushTxnToStore() {
        String car = "car";
        String shopping = "shopping";

        for (int i = 0; i < 10; i++) {
            Long val = 1111L + i;
            Long id = 2222L + i;
            Transaction txn = new Transaction(car, val,null, id);
            store.push(txn);
        }

        for (int i = 0; i < 10; i++) {
            Long val = 3333L + i;
            Long id = 4444L + i;
            Transaction txn = new Transaction(shopping, val, null, id);
            store.push(txn);
        }

        assertEquals(20, this.store.transactionMap.size());
        assertEquals(10, this.store.typeMap.get(car).size());
        assertEquals(10, this.store.typeMap.get(shopping).size());
    }

    @Test
    public void testGetTxnByIdShouldReturnTxn() {
        Transaction txn = new Transaction("car", 10000L, null, 1234L);

        this.store.push(txn);
        assertEquals(1, this.store.transactionMap.size());

        Transaction result = this.store.getTransactionById(1234L);
        assertEquals("car", result.getType());
        assertEquals(10000L, (long) result.getAmount());
    }

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testGetTxnByIdShouldThrowRuntimeExceptionIfRequestedTxnIdDoesNotExist() {

        Transaction txn = new Transaction("car", 10000L, null, 1234L);

        this.store.push(txn);
        assertEquals(1, this.store.transactionMap.size());

        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Could not resolve txn_id: 12345");

        this.store.getTransactionById(12345L);
    }


    @Test
    public void testGetGetChildIdsForParentIdShouldReturnChildIds() {

        this.store.push(new Transaction("car", 10000L, null, 1234L));
        this.store.push(new Transaction("car", 1000L, 1234L, 1235L));

        assertEquals(2, this.store.transactionMap.size());

        assertEquals(1, this.store.getChildIdsForParentId(1234L).size());
    }


}