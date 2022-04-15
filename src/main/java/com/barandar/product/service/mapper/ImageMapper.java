package com.barandar.product.service.mapper;

import com.barandar.product.domain.*;
import com.barandar.product.service.dto.ImageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Image} and its DTO {@link ImageDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProductMapper.class })
public interface ImageMapper extends EntityMapper<ImageDTO, Image> {
    ImageDTO toDto(Image s);
}
