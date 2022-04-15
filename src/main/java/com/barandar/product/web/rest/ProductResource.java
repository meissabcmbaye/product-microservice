package com.barandar.product.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.barandar.product.domain.Product;
import com.barandar.product.repository.ProductRepository;
import com.barandar.product.service.ProductQueryService;
import com.barandar.product.service.ProductService;
import com.barandar.product.service.criteria.ProductCriteria;
import com.barandar.product.service.dto.ProductDTO;
import com.barandar.product.service.mapper.ProductMapper;
import com.barandar.product.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.barandar.product.domain.Product}.
 */
@RestController
@RequestMapping("/api")
public class ProductResource {

    private final Logger log = LoggerFactory.getLogger(ProductResource.class);

    private static final String ENTITY_NAME = "productProduct";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductService productService;

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final ProductQueryService productQueryService;

    public ProductResource(ProductService productService, ProductRepository productRepository,
            ProductQueryService productQueryService, ProductMapper productMapper) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.productQueryService = productQueryService;
        this.productMapper = productMapper;
    }

    /**
     * {@code POST  /products} : Create a new product.
     *
     * @param productDTO the productDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new productDTO, or with status {@code 400 (Bad Request)} if
     *         the product has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/products")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO)
            throws URISyntaxException {
        log.debug("REST request to save Product : {}", productDTO);
        if (productDTO.getId() != null) {
            throw new BadRequestAlertException("A new product cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProductDTO result = productService.save(productDTO);
        return ResponseEntity
                .created(new URI("/api/products/" + result.getId())).headers(HeaderUtil
                        .createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    /**
     * {@code PUT  /products/:id} : Updates an existing product.
     *
     * @param id         the id of the productDTO to save.
     * @param productDTO the productDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated productDTO, or with status {@code 400 (Bad Request)} if
     *         the productDTO is not valid, or with status
     *         {@code 500 (Internal Server Error)} if the productDTO couldn't be
     *         updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody ProductDTO productDTO) throws URISyntaxException {
        log.debug("REST request to update Product : {}, {}", id, productDTO);
        if (productDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProductDTO result = productService.save(productDTO);
        return ResponseEntity.ok().headers(
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productDTO.getId().toString()))
                .body(result);
    }

    /**
     * {@code PATCH  /products/:id} : Partial updates given fields of an existing
     * product, field will ignore if it is null
     *
     * @param id         the id of the productDTO to save.
     * @param productDTO the productDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated productDTO, or with status {@code 400 (Bad Request)} if
     *         the productDTO is not valid, or with status {@code 404 (Not Found)}
     *         if the productDTO is not found, or with status
     *         {@code 500 (Internal Server Error)} if the productDTO couldn't be
     *         updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/products/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<ProductDTO> partialUpdateProduct(@PathVariable(value = "id", required = false) final Long id,
            @NotNull @RequestBody ProductDTO productDTO) throws URISyntaxException {
        log.debug("REST request to partial update Product partially : {}, {}", id, productDTO);
        if (productDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductDTO> result = productService.partialUpdate(productDTO);

        return ResponseUtil.wrapOrNotFound(result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productDTO.getId().toString()));
    }

    /**
     * {@code GET  /products} : get all the products.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of products in body.
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getAllProducts(ProductCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Products by criteria: {}", criteria);
        Page<ProductDTO> page = productQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil
                .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /products/count} : count all the products.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
     *         in body.
     */
    @GetMapping("/products/count")
    public ResponseEntity<Long> countProducts(ProductCriteria criteria) {
        log.debug("REST request to count Products by criteria: {}", criteria);
        return ResponseEntity.ok().body(productQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /products/:id} : get the "id" product.
     *
     * @param id the id of the productDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the productDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        log.debug("REST request to get Product : {}", id);
        Optional<ProductDTO> productDTO = productService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productDTO);
    }

    /**
     * {@code DELETE  /products/:id} : delete the "id" product.
     *
     * @param id the id of the productDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.debug("REST request to delete Product : {}", id);
        productService.delete(id);
        return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }

    /**
     * {@code SEARCH  /_search/products?query=:query} : search for the product
     * corresponding to the query.
     *
     * @param query    the query of the product search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/products")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Products for query {}", query);
        Page<ProductDTO> page = productService.search(query, pageable);
        HttpHeaders headers = PaginationUtil
                .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/_search-products")
    public ResponseEntity<List<ProductDTO>> getAllByQuery(@RequestParam(value = "query") String query) {
        log.debug("REST request to search for a page of Products for query {}", query);
        List<ProductDTO> result = new ArrayList<ProductDTO>();
        String[] queryString = query.split(" ");
        for (String q : queryString) {
            List<Product> page = productRepository.findByDescriptionContainingIgnoreCase(q);
            for (ProductDTO p : productMapper.toDto(page)) {
                if (!result.contains(p)) {
                    result.add(p);
                }
            }
        }

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/_search-by-category")
    public ResponseEntity<List<Product>> getAllByCategory(@RequestParam(value = "query") String query) {
        log.debug("REST request to search for a page of Products for query {}", query);
        List<Product> result = new ArrayList<Product>();
        String[] queryString = query.split(" ");
        for (String q : queryString) {
            List<Product> page = productRepository.findByCategories_NameContainingIgnoreCase(q);
            for (Product p : page) {
                if (!result.contains(p)) {
                    result.add(p);
                }
            }
        }

        return ResponseEntity.ok().body(result);
    }
}
