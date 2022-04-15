package com.barandar.product.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductCategoryMapperTest {

    private ProductCategoryMapper productCategoryMapper;

    @BeforeEach
    public void setUp() {
        productCategoryMapper = new ProductCategoryMapperImpl();
    }
}
