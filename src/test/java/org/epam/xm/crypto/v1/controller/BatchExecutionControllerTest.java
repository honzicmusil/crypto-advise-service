package org.epam.xm.crypto.v1.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.epam.xm.crypto.repository.CryptoRepository;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@SpringBatchTest
@AutoConfigureMockMvc
@EnableAutoConfiguration
class BatchExecutionControllerTest {

    @Autowired
    private CryptoRepository cryptoRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        cryptoRepository.deleteAll();
    }

    @AfterEach
    public void cleanUp() {
        cryptoRepository.deleteAll();
    }

    @Test
    public void testTriggerBatchEndpointSunny() throws Exception {
        mockMvc.perform(post("/api/v1/crypto/batch/trigger-batch"))
                .andExpect(status().isAccepted());

        Assert.assertFalse(cryptoRepository.findAll().isEmpty());
    }
}
