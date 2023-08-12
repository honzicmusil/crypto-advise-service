package org.epam.xm.crypto.model;

/**
 * Represents the normalized output data for cryptocurrency-related operations.
 *
 * <p>This record encapsulates details about a cryptocurrency after normalization,
 * such as its symbol and the normalized price. It provides a compact way to
 * model immutable data structures.</p>
 *
 * @author Your Name (or you can omit if you don't want this)
 */
public record CryptoNormalizedOutput(

        /*
          Symbol of the cryptocurrency (e.g., BTC for Bitcoin, ETH for Ethereum).
         */
        String symbol,

        /*
          Normalized price of the cryptocurrency.
         */
        Double normalizedPrice
) {

}
