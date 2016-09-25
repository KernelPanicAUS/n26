package com.khalilt.n26.model;

public class Transaction {

    private String type;
    private Long amount;
    private Long id;
    private Long parent_id;

    public Transaction(String type, Long amount, Long parent_id, Long id) {
        this.type = type;
        this.amount = amount;
        this.parent_id = parent_id;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
