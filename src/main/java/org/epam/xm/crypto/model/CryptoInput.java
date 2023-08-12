package org.epam.xm.crypto.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the input data structure for crypto-related operations.
 *
 * <p>This class encapsulates details about a cryptocurrency, such as its timestamp, symbol, and price.
 * It may be used to read or accept input data for further processing or mapping.</p>
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoInput {

    /**
     * Represents the timestamp associated with the crypto data.
     */
    private Timestamp timestamp;

    /**
     * Symbol of the cryptocurrency (e.g., BTC for Bitcoin, ETH for Ethereum).
     */
    private String symbol;

    /**
     * Price of the cryptocurrency at the specified timestamp.
     */
    private Double price;
}
