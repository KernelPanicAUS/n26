package com.khalilt.n26.controller;

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
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionControllerTest {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testIndex() throws Exception {
        String body = this.testRestTemplate.getForObject("/transactionservice/transaction", String.class);

        assertThat(body).isEqualTo("{\"status\": "+ HttpStatus.OK.value() +"}");

    }

    @Test
    public void testCreateTxn() throws Exception {

        Map<String, Object> requestBody = new HashMap<String, Object>();
        requestBody.put("foo", "bar");

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);

        Map apiResponse = testRestTemplate.exchange("/transactionservice/transaction/12345", HttpMethod.PUT, httpEntity, Map.class, Collections.emptyMap()).getBody();


        assertEquals(apiResponse.isEmpty(), false);

        assertEquals(apiResponse.get("status"), HttpStatus.OK.value());

    }

    @Test
    public void testFetchTxn() throws Exception {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));

        HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

        ResponseEntity<Map> response = this.testRestTemplate.exchange("/transactionservice/transaction/45678", HttpMethod.GET, httpEntity, Map.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);

        Map responseBody = (Map) response.getBody();

        assertEquals(responseBody.get("amount"), 1000);
        assertEquals(responseBody.get("type"), "car");
        assertEquals(responseBody.get("parent_id"), 45678);
    }

    @Test
    public void testFetchTxnIdsByType() throws Exception {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));

        HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

        ResponseEntity<Object[]> response = this.testRestTemplate.exchange("/transactionservice/types/car", HttpMethod.GET, httpEntity, Object[].class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);

        List<Object> responseBody= Arrays.asList(response.getBody());

        assertEquals(responseBody.size(), 3);
    }

    @Test
    public void testFetchTxnSumForTxn() throws Exception {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));

        HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

        ResponseEntity<Map> response = this.testRestTemplate.exchange("/transactionservice/sum/12345678", HttpMethod.GET, httpEntity, Map.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);

        Map responseBody = (Map) response.getBody();

        assertEquals(responseBody.get("sum"), 10023);
    }
}