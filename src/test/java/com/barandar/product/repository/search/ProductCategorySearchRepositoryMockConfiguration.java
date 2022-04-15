package com.barandar.product.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link ProductCategorySearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ProductCategorySearchRepositoryMockConfiguration {

    @MockBean
    private ProductCategorySearchRepository mockProductCategorySearchRepository;
}
