package com.acme.banking.dbo.spring.it;

import com.acme.banking.dbo.spring.dao.AccountRepository;
import com.acme.banking.dbo.spring.domain.Account;
import com.acme.banking.dbo.spring.domain.SavingAccount;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("integration-test")
@AutoConfigureMockMvc
public class AccountControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    Logger logger;
    @MockBean
    private AccountRepository accounts;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void shouldGetPrePopulatedAccount() throws Exception {
        SavingAccount accountStub = new SavingAccount(100., "aaa@aaa.aa");
        when(accounts.findAll()).thenReturn(asList(accountStub, accountStub, accountStub, accountStub));

        logger.debug(">>>>>" +
                mockMvc.perform(get("/api/accounts").header("X-API-VERSION", "1"))
                        .andExpect(status().is2xxSuccessful())
                        .andExpect(jsonPath("$.length()").value(4))
                        .andReturn().getResponse()
                        .getContentAsString()
        );
    }

    @Test
    public void shouldNotCreateAccountWithDuplicatedEmail() throws Exception {
        Account account = new SavingAccount(100, "a@a.ru");
        when(accounts.findByEmail("a@a.ru")).thenReturn(asList(account)); // return non-null
        logger.debug(">>>>>" +
                mockMvc.perform(post("/api/accounts").header("X-API-VERSION", "1")
                        .content(objectMapper.writeValueAsString(account)))
                        .andExpect(status().isConflict())
                        .andReturn().getResponse()
                        .getContentAsString()
        );

    }

    @Test
    public void shouldCreateAccount() throws Exception {
        when(accounts.findByEmail("a@a.ru")).thenReturn(new ArrayList<>());
        logger.debug(">>>>>" +
                mockMvc.perform(post("/api/accounts").header("X-API-VERSION", "1")
                        .content("{\"email\":\"abab@a.ru\",\"amount\":100.0, \"type\":\"C\"}"))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn().getResponse()
                        .getContentAsString()
        );
    }

    @Test
    public void shouldReturnFirstAccountFromDb() throws Exception {
        Account account = new SavingAccount(12345, "stub@email.ru");
        when(accounts.findById(1L)).thenReturn(java.util.Optional.of(account));
        mockMvc.perform(get("/api/accounts/1").header("X-API-VERSION", "1"))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.email").value("stub@email.ru"))
                .andExpect(jsonPath("$.amount").value("12345.0"));
    }

    @Test
    public void shouldNotReturnNonExistAccount() throws Exception {
        // fail with unknown reason
        when(accounts.findById(1L)).thenThrow(new EmptyResultDataAccessException(0));
        mockMvc.perform(get("/api/accounts/1").header("X-API-VERSION", "1"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void shouldDeleteExistingAccount() throws Exception {
        mockMvc.perform(get("/api/accounts/delete/1").header("X-API-VERSION", "1"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void shouldReturnNotFoundWhenAccountNotExist() throws Exception {
        doNothing().when(accounts.deleteById(1L));
        mockMvc.perform(get("/api/accounts/delete/1").header("X-API-VERSION", "1"))
                .andExpect(status().is2xxSuccessful());
    }
}
