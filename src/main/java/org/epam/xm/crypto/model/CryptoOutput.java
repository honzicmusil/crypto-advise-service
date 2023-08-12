package org.epam.xm.crypto.model;

import java.sql.Timestamp;

public record CryptoInput(Timestamp timestamp, String symbol, Double price) {

}
