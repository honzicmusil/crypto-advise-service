package org.epam.xm.crypto.v1.controller;

import org.epam.xm.crypto.repository.CryptoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@SpringBatchTest
@AutoConfigureMockMvc
@EnableAutoConfiguration
class CryptoInfoControllerTest {

    public static final int EXPECTED_VALUE = 710;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private CryptoRepository cryptoRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() throws Exception {
        jobLauncherTestUtils.launchJob();
    }

    @AfterEach
    public void cleanUp() {
        cryptoRepository.deleteAll();
    }


    @Test
    public void testNormalisedEndpointSunny() throws Exception {
        mockMvc.perform(get("/api/v1/crypto/info/normalized"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[{\"symbol\":\"XRP\",\"normalizedPrice\":0.5060541310541311},"
                        + "{\"symbol\":\"LTC\",\"normalizedPrice\":0.4651837524177949},"
                        + "{\"symbol\":\"DOGE\",\"normalizedPrice\":0.5046511627906975},"
                        + "{\"symbol\":\"BTC\",\"normalizedPrice\":0.43412110435594536}]")));
    }

    @Test
    public void testStatisticsEndpointForGivenMonthNoContent() throws Exception {
        mockMvc.perform(get("/api/v1/crypto/info/statistics?yearMonth=2021-05-23"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testStatisticsEndpointForGivenMonthSunny() throws Exception {
        mockMvc.perform(get("/api/v1/crypto/info/statistics?yearMonth=2022-01-05"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[{\"cryptoSymbol\":\"XRP\",\"interval\":\"2022-01\",\"oldestValue\":0.8298,\"newestValue\":0.5867,\"minValue\":0.5616,\"maxValue\":0.8458},"
                        + "{\"cryptoSymbol\":\"LTC\",\"interval\":\"2022-01\",\"oldestValue\":148.1,\"newestValue\":109.6,\"minValue\":103.4,\"maxValue\":151.5},"
                        + "{\"cryptoSymbol\":\"DOGE\",\"interval\":\"2022-01\",\"oldestValue\":0.1702,\"newestValue\":0.1415,\"minValue\":0.129,\"maxValue\":0.1941},"
                        + "{\"cryptoSymbol\":\"BTC\",\"interval\":\"2022-01\",\"oldestValue\":46813.21,\"newestValue\":38415.79,\"minValue\":33276.59,\"maxValue\":47722.66}]")));
    }

    @Test
    public void testStatisticsEndpointForGivenMonthAndGivenCryptoSunny() throws Exception {
        mockMvc.perform(get("/api/v1/crypto/info/statistics/BTC?yearMonth=2022-01-05"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"cryptoSymbol\":\"BTC\",\"interval\":\"2022-01\",\"oldestValue\":46813.21,\"newestValue\":38415.79,\"minValue\":33276.59,\"maxValue\":47722.66}")));
    }

    @Test
    public void testStatisticsEndpointForGivenMonthAndGivenCryptoCryptoNotAllowed() throws Exception {
        mockMvc.perform(get("/api/v1/crypto/info/statistics/SHIB?yearMonth=2022-01-05"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("\"message\":\"Given crypto symbol is not allowed:SHIB\"")));
    }

    @Test
    public void testStatisticsEndpointForGivenMonthAndGivenCryptoNoContent() throws Exception {
        mockMvc.perform(get("/api/v1/crypto/info/statistics/ADA?yearMonth=2022-01-05"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testStatisticsEndpointForGivenTimeRangeAndGivenCryptoSunny() throws Exception {
        mockMvc.perform(get("/api/v1/crypto/info/range-statistics/BTC?from=2022-01-0100:00:01&to=2023-01-0100:00:01"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"cryptoSymbol\":\"BTC\",\"interval\":\"2022-01-01T00:00:01 - 2023-01-01T00:00:01\",\"oldestValue\":46813.21,\"newestValue\":38415.79,\"minValue\":33276.59,\"maxValue\":47722.66}")));
    }

    @Test
    public void testStatisticsEndpointForGivenTimeRangeAndGivenCryptoNoContent() throws Exception {
        mockMvc.perform(get("/api/v1/crypto/info/range-statistics/ADA?from=2022-01-0100:00:01&to=2023-01-0100:00:01"))
                .andExpect(status().isNoContent());
    }


    @Test
    public void testStatisticsEndpointForGivenTimeRangeAndGivenCryptoCryptoNotAllowed() throws Exception {
        mockMvc.perform(get("/api/v1/crypto/info/range-statistics/SHIB?from=2022-01-0100:00:01&to=2023-01-0100:00:01"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("\"message\":\"Given crypto symbol is not allowed:SHIB\"")));
    }

    @Test
    public void testEndpointForGivenCryptosSunny() throws Exception {
        mockMvc.perform(get("/api/v1/crypto/info?symbol=ADA"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testEndpointForHighestNormalizedForGivenDaySunny() throws Exception {
        mockMvc.perform(get("/api/v1/crypto/info/normalized/highest?date=2022-01-05"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"symbol\":\"DOGE\",\"normalizedPrice\":0.06191369606003765}")));
    }

    @Test
    public void testEndpointForHighestNormalizedForGivenDayNoContent() throws Exception {
        mockMvc.perform(get("/api/v1/crypto/info/normalized/highest?date=2021-01-17"))
                .andExpect(status().isNoContent());
    }

}
