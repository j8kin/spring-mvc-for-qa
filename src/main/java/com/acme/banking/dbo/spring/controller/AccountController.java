package com.acme.banking.dbo.spring.controller;

import com.acme.banking.dbo.spring.dao.AccountRepository;
import com.acme.banking.dbo.spring.domain.Account;
import com.acme.banking.dbo.spring.domain.CheckingAccount;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping(value = "/api", headers = "X-API-VERSION")
@Validated
@Api(value = "Accounts", description = "Operations on Accounts of DBO")
public class AccountController {
    @Resource
    private AccountRepository accounts;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Logger logger;

    @GetMapping(value = "/accounts", headers = "X-API-VERSION=1")
    @ApiOperation(value = "View a list of all accounts", response = Collection.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public Collection<Account> getAllAccounts() {
        return accounts.findAll();
    }

    /**
     * @param id is @Valid out-of-box by using @Validated annotation for Controller class
     */
    @GetMapping(value = "/accounts/{id}", headers = "X-API-VERSION=1")
    public ResponseEntity<Account> getAccount(@PathVariable @PositiveOrZero(message = "No negative id!") long id) {
        return accounts.findById(id)
                .map(account -> new ResponseEntity<>(account, HttpStatus.FOUND))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account not found id: " + id
                ));
    }

    /**
     * @param id is @Valid out-of-box by using @Validated annotation for Controller class
     */
    @GetMapping(value = "/accounts/delete/{id}", headers = "X-API-VERSION=1")
    public ResponseEntity<Account> deleteAccount(@PathVariable @PositiveOrZero(message = "No negative id!") long id) {
        try {
            accounts.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Account not found id: " + id, e
            );
        }
    }

    @PostMapping(value = "/accounts", headers = "X-API-VERSION=1")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        if (!accounts.findByEmail(account.getEmail()).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Account already exist: " + account
            );
        }

        Account saved = accounts.save(account);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
