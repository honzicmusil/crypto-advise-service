package org.epam.xm.crypto.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.epam.xm.crypto.mapper.CryptoMapper;
import org.epam.xm.crypto.model.CryptoNormalizedOutput;
import org.epam.xm.crypto.model.CryptoOutput;
import org.epam.xm.crypto.model.CryptoStatisticOutput;
import org.epam.xm.crypto.repository.CryptoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service responsible for providing crypto information, statistics, and normalized price calculations.
 */
@Service
public class CryptoInfoService {

    @Value("${app.forbidden-symbols:}#{T(java.util.Collections).emptyList()}")
    private List<String> forbiddenCryptoSymbols;
    private final CryptoRepository cryptoRepository;
    private final CryptoMapper cryptoMapper;

    /**
     * Constructor to inject the necessary dependencies.
     *
     * @param cryptoRepository Repository for accessing crypto data.
     * @param cryptoMapper     Mapper for converting between entities and DTOs.
     */
    public CryptoInfoService(CryptoRepository cryptoRepository, CryptoMapper cryptoMapper) {
        this.cryptoRepository = cryptoRepository;
        this.cryptoMapper = cryptoMapper;
    }

    /**
     * Retrieves a list of crypto information, optionally filtered by a given symbol,
     * and orders the result by crypto symbol and timestamp.
     *
     * <p>If a symbol is provided, this method fetches cryptos matching that symbol
     * and orders them by symbol and timestamp in descending order.
     * If no symbol is provided, it fetches all cryptos with the same ordering.</p>
     *
     * @param symbol An optional parameter that, if present, indicates which crypto symbol's data to retrieve.
     *               If absent, data for all cryptos will be retrieved.
     * @return A list of {@link CryptoOutput} objects representing the retrieved crypto information.
     */
    public List<CryptoOutput> getAllOrderBySymbolAndTimestamp(Optional<String> symbol) {
        if (symbol.isPresent()) {
            // If a symbol is provided, fetch its related cryptos,
            // filtering out those with forbidden symbols, and order by symbol and timestamp.
            return cryptoMapper.entitiesToDtos(
                    cryptoRepository.findAllBySymbolOrderBySymbolDescTimestampDesc(symbol.get(), forbiddenCryptoSymbols)
            );
        } else {
            // If no symbol is provided, fetch all cryptos,
            // filtering out those with forbidden symbols, and order by symbol and timestamp.
            return cryptoMapper.entitiesToDtos(
                    cryptoRepository.findAllOrderBySymbolDescTimestampDesc(forbiddenCryptoSymbols)
            );
        }
    }


    /**
     * Calculates statistics for all allowed cryptos for a specific month.
     *
     * <p>This method retrieves the list of allowed crypto symbols and computes statistics
     * for each of them for the specified month. Only valid statistics (non-null) are added
     * to the result list.</p>
     *
     * @param yearMonth The specific month and year for which statistics need to be generated.
     * @return A list of {@link CryptoStatisticOutput} containing statistics for each allowed crypto within the specified month.
     */
    public List<CryptoStatisticOutput> calculateAllCryptoStatisticForSpecificMonth(LocalDate yearMonth) {
        // Retrieve the list of allowed crypto symbols from the repository
        final List<String> allowedCryptoSymbols = cryptoRepository.findDistinctBySymbolNotIn(forbiddenCryptoSymbols);

        // Initialize the list to hold the computed statistics
        final List<CryptoStatisticOutput> cryptoStatisticOutputs = new ArrayList<>();

        // Loop through each allowed crypto symbol and compute the statistics
        for (String cryptoSymbol : allowedCryptoSymbols) {
            // Compute statistics for the current crypto symbol for the specified month
            final CryptoStatisticOutput cryptoStatisticOutput = computeCryptoStatisticsForCertainMonth(cryptoSymbol, yearMonth);

            // If the computed statistics are valid, add them to the result list
            if (cryptoStatisticOutput != null) {
                cryptoStatisticOutputs.add(cryptoStatisticOutput);
            }
        }

        // Return the list of computed statistics
        return cryptoStatisticOutputs;
    }


    /**
     * Calculates crypto statistics for a given symbol within a specific month.
     *
     * <p>This method first validates the given crypto symbol to ensure it is allowed for
     * statistical computation. Once validated, it computes the statistics for the given month.</p>
     *
     * @param cryptoSymbol The symbol of the crypto for which statistics need to be generated.
     * @param yearMonth The specific month and year for which statistics need to be generated.
     * @return A {@link CryptoStatisticOutput} containing statistics for the given crypto within the specified month, or null if the given crypto symbol is not in the list of allowed symbols.
     * @throws IllegalArgumentException if the given crypto symbol is in the forbidden symbols list.
     */
    public CryptoStatisticOutput calculateGivenCryptoStatisticForSpecificMonth(String cryptoSymbol, LocalDate yearMonth) {
        // Retrieve the list of allowed crypto symbols from the repository
        final List<String> allowedCryptoSymbols = cryptoRepository.findDistinctBySymbolNotIn(forbiddenCryptoSymbols);

        // Check if the given crypto symbol is in the list of forbidden symbols
        if (forbiddenCryptoSymbols.contains(cryptoSymbol)) {
            throw new IllegalArgumentException("Given crypto symbol is not allowed:" + cryptoSymbol);
        }

        // Check if the given crypto symbol is not in the list of allowed symbols
        if (!allowedCryptoSymbols.contains(cryptoSymbol)) {
            return null;
        }

        // Compute the statistics using the computeCryptoStatisticsForCertainMonth method and return the result
        return computeCryptoStatisticsForCertainMonth(cryptoSymbol, yearMonth);
    }


    /**
     * Calculates crypto statistics for a given symbol within a specific time range.
     *
     * <p>This method first validates the given crypto symbol to ensure it is allowed for
     * statistical computation. Once validated, it computes the statistics for the given time range.</p>
     *
     * @param cryptoSymbol The symbol of the crypto for which statistics need to be generated.
     * @param from The start of the time range for which statistics need to be generated.
     * @param to The end of the time range for which statistics need to be generated.
     * @return A {@link CryptoStatisticOutput} containing statistics for the given crypto within the specified time range, or null if the given crypto symbol is not in the list of allowed symbols.
     * @throws IllegalArgumentException if the given crypto symbol is in the forbidden symbols list.
     */
    public CryptoStatisticOutput calculateGivenCryptoStatisticForTimeRange(String cryptoSymbol, LocalDateTime from, LocalDateTime to) {
        // Retrieve the list of allowed crypto symbols from the repository
        final List<String> allowedCryptoSymbols = cryptoRepository.findDistinctBySymbolNotIn(forbiddenCryptoSymbols);

        // Check if the given crypto symbol is in the list of forbidden symbols
        if (forbiddenCryptoSymbols.contains(cryptoSymbol)) {
            throw new IllegalArgumentException("Given crypto symbol is not allowed:" + cryptoSymbol);
        }

        // Check if the given crypto symbol is not in the list of allowed symbols
        if (!allowedCryptoSymbols.contains(cryptoSymbol)) {
            return null;
        }

        // Compute the statistics using the computeCryptoStatisticsForTimeRange method and return the result
        return computeCryptoStatisticsForTimeRange(cryptoSymbol, from, to);
    }

    /**
     * Creates a {@link CryptoStatisticOutput} for the given crypto symbol and time interval.
     *
     * <p>This method fetches the maximum, minimum, first, and last prices of the specified crypto
     * for the provided time interval. The resulting output encapsulates these metrics along
     * with the crypto symbol and time interval.</p>
     *
     * @param cryptoSymbol The symbol of the crypto for which statistics need to be generated.
     * @param from The starting point of the time interval.
     * @param to The ending point of the time interval.
     * @param interval A string representation of the time interval.
     * @return A {@link CryptoStatisticOutput} containing statistics for the given crypto and time interval
     * or null if any of the fetched metrics are missing.
     */
    private CryptoStatisticOutput createCryptoStatisticOutput(String cryptoSymbol, Timestamp from, Timestamp to, String interval) {
        // Fetch the maximum price for the given crypto and time interval
        final Optional<Double> max = cryptoRepository.findMaxBySymbolAndTimestamp(cryptoSymbol, from, to);

        // Fetch the minimum price for the given crypto and time interval
        final Optional<Double> min = cryptoRepository.findMinBySymbolAndTimestamp(cryptoSymbol, from, to);

        // Fetch the first price for the given crypto and time interval (based on the ascending order of timestamps)
        final Optional<Double> first = cryptoRepository.findFirstBySymbolAndTimestampOrderByTimestampDesc(cryptoSymbol, from, to);

        // Fetch the last price for the given crypto and time interval (based on the descending order of timestamps)
        final Optional<Double> last = cryptoRepository.findLastBySymbolAndTimestampOrderByTimestampDesc(cryptoSymbol, from, to);

        // Check if any of the fetched metrics are missing
        if (first.isEmpty() || last.isEmpty() || min.isEmpty() || max.isEmpty()) {
            return null;
        }

        // Return a CryptoStatisticOutput object containing the fetched metrics, crypto symbol, and time interval
        return new CryptoStatisticOutput(cryptoSymbol, interval, first.get(), last.get(), min.get(), max.get());
    }

    /**
     * Calculates the normalized price for all available cryptos.
     *
     * <p>The normalized price is determined based on the difference between the max and min prices
     * of each crypto, divided by the min price. This method fetches cryptos that are not in the forbidden list
     * and then calculates the normalized price for each.</p>
     *
     * @return A list of {@link CryptoNormalizedOutput} containing cryptos and their normalized prices.
     */
    public List<CryptoNormalizedOutput> calculateNormalizedPriceForAllCrypto() {
        // Fetch allowed crypto symbols that are not in the forbidden list
        final List<String> allowedCryptoSymbols = cryptoRepository.findDistinctBySymbolNotIn(forbiddenCryptoSymbols);

        // Map each allowed crypto symbol to its normalized price and filter out any null results
        return allowedCryptoSymbols.stream()
                .map(cryptoSymbol -> {
                    // Fetch the max and min prices of the crypto
                    final Optional<Double> max = cryptoRepository.findMaxBySymbol(cryptoSymbol);
                    final Optional<Double> min = cryptoRepository.findMinBySymbol(cryptoSymbol);

                    // If either max or min price is not available, return null
                    if (min.isEmpty() || max.isEmpty()) {
                        return null;
                    }

                    // Create and return a new CryptoNormalizedOutput object with the calculated normalized price
                    return new CryptoNormalizedOutput(cryptoSymbol, normalizePrice(min.get(), max.get()));
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Calculates the crypto with the highest normalized price from all available cryptos for a given date.
     *
     * <p>The normalized price is determined based on the difference between the max and min prices
     * of the crypto on that specific date, divided by the min price.
     * This method fetches cryptos that are not in the forbidden list and then calculates the normalized
     * price for each. It returns the crypto with the highest normalized price.</p>
     *
     * @param date The date to be considered for the computation.
     * @return An Optional containing the {@link CryptoNormalizedOutput} with the highest normalized price,
     * or empty if no such crypto is found.
     */
    public Optional<CryptoNormalizedOutput> calculateHighestNormalizedPriceFromAllAvailableCryptos(LocalDate date) {
        // Convert the provided date to the start and end timestamps of the day
        Timestamp from = Timestamp.valueOf(date.atTime(LocalTime.MIN));
        Timestamp to = Timestamp.valueOf(date.atTime(LocalTime.MAX));

        // Fetch allowed crypto symbols that are not in the forbidden list
        final List<String> allowedCryptoSymbols = cryptoRepository.findDistinctBySymbolNotIn(forbiddenCryptoSymbols);

        // For each allowed crypto symbol, calculate the normalized price and return the highest
        return allowedCryptoSymbols.stream()
                .map(cryptoSymbol -> {
                    // Fetch the max and min prices of the crypto for the given date
                    final Optional<Double> max = cryptoRepository.findMaxBySymbolAndTimestamp(cryptoSymbol, from, to);
                    final Optional<Double> min = cryptoRepository.findMinBySymbolAndTimestamp(cryptoSymbol, from, to);

                    // If either max or min price is not available, return null
                    if (min.isEmpty() || max.isEmpty()) {
                        return null;
                    }

                    // Create and return a new CryptoNormalizedOutput object with the calculated normalized price
                    return new CryptoNormalizedOutput(cryptoSymbol, normalizePrice(min.get(), max.get()));
                })
                .toList()
                .stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparingDouble(CryptoNormalizedOutput::normalizedPrice));
    }

    /**
     * Computes the normalized price using given min and max values.
     *
     * @param min Minimum price.
     * @param max Maximum price.
     * @return Normalized price.
     */
    private double normalizePrice(double min, double max) {
        return (max - min) / min;
    }

    /**
     * Computes the crypto statistics for a specific time range.
     *
     * <p>This method converts the provided LocalDateTime range to Timestamp format and
     * delegates the responsibility of computing the statistics to the
     * {@link #createCryptoStatisticOutput(String, Timestamp, Timestamp, String)} method.</p>
     *
     * @param cryptoSymbol The symbol of the crypto for which statistics need to be generated.
     * @param inputFrom The starting point of the time range in LocalDateTime format.
     * @param inputTo The ending point of the time range in LocalDateTime format.
     * @return A {@link CryptoStatisticOutput} containing statistics for the given crypto and time range.
     */
    private CryptoStatisticOutput computeCryptoStatisticsForTimeRange(String cryptoSymbol, LocalDateTime inputFrom, LocalDateTime inputTo) {
        // Convert the provided LocalDateTime range to Timestamp format
        final Timestamp from = Timestamp.valueOf(inputFrom);
        final Timestamp to = Timestamp.valueOf(inputTo);

        // Compute the statistics using the createCryptoStatisticOutput method and return the result
        return createCryptoStatisticOutput(cryptoSymbol, from, to, inputFrom + " - " + inputTo);
    }

    /**
     * Computes the crypto statistics for a specific month.
     *
     * <p>This method takes a specific month represented as a LocalDate, determines its start and end
     * points, and then delegates the responsibility of computing the statistics to the
     * {@link #createCryptoStatisticOutput(String, Timestamp, Timestamp, String)} method.</p>
     *
     * @param cryptoSymbol The symbol of the crypto for which statistics need to be generated.
     * @param inputYearMonth The month in LocalDate format for which statistics need to be generated.
     * @return A {@link CryptoStatisticOutput} containing statistics for the given crypto for the entire month.
     */
    private CryptoStatisticOutput computeCryptoStatisticsForCertainMonth(String cryptoSymbol, LocalDate inputYearMonth) {
        // Convert the provided LocalDate month to YearMonth format
        YearMonth yearMonth = YearMonth.from(inputYearMonth);

        // Determine the start (beginning of the month at midnight) and end (end of the month just before midnight) points in Timestamp format
        final Timestamp from = Timestamp.valueOf(yearMonth.atDay(1).atStartOfDay());
        final Timestamp to = Timestamp.valueOf(yearMonth.atEndOfMonth().atTime(LocalTime.MAX));

        // Compute the statistics using the createCryptoStatisticOutput method and return the result
        return createCryptoStatisticOutput(cryptoSymbol, from, to, yearMonth.toString());
    }


}
