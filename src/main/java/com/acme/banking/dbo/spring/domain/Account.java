package com.acme.banking.dbo.spring.domain;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Entity //TODO JPA Entity semantics
@Inheritance
@DiscriminatorColumn(name = "ACCOUNT_TYPE")
public abstract class Account {
    /**
     * TODO Validation Framework
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Email
    @Size(max = 50)
    private String email;
    private double amount;

    /**
     * No-arg constructor needed by JPA
     */
    public Account() {
    }

    public Account(double amount, String email) {
        this.amount = amount;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getEmail() {
        return email;
    }

    /**
     * TODO Mutable state
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return getId() + " " + getAmount();
    }
}
