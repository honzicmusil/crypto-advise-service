package org.epam.xm.crypto.processor;

import org.epam.xm.crypto.entity.CryptoEntity;
import org.epam.xm.crypto.model.CryptoInput;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Implements the processing logic to convert a {@link CryptoInput} into a {@link CryptoEntity}.
 *
 * <p>This processor is particularly useful within batch operations, where raw data from an input
 * source needs to be transformed or processed into a format suitable for persistence or further operations.
 * In this case, it helps in mapping raw input data into entities ready for database operations.</p>
 *
 * @author Your Name (or you can omit if you don't want this)
 * @see CryptoInput
 * @see CryptoEntity
 */
@Component
public class CryptoInputProcessor implements ItemProcessor<CryptoInput, CryptoEntity> {

    /**
     * Converts a given {@link CryptoInput} object into its corresponding {@link CryptoEntity} representation.
     *
     * @param input The crypto input data to be processed.
     * @return A crypto entity representation of the input data.
     */
    @Override
    public CryptoEntity process(CryptoInput input) {
        return new CryptoEntity(input.getTimestamp(), input.getSymbol(), input.getPrice());
    }
}
