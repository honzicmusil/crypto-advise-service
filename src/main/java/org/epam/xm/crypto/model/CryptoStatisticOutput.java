package org.epam.xm.crypto.model;

/**
 * Represents a statistical summary of cryptocurrency data over a specific interval.
 *
 * <p>This record encapsulates vital statistics about a cryptocurrency, such as its
 * oldest, newest, minimum, and maximum price values within a specified time interval.
 * It provides a clear and immutable representation for summarizing crypto data trends.</p>
 *
 */
public record CryptoStatisticOutput(

        /*
          Symbol of the cryptocurrency (e.g., BTC for Bitcoin, ETH for Ethereum).
         */
        String cryptoSymbol,

        /*
          The interval (e.g., "daily", "monthly") over which these statistics were calculated.
         */
        String interval,

        /*
          Oldest (or starting) value of the cryptocurrency within the specified interval.
         */
        Double oldestValue,

        /*
          Most recent (or ending) value of the cryptocurrency within the specified interval.
         */
        Double newestValue,

        /*
          Minimum price value of the cryptocurrency observed within the specified interval.
         */
        Double minValue,

        /*
          Maximum price value of the cryptocurrency observed within the specified interval.
         */
        Double maxValue
) {

}
