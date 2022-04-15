package com.barandar.product.service;

import com.barandar.product.service.dto.ProductDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.barandar.product.domain.Product}.
 */
public interface ProductService {
    /**
     * Save a product.
     *
     * @param productDTO the entity to save.
     * @return the persisted entity.
     */
    ProductDTO save(ProductDTO productDTO);

    /**
     * Partially updates a product.
     *
     * @param productDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProductDTO> partialUpdate(ProductDTO productDTO);

    /**
     * Get all the products.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductDTO> findAll(Pageable pageable);

    /**
     * Get all the products with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" product.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProductDTO> findOne(Long id);

    /**
     * Delete the "id" product.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the product corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductDTO> search(String query, Pageable pageable);
}
