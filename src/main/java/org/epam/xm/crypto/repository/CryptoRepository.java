package org.epam.xm.crypto.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.epam.xm.crypto.entity.CryptoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link CryptoEntity} CRUD operations.
 *
 * <p>This repository provides various custom query methods to fetch and analyze
 * cryptocurrency data based on different criteria such as symbol, timestamp, and price.</p>
 *
 * @author Your Name (or you can omit if you don't want this)
 * @see CryptoEntity
 */
@Repository
public interface CryptoRepository extends JpaRepository<CryptoEntity, Long> {

    /**
     * Fetches all crypto entities by a specific symbol, excluding forbidden symbols, ordered by symbol and timestamp.
     *
     * @param symbol The specific crypto symbol to fetch.
     * @param forbiddenSymbols A list of symbols to exclude from the results.
     * @return A list of matching crypto entities.
     */
    @Query("SELECT p FROM CryptoEntity p "
            + "WHERE p.symbol NOT IN (:forbiddenSymbols) "
            + "AND p.symbol = :symbol "
            + "ORDER BY p.symbol DESC, p.timestamp DESC")
    List<CryptoEntity> findAllBySymbolOrderBySymbolDescTimestampDesc(String symbol, List<String> forbiddenSymbols);

    /**
     * Fetches all crypto entities, excluding forbidden symbols, ordered by symbol and timestamp.
     *
     * @param forbiddenSymbols A list of symbols to exclude from the results.
     * @return A list of matching crypto entities.
     */
    @Query("SELECT p FROM CryptoEntity p "
            + "WHERE p.symbol NOT IN (:forbiddenSymbols) "
            + "ORDER BY p.symbol DESC, p.timestamp DESC")
    List<CryptoEntity> findAllOrderBySymbolDescTimestampDesc(List<String> forbiddenSymbols);

    /**
     * Finds the maximum price of a cryptocurrency within a specific time range.
     *
     * @param symbol The crypto symbol to search for.
     * @param from Starting timestamp of the range.
     * @param to Ending timestamp of the range.
     * @return Maximum price value or an empty Optional if no record is found.
     */
    @Query("SELECT MAX(p.price) FROM CryptoEntity p "
            + "WHERE p.symbol = :symbol "
            + "AND p.timestamp >= :from AND p.timestamp <= :to")
    Optional<Double> findMaxBySymbolAndTimestamp(String symbol, Timestamp from, Timestamp to);

    /**
     * Finds the maximum price of a given cryptocurrency symbol across all timestamps.
     *
     * @param symbol The crypto symbol to search for.
     * @return The maximum price value for the specified symbol, or an empty Optional if no record is found.
     */
    @Query("SELECT MAX(p.price) FROM CryptoEntity p "
            + "WHERE p.symbol = :symbol ")
    Optional<Double> findMaxBySymbol(String symbol);

    /**
     * Finds the minimum price of a given cryptocurrency symbol within a specified time range.
     *
     * @param symbol The crypto symbol to search for.
     * @param from The start timestamp of the range.
     * @param to The end timestamp of the range.
     * @return The minimum price value within the time range for the specified symbol, or an empty Optional if no record is found.
     */
    @Query("SELECT MIN(p.price) FROM CryptoEntity p "
            + "WHERE p.symbol = :symbol "
            + "AND p.timestamp >= :from AND p.timestamp <= :to ")
    Optional<Double> findMinBySymbolAndTimestamp(String symbol, Timestamp from, Timestamp to);

    /**
     * Finds the minimum price of a given cryptocurrency symbol across all timestamps.
     *
     * @param symbol The crypto symbol to search for.
     * @return The minimum price value for the specified symbol, or an empty Optional if no record is found.
     */
    @Query("SELECT MIN(p.price) FROM CryptoEntity p "
            + "WHERE p.symbol = :symbol ")
    Optional<Double> findMinBySymbol(String symbol);

    /**
     * Retrieves the first recorded price of a given cryptocurrency symbol within a specified time range.
     *
     * @param symbol The crypto symbol to search for.
     * @param from The start timestamp of the range.
     * @param to The end timestamp of the range.
     * @return The earliest recorded price within the time range for the specified symbol, or an empty Optional if no record is found.
     */
    @Query("SELECT p.price FROM CryptoEntity p "
            + "WHERE p.symbol = :symbol "
            + "AND p.timestamp >= :from AND p.timestamp <= :to "
            + "ORDER BY p.timestamp ASC "
            + "LIMIT 1")
    Optional<Double> findFirstBySymbolAndTimestampOrderByTimestampDesc(String symbol, Timestamp from, Timestamp to);

    /**
     * Retrieves the latest recorded price of a given cryptocurrency symbol within a specified time range.
     *
     * @param symbol The crypto symbol to search for.
     * @param from The start timestamp of the range.
     * @param to The end timestamp of the range.
     * @return The latest recorded price within the time range for the specified symbol, or an empty Optional if no record is found.
     */
    @Query("SELECT p.price FROM CryptoEntity p "
            + "WHERE p.symbol = :symbol "
            + "AND p.timestamp >= :from AND p.timestamp <= :to "
            + "ORDER BY p.timestamp DESC "
            + "LIMIT 1")
    Optional<Double> findLastBySymbolAndTimestampOrderByTimestampDesc(String symbol, Timestamp from, Timestamp to);

    /**
     * Fetches distinct crypto symbols, excluding forbidden symbols, ordered in descending order.
     *
     * @param forbiddenSymbols A list of symbols to exclude from the results.
     * @return A list of distinct crypto symbols.
     */
    @Query("SELECT DISTINCT(p.symbol) FROM CryptoEntity p "
            + "WHERE p.symbol NOT IN (:forbiddenSymbols) "
            + "ORDER BY p.symbol DESC")
    List<String> findDistinctBySymbolNotIn(List<String> forbiddenSymbols);
}
