package com.acme.banking.dbo.spring.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@ApiModel(subTypes = {SavingAccount.class, CheckingAccount.class})
@Entity //TODO JPA Entity semantics
@Inheritance @DiscriminatorColumn(name="ACCOUNT_TYPE")
@JsonPropertyOrder({ "id", "type", "email", "amount" }) //TODO Jackson annotations semantics: https://www.baeldung.com/jackson-annotations & https://github.com/FasterXML/jackson-annotations
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SavingAccount.class, name = "S"),
        @JsonSubTypes.Type(value = CheckingAccount.class, name = "C")
})
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

    @ApiModelProperty(allowableValues = "S,C")
    @JsonIgnore
    public abstract String getType();

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
