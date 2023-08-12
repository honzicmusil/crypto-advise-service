package org.epam.xm.crypto.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a crypto entity with attributes such as timestamp, symbol, and price.
 * This entity corresponds to a table in the database with the specified indexes.
 *
 * <p>Indexes have been added to boost query performance on frequently accessed fields or combinations.</p>
 *
 */
@Data
@Entity
@Table(
        name = "crypto_entity",
        // Defining indexes for the entity.
        indexes = {
                // Compound index on symbol and timestamp for efficient querying based on these fields.
                @Index(name = "index_symbol_timestamp", columnList = "symbol,timestamp"),
                // Index on price for efficient querying based on the price.
                @Index(name = "index_price", columnList = "price")
        }
)
@NoArgsConstructor
public class CryptoEntity {

    /**
     * Constructs a new CryptoEntity with the provided details.
     *
     * @param timestamp The timestamp associated with the crypto data.
     * @param symbol    The symbol representing the crypto.
     * @param price     The price of the crypto at the specified timestamp.
     */
    public CryptoEntity(Timestamp timestamp, String symbol, Double price) {
        this.timestamp = timestamp;
        this.symbol = symbol;
        this.price = price;
    }

    /**
     * Unique identifier for the crypto entity.
     * It's generated automatically when a new record is persisted.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    /**
     * Represents the timestamp associated with the crypto data.
     * This field is mandatory in the database.
     */
    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

    /**
     * Symbol representing the crypto.
     * This field is mandatory in the database.
     */
    @NotNull
    @Column(name = "symbol", nullable = false)
    private String symbol;

    /**
     * Price of the crypto at the specified timestamp.
     * This field is mandatory in the database.
     */
    @NotNull
    @Column(name = "price", nullable = false)
    private Double price;
}
