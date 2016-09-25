package com.khalilt.n26.controller;

import com.khalilt.n26.http.JsonHttpResponse;
import com.khalilt.n26.model.Transaction;
import com.khalilt.n26.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/transactionservice", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/transaction/{txnId}", method = RequestMethod.PUT)
    public JsonHttpResponse createTxn(@PathVariable Long txnId, @RequestBody Map<String, Object> payload) {
        this.transactionService.createTxn(txnId, payload);

        return new JsonHttpResponse(HttpStatus.OK.getReasonPhrase());
    }

    @RequestMapping(value = "/transaction/{txnId}", method = RequestMethod.GET)
    public ResponseEntity<Transaction> fetchTxn(@PathVariable Long txnId) {
        return new ResponseEntity<Transaction>(this.transactionService.fetchTransaction(txnId), HttpStatus.FOUND);
    }

    @RequestMapping(value = "/types/{type}", method = RequestMethod.GET)
    public ResponseEntity<List> fetchTxnIdsByType(@PathVariable String type) {
        return new ResponseEntity<List>(this.transactionService.fetchTxnIdsByType(type), HttpStatus.FOUND);
    }

    @RequestMapping(value = "/sum/{txnId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Long>> fetchTxnSumForTxn(@PathVariable Long txnId) {
        Map response = new HashMap<>();
        response.put("sum", this.transactionService.getSumForTransactionId(txnId));

        return new ResponseEntity<Map<String, Long>>(response, HttpStatus.FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Status> handleException(RuntimeException e) {
        Status status = new Status(e.getMessage());
        return new ResponseEntity<Status>(status, HttpStatus.BAD_REQUEST);
    }
}
