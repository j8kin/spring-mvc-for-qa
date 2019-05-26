package com.acme.banking.dbo.spring.domain;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("S")
public class SavingAccount extends Account {
    public SavingAccount() { }

    public SavingAccount(double amount, String email) {
        super(amount, email);
    }

    // why not to create type as private and getType in superclass
    @Override
    @ApiModelProperty(allowableValues = "S")
    public String getType() {
        return "S";
    }

    @Override
    public String toString() {
        return super.toString() + " S";
    }
}
