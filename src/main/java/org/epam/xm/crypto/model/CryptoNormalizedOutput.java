package org.epam.xm.crypto.model;

import java.sql.Timestamp;

public record CryptoOutput(Timestamp timestamp, String symbol, Double price) {

}
