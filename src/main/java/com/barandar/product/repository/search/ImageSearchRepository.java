package com.barandar.product.repository.search;

import com.barandar.product.domain.Image;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Image} entity.
 */
public interface ImageSearchRepository extends ElasticsearchRepository<Image, Long> {}
