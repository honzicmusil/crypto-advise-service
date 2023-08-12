package org.epam.xm.crypto.model;

import java.sql.Timestamp;

/**
 * Represents the output data structure for cryptocurrency-related operations.
 *
 * <p>This record encapsulates detailed information about a cryptocurrency, including
 * its timestamp, symbol, and price. It provides a concise and immutable representation
 * for the said data.</p>
 *
 */
public record CryptoOutput(

        /*
          Timestamp marking the time at which the cryptocurrency data was recorded or fetched.
         */
        Timestamp timestamp,

        /*
          Symbol of the cryptocurrency (e.g., BTC for Bitcoin, ETH for Ethereum).
         */
        String symbol,

        /*
          Price of the cryptocurrency at the specified timestamp.
         */
        Double price
) {

}
