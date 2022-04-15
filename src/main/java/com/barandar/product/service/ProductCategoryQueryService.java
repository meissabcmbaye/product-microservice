package com.barandar.product.service;

import com.barandar.product.domain.*; // for static metamodels
import com.barandar.product.domain.ProductCategory;
import com.barandar.product.repository.ProductCategoryRepository;
import com.barandar.product.repository.search.ProductCategorySearchRepository;
import com.barandar.product.service.criteria.ProductCategoryCriteria;
import com.barandar.product.service.dto.ProductCategoryDTO;
import com.barandar.product.service.mapper.ProductCategoryMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ProductCategory} entities in the database.
 * The main input is a {@link ProductCategoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProductCategoryDTO} or a {@link Page} of {@link ProductCategoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductCategoryQueryService extends QueryService<ProductCategory> {

    private final Logger log = LoggerFactory.getLogger(ProductCategoryQueryService.class);

    private final ProductCategoryRepository productCategoryRepository;

    private final ProductCategoryMapper productCategoryMapper;

    private final ProductCategorySearchRepository productCategorySearchRepository;

    public ProductCategoryQueryService(
        ProductCategoryRepository productCategoryRepository,
        ProductCategoryMapper productCategoryMapper,
        ProductCategorySearchRepository productCategorySearchRepository
    ) {
        this.productCategoryRepository = productCategoryRepository;
        this.productCategoryMapper = productCategoryMapper;
        this.productCategorySearchRepository = productCategorySearchRepository;
    }

    /**
     * Return a {@link List} of {@link ProductCategoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProductCategoryDTO> findByCriteria(ProductCategoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ProductCategory> specification = createSpecification(criteria);
        return productCategoryMapper.toDto(productCategoryRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProductCategoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductCategoryDTO> findByCriteria(ProductCategoryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProductCategory> specification = createSpecification(criteria);
        return productCategoryRepository.findAll(specification, page).map(productCategoryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProductCategoryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ProductCategory> specification = createSpecification(criteria);
        return productCategoryRepository.count(specification);
    }

    /**
     * Function to convert {@link ProductCategoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProductCategory> createSpecification(ProductCategoryCriteria criteria) {
        Specification<ProductCategory> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ProductCategory_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), ProductCategory_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), ProductCategory_.description));
            }
            if (criteria.getProductsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProductsId(),
                            root -> root.join(ProductCategory_.products, JoinType.LEFT).get(Product_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
