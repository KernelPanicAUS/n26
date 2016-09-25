package com.khalilt.n26.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/transactionservice", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TransactionController {

    @RequestMapping(value = "/transaction", method = RequestMethod.GET)
    @ResponseBody
    public String index() {
        return "{\"status\": "+ HttpStatus.OK.value() +"}";
    }

    @ResponseBody
    @RequestMapping(value = "/transaction/{txnId}", method = RequestMethod.PUT)
    public String createTxn(@PathVariable Long txnId, @RequestBody Map<String, Object> payload) {
        return "{\"status\": "+ HttpStatus.OK.value() +"}";
    }

    @ResponseBody
    @RequestMapping(value = "/transaction/{txnId}", method = RequestMethod.GET)
    public String fetchTxn(@PathVariable Long txnId) {
        return "{\"amount\": 1000, \"type\": \"car\", \"parent_id\": " + txnId + "}";
    }

    @ResponseBody
    @RequestMapping(value = "/types/{type}", method = RequestMethod.GET)
    public String fetchTxnIdsByType(@PathVariable String type) {
        return "[10001,10002,10003]";
    }

    @ResponseBody
    @RequestMapping(value = "/sum/{txnId}", method = RequestMethod.GET)
    public String fetchTxnSumForTxn(@PathVariable Long txnId) {
        return "{\"sum\": 10023}";
    }
}
