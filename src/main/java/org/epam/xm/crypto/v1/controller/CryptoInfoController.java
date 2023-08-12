package org.epam.xm.crypto.v1.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.epam.xm.crypto.model.CryptoNormalizedOutput;
import org.epam.xm.crypto.model.CryptoOutput;
import org.epam.xm.crypto.model.CryptoStatisticOutput;
import org.epam.xm.crypto.service.CryptoInfoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

/**
 * REST controller for accessing and managing cryptocurrency information.
 * <p>
 * This controller provides endpoints to fetch statistics, normalized prices, and general
 * information about cryptocurrencies.
 */
@RestController
@RequestMapping("/api/v1/crypto/info")
public class CryptoInfoController {

    // Service layer for handling crypto-related operations.
    private final CryptoInfoService cryptoInfoService;

    /**
     * Constructor dependency injection for `CryptoInfoService`.
     *
     * @param cryptoInfoService Service instance for crypto operations.
     */
    public CryptoInfoController(CryptoInfoService cryptoInfoService) {
        this.cryptoInfoService = cryptoInfoService;
    }

    /**
     * REST endpoint to fetch normalized prices of all allowed cryptocurrencies.
     * <p>
     * This endpoint returns the normalized prices ((max-min)/min) of all allowed
     * cryptocurrencies. The normalization technique aids in standardizing and
     * comparing cryptocurrency prices, providing an understanding of the range of variation.
     * If no data is found, a NO_CONTENT response is returned.
     */
    @Operation(summary = "Endpoint returns list of all allowed crypto normalised ((max-min)/min) prices. In case of no crypto found, NO_CONTENT is returned.")
    @GetMapping(value = "/normalized")
    @ResponseStatus(HttpStatus.OK)
    @RateLimiter(name = "restEndpointRateLimiter")
    public ResponseEntity<List<CryptoNormalizedOutput>> getNormalizedCryptos() {

        // Calculate normalized prices for all allowed cryptos
        List<CryptoNormalizedOutput> results = cryptoInfoService.calculateNormalizedPriceForAllCrypto();

        // Return the calculated normalized prices or a NO_CONTENT status if no data was found.
        return results.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(results, HttpStatus.OK);
    }


    /**
     * REST endpoint to fetch statistics for all allowed cryptocurrencies for a specified month.
     * <p>
     * This endpoint retrieves statistics for all allowed cryptocurrencies over
     * the given month. The month is specified via the 'yearMonth' request parameter.
     * If no statistics are found for the given month, a NO_CONTENT response is returned.
     */
    @Operation(summary = "Endpoint returns list of statistics for all allowed cryptos for given month."
            + "In case of no cryptos found, NO_CONTENT is returned.")
    @GetMapping(value = "/statistics")
    @ResponseStatus(HttpStatus.OK)
    @RateLimiter(name = "restEndpointRateLimiter")
    public ResponseEntity<List<CryptoStatisticOutput>> getStatisticsForAllAllowedCryptos(
            @Parameter(example = "2023-08-01")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate yearMonth) {  // Specified month for which stats are required

        // Calculate statistics for all allowed cryptos for the given month
        List<CryptoStatisticOutput> results = cryptoInfoService.calculateAllCryptoStatisticForSpecificMonth(yearMonth);

        // Return the calculated statistics or a NO_CONTENT status if no statistics were found.
        return results.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(results, HttpStatus.OK);
    }

    /**
     * REST endpoint to fetch statistics for a given cryptocurrency for a specified month.
     * <p>
     * This endpoint retrieves statistics for a specified cryptocurrency symbol over
     * the given month. The month is provided via the 'yearMonth' request parameter.
     * If no statistics are found for the provided criteria, a NO_CONTENT response is returned.
     */
    @Operation(summary = "Endpoint returns list of statistics for given crypto and given month."
            + "In case of no cryptos found, NO_CONTENT is returned.")
    @GetMapping(value = "/statistics/{cryptoSymbol}")
    @ResponseStatus(HttpStatus.OK)
    @RateLimiter(name = "restEndpointRateLimiter")
    public ResponseEntity<CryptoStatisticOutput> getStatisticsForAllAllowedCryptos(
            @PathVariable String cryptoSymbol, // Cryptocurrency symbol
            @Parameter(example = "2023-08-01")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate yearMonth) {  // Specified month

        // Calculate statistics for the given crypto and month
        CryptoStatisticOutput cryptoStatisticOutput = cryptoInfoService.calculateGivenCryptoStatisticForSpecificMonth(cryptoSymbol, yearMonth);

        // Return the calculated statistics or a NO_CONTENT status if no statistics found.
        return cryptoStatisticOutput == null ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(cryptoStatisticOutput, HttpStatus.OK);
    }

    /**
     * REST endpoint to fetch statistics for a given cryptocurrency over a specified time range.
     * <p>
     * This endpoint retrieves statistics for a specified cryptocurrency symbol over
     * the defined time range. The time range is provided via 'from' and 'to' request parameters.
     * If no statistics are found for the provided criteria, a NO_CONTENT response is returned.
     */
    @Operation(summary = "Endpoint returns list of statistics for given crypto and given time range."
            + "In case of no cryptos found, NO_CONTENT is returned.")
    @GetMapping(value = "/range-statistics/{symbol}")
    @ResponseStatus(HttpStatus.OK)
    @RateLimiter(name = "restEndpointRateLimiter")
    public ResponseEntity<CryptoStatisticOutput> getStatisticsForTimeRange(
            @PathVariable String symbol, // Cryptocurrency symbol
            @Parameter(example = "2023-04-01 00:00:00")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-ddHH:mm:ss") LocalDateTime from, // Start of the time range
            @Parameter(example = "2023-07-31 23:59:59")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-ddHH:mm:ss") LocalDateTime to) {  // End of the time range

        // Calculate statistics for the given crypto and time range
        CryptoStatisticOutput cryptoStatisticOutput = cryptoInfoService.calculateGivenCryptoStatisticForTimeRange(symbol, from, to);

        // Return the calculated statistics or a NO_CONTENT status if no statistics found.
        return cryptoStatisticOutput == null ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(cryptoStatisticOutput, HttpStatus.OK);
    }


    /**
     * REST endpoint to fetch available cryptocurrencies.
     * <p>
     * This endpoint retrieves a list of all available cryptocurrencies. If a symbol
     * parameter is provided, the response will be filtered to only return cryptos
     * with the specified symbol.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Endpoint returns list of all allowed cryptos. if symbol is provided, only cryptos with that symbol are returned."
            + "In case of no cryptos found, NO_CONTENT is returned.")
    @RateLimiter(name = "restEndpointRateLimiter")
    public ResponseEntity<List<CryptoOutput>> getAvailableCryptos(@RequestParam Optional<String> symbol) {

        // Fetching all available cryptos or specific crypto based on the provided symbol.
        List<CryptoOutput> results = cryptoInfoService.getAllOrderBySymbolAndTimestamp(symbol);

        // Return the list of available cryptos or a NO_CONTENT status if the list is empty.
        return results.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(results, HttpStatus.OK);
    }

    /**
     * Endpoint that fetches the highest normalized crypto price for a given day.
     *
     * @param date Date for which to fetch the highest normalized price.
     * @return The highest normalized price or NO_CONTENT if not found.
     */
    @Operation(summary = "Endpoint returns highest normalized crypto price for specific day."
        + "In case of no crypto found, NO_CONTENT is returned.")
    @GetMapping("/normalized/highest")
    @ResponseStatus(HttpStatus.OK)
    @RateLimiter(name = "restEndpointRateLimiter")
    public ResponseEntity<CryptoNormalizedOutput> getHighestNormalizedForDay(
            @Parameter(example = "2023-08-01")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Optional<CryptoNormalizedOutput> result = cryptoInfoService.calculateHighestNormalizedPriceFromAllAvailableCryptos(date);
        return result.map(cryptoNormalizedOutput -> new ResponseEntity<>(cryptoNormalizedOutput, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}
