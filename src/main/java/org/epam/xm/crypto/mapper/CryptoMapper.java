package org.epam.xm.crypto.mapper;

import java.util.List;

import org.epam.xm.crypto.entity.CryptoEntity;
import org.epam.xm.crypto.model.CryptoOutput;
import org.mapstruct.Mapper;

/**
 * Provides mapping functions to convert between {@link CryptoEntity} and {@link CryptoOutput} DTO.
 * Utilizing MapStruct for generating the implementation at compile-time.
 *
 * <p>The generated mapper is a Spring bean, and it can be autowired wherever necessary.</p>
 *
 */
@Mapper(componentModel = "spring")
public interface CryptoMapper {

    /**
     * Transforms a list of {@link CryptoEntity} objects into a list of {@link CryptoOutput} DTOs.
     *
     * @param entities The list of crypto entities to be transformed.
     * @return A list of crypto DTOs after the transformation.
     */
    List<CryptoOutput> entitiesToDtos(List<CryptoEntity> entities);
}
