package com.barandar.product.service.mapper;

import com.barandar.product.domain.*;
import com.barandar.product.service.dto.ProductDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProductCategoryMapper.class })
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "categories", source = "categories", qualifiedByName = "idSet")
    ProductDTO toDto(Product s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoId(Product product);

    @Mapping(target = "removeCategories", ignore = true)
    Product toEntity(ProductDTO productDTO);
}
