package com.barandar.product.service.mapper;

import com.barandar.product.domain.*;
import com.barandar.product.service.dto.ProductCategoryDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductCategory} and its DTO {@link ProductCategoryDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ProductCategoryMapper extends EntityMapper<ProductCategoryDTO, ProductCategory> {
    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    Set<ProductCategoryDTO> toDtoIdSet(Set<ProductCategory> productCategory);
}
