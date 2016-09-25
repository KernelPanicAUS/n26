package com.khalilt.n26.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionControllerTest {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private TestRestTemplate testRestTemplate;


    @Test
    public void testCreateTxn() throws Exception {

        Map apiResponse = sendCreateTxnRequest(1000, 12345, false);

        assertEquals(apiResponse.isEmpty(), false);

        assertEquals(HttpStatus.OK.getReasonPhrase(), apiResponse.get("status"));

    }

    @Test
    public void testCreateTxnShouldRespondWithErrorMessageWhenTxnTypeIsMissing() throws Exception {

        Map<String, Object> requestBody = new HashMap<String, Object>();
        requestBody.put("amount", 10000);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<String> requestEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);

        Map apiResponse = testRestTemplate.exchange("/transactionservice/transaction/120", HttpMethod.PUT, requestEntity, Map.class, Collections.emptyMap()).getBody();

        assertEquals(apiResponse.isEmpty(), false);

        assertEquals("Type was not specified", apiResponse.get("status"));

    }

    @Test
    public void testCreateTxnShouldRespondWithErrorMessageWhenTxnAmountIsMissing() throws Exception {

        Map<String, Object> requestBody = new HashMap<String, Object>();
        requestBody.put("type", "car");

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<String> requestEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);

        Map apiResponse = testRestTemplate.exchange("/transactionservice/transaction/120", HttpMethod.PUT, requestEntity, Map.class, Collections.emptyMap()).getBody();

        assertEquals(apiResponse.isEmpty(), false);

        assertEquals("Amount was not specified", apiResponse.get("status"));

    }

    @Test
    public void testFetchTxnShouldReturnCorrectJson() throws Exception {

        sendCreateTxnRequest(1000, 12345, false);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);
        ResponseEntity<Map> response = this.testRestTemplate.exchange("/transactionservice/transaction/12345", HttpMethod.GET, httpEntity, Map.class);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        Map responseBody = (Map) response.getBody();

        assertEquals(1000, responseBody.get("amount"));
        assertEquals("car", responseBody.get("type"));
    }

    @Test
    public void testFetchTxnIdsByTypeShouldReturnListOfTransactionIdsCorrespondingToRequestedType() throws Exception {

        for (int i=0; i < 2; i++) {
            sendCreateTxnRequest(1222+i, 122+i, false);
        }

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));

        HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

        ResponseEntity<Object[]> response = this.testRestTemplate.exchange("/transactionservice/types/car", HttpMethod.GET, httpEntity, Object[].class);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        List<Object> responseBody= Arrays.asList(response.getBody());

        assertEquals(2, responseBody.size());
    }


    @Test
    public void testFetchTxnIdsByTypeShouldReturnErrorWhenRequstedTransactionTypeDoesNotExist() throws Exception {

        for (int i=0; i < 2; i++) {
            sendCreateTxnRequest(1222+i, 122+i, false);
        }

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));

        HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

        ResponseEntity<Map> response = this.testRestTemplate.exchange("/transactionservice/types/foobar", HttpMethod.GET, httpEntity, Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map responseBody = response.getBody();

        assertEquals("Type: foobar was not found.", responseBody.get("status"));
    }

    @Test
    public void testFetchTxnSumForParentTxnShouldReturnSumOfParentAndChildTxns() throws Exception {

        sendCreateTxnRequest(2000, 120, false);
        sendCreateTxnRequest(3000, 121, true);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));

        HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

        ResponseEntity<Map> response = this.testRestTemplate.exchange("/transactionservice/sum/120", HttpMethod.GET, httpEntity, Map.class);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        Map responseBody = (Map) response.getBody();

        assertEquals(5000, responseBody.get("sum"));
    }

    @Test
    public void testFetchTxnSumForChildTxnShouldReturnSumOfChildTxn() throws Exception {

        sendCreateTxnRequest(2000, 120, false);
        sendCreateTxnRequest(3000, 121, true);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));

        HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

        ResponseEntity<Map> response = this.testRestTemplate.exchange("/transactionservice/sum/121", HttpMethod.GET, httpEntity, Map.class);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        Map responseBody = (Map) response.getBody();

        assertEquals(3000, responseBody.get("sum"));
    }

    private Map sendCreateTxnRequest(long amount, long id, boolean child) throws JsonProcessingException {

        Map<String, Object> requestBody = new HashMap<String, Object>();
        requestBody.put("amount", amount);
        requestBody.put("type", "car");

        if (child) {
            requestBody.put("parent_id", 120);
        }

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<String> requestEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);

        return testRestTemplate.exchange("/transactionservice/transaction/" + id, HttpMethod.PUT, requestEntity, Map.class, Collections.emptyMap()).getBody();
    }
}