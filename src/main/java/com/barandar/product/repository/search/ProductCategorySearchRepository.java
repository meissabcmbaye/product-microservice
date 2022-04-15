package com.barandar.product.repository.search;

import com.barandar.product.domain.ProductCategory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ProductCategory} entity.
 */
public interface ProductCategorySearchRepository extends ElasticsearchRepository<ProductCategory, Long> {}
