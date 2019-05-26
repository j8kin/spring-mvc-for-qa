package com.acme.banking.dbo.spring.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("integration-test")
@AutoConfigureMockMvc
public class AccountControllerLightST {
    @Autowired private MockMvc mockMvc;
    @Autowired Logger logger;

    @Test
    public void shouldGetPrePopulatedAccount() throws Exception {
        logger.debug(">>>>>" +
                mockMvc.perform(get("/api/accounts").header("X-API-VERSION", "1"))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn().getResponse()
                        .getContentAsString()
        );
    }

    @Test
    public void shouldGetAllAccounts() throws Exception {
        mockMvc.perform((get("/api/accounts").header("X-API-VERSION", "1")))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()").value(4))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void shouldReturnFirstAccountFromDb() throws Exception {
        mockMvc.perform(get("/api/accounts/1").header("X-API-VERSION", "1"))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("a@a.ru"));
    }
}
