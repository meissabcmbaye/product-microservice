package com.barandar.product.repository;

import com.barandar.product.domain.Product;
import com.barandar.product.service.dto.ProductDTO;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @Query(value = "select distinct product from Product product left join fetch product.categories", countQuery = "select count(distinct product) from Product product")
    Page<Product> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct product from Product product left join fetch product.categories")
    List<Product> findAllWithEagerRelationships();

    @Query("select product from Product product left join fetch product.categories where product.id =:id")
    Optional<Product> findOneWithEagerRelationships(@Param("id") Long id);

    List<Product> findByDescriptionContainingIgnoreCase(@Param("description") String description);

    List<Product> findByCategories_NameContainingIgnoreCase(@Param("category") String category);

}
