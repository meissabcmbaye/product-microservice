package com.barandar.product.web.rest;

import static com.barandar.product.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.barandar.product.IntegrationTest;
import com.barandar.product.domain.Image;
import com.barandar.product.domain.Product;
import com.barandar.product.domain.ProductCategory;
import com.barandar.product.domain.enumeration.ProductStatus;
import com.barandar.product.repository.ProductRepository;
import com.barandar.product.repository.search.ProductSearchRepository;
import com.barandar.product.service.ProductService;
import com.barandar.product.service.dto.ProductDTO;
import com.barandar.product.service.mapper.ProductMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProductResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProductResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(1 - 1);

    private static final ProductStatus DEFAULT_AVAILABLE = ProductStatus.AVAILABLE;
    private static final ProductStatus UPDATED_AVAILABLE = ProductStatus.OUT_OF_STOCK;

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final String DEFAULT_PRODUCT_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCT_UNIT = "BBBBBBBBBB";

    private static final String DEFAULT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBB";

    private static final Integer DEFAULT_DISCOUNT = 0;
    private static final Integer UPDATED_DISCOUNT = 1;
    private static final Integer SMALLER_DISCOUNT = 0 - 1;

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/products";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductRepository productRepository;

    @Mock
    private ProductRepository productRepositoryMock;

    @Autowired
    private ProductMapper productMapper;

    @Mock
    private ProductService productServiceMock;

    /**
     * This repository is mocked in the com.barandar.product.repository.search test package.
     *
     * @see com.barandar.product.repository.search.ProductSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProductSearchRepository mockProductSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductMockMvc;

    private Product product;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createEntity(EntityManager em) {
        Product product = new Product()
            .description(DEFAULT_DESCRIPTION)
            .price(DEFAULT_PRICE)
            .available(DEFAULT_AVAILABLE)
            .quantity(DEFAULT_QUANTITY)
            .productUnit(DEFAULT_PRODUCT_UNIT)
            .details(DEFAULT_DETAILS)
            .discount(DEFAULT_DISCOUNT);
        return product;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createUpdatedEntity(EntityManager em) {
        Product product = new Product()
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .available(UPDATED_AVAILABLE)
            .quantity(UPDATED_QUANTITY)
            .productUnit(UPDATED_PRODUCT_UNIT)
            .details(UPDATED_DETAILS)
            .discount(UPDATED_DISCOUNT);
        return product;
    }

    @BeforeEach
    public void initTest() {
        product = createEntity(em);
    }

    @Test
    @Transactional
    void createProduct() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();
        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);
        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isCreated());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate + 1);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProduct.getPrice()).isEqualByComparingTo(DEFAULT_PRICE);
        assertThat(testProduct.getAvailable()).isEqualTo(DEFAULT_AVAILABLE);
        assertThat(testProduct.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testProduct.getProductUnit()).isEqualTo(DEFAULT_PRODUCT_UNIT);
        assertThat(testProduct.getDetails()).isEqualTo(DEFAULT_DETAILS);
        assertThat(testProduct.getDiscount()).isEqualTo(DEFAULT_DISCOUNT);

        // Validate the Product in Elasticsearch
        // verify(mockProductSearchRepository, times(1)).save(testProduct);
    }

    @Test
    @Transactional
    void createProductWithExistingId() throws Exception {
        // Create the Product with an existing ID
        product.setId(1L);
        ProductDTO productDTO = productMapper.toDto(product);

        int databaseSizeBeforeCreate = productRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(0)).save(product);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setPrice(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAvailableIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setAvailable(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setQuantity(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkProductUnitIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setProductUnit(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProducts() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].available").value(hasItem(DEFAULT_AVAILABLE.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].productUnit").value(hasItem(DEFAULT_PRODUCT_UNIT)))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS)))
            .andExpect(jsonPath("$.[*].discount").value(hasItem(DEFAULT_DISCOUNT)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductsWithEagerRelationshipsIsEnabled() throws Exception {
        when(productServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(productServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(productServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(productServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get the product
        restProductMockMvc
            .perform(get(ENTITY_API_URL_ID, product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(product.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.available").value(DEFAULT_AVAILABLE.toString()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.productUnit").value(DEFAULT_PRODUCT_UNIT))
            .andExpect(jsonPath("$.details").value(DEFAULT_DETAILS))
            .andExpect(jsonPath("$.discount").value(DEFAULT_DISCOUNT));
    }

    @Test
    @Transactional
    void getProductsByIdFiltering() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        Long id = product.getId();

        defaultProductShouldBeFound("id.equals=" + id);
        defaultProductShouldNotBeFound("id.notEquals=" + id);

        defaultProductShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProductShouldNotBeFound("id.greaterThan=" + id);

        defaultProductShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProductShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProductsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description equals to DEFAULT_DESCRIPTION
        defaultProductShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the productList where description equals to UPDATED_DESCRIPTION
        defaultProductShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description not equals to DEFAULT_DESCRIPTION
        defaultProductShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the productList where description not equals to UPDATED_DESCRIPTION
        defaultProductShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultProductShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the productList where description equals to UPDATED_DESCRIPTION
        defaultProductShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description is not null
        defaultProductShouldBeFound("description.specified=true");

        // Get all the productList where description is null
        defaultProductShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description contains DEFAULT_DESCRIPTION
        defaultProductShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the productList where description contains UPDATED_DESCRIPTION
        defaultProductShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description does not contain DEFAULT_DESCRIPTION
        defaultProductShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the productList where description does not contain UPDATED_DESCRIPTION
        defaultProductShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price equals to DEFAULT_PRICE
        defaultProductShouldBeFound("price.equals=" + DEFAULT_PRICE);

        // Get all the productList where price equals to UPDATED_PRICE
        defaultProductShouldNotBeFound("price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price not equals to DEFAULT_PRICE
        defaultProductShouldNotBeFound("price.notEquals=" + DEFAULT_PRICE);

        // Get all the productList where price not equals to UPDATED_PRICE
        defaultProductShouldBeFound("price.notEquals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultProductShouldBeFound("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE);

        // Get all the productList where price equals to UPDATED_PRICE
        defaultProductShouldNotBeFound("price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price is not null
        defaultProductShouldBeFound("price.specified=true");

        // Get all the productList where price is null
        defaultProductShouldNotBeFound("price.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price is greater than or equal to DEFAULT_PRICE
        defaultProductShouldBeFound("price.greaterThanOrEqual=" + DEFAULT_PRICE);

        // Get all the productList where price is greater than or equal to UPDATED_PRICE
        defaultProductShouldNotBeFound("price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price is less than or equal to DEFAULT_PRICE
        defaultProductShouldBeFound("price.lessThanOrEqual=" + DEFAULT_PRICE);

        // Get all the productList where price is less than or equal to SMALLER_PRICE
        defaultProductShouldNotBeFound("price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price is less than DEFAULT_PRICE
        defaultProductShouldNotBeFound("price.lessThan=" + DEFAULT_PRICE);

        // Get all the productList where price is less than UPDATED_PRICE
        defaultProductShouldBeFound("price.lessThan=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price is greater than DEFAULT_PRICE
        defaultProductShouldNotBeFound("price.greaterThan=" + DEFAULT_PRICE);

        // Get all the productList where price is greater than SMALLER_PRICE
        defaultProductShouldBeFound("price.greaterThan=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByAvailableIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where available equals to DEFAULT_AVAILABLE
        defaultProductShouldBeFound("available.equals=" + DEFAULT_AVAILABLE);

        // Get all the productList where available equals to UPDATED_AVAILABLE
        defaultProductShouldNotBeFound("available.equals=" + UPDATED_AVAILABLE);
    }

    @Test
    @Transactional
    void getAllProductsByAvailableIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where available not equals to DEFAULT_AVAILABLE
        defaultProductShouldNotBeFound("available.notEquals=" + DEFAULT_AVAILABLE);

        // Get all the productList where available not equals to UPDATED_AVAILABLE
        defaultProductShouldBeFound("available.notEquals=" + UPDATED_AVAILABLE);
    }

    @Test
    @Transactional
    void getAllProductsByAvailableIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where available in DEFAULT_AVAILABLE or UPDATED_AVAILABLE
        defaultProductShouldBeFound("available.in=" + DEFAULT_AVAILABLE + "," + UPDATED_AVAILABLE);

        // Get all the productList where available equals to UPDATED_AVAILABLE
        defaultProductShouldNotBeFound("available.in=" + UPDATED_AVAILABLE);
    }

    @Test
    @Transactional
    void getAllProductsByAvailableIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where available is not null
        defaultProductShouldBeFound("available.specified=true");

        // Get all the productList where available is null
        defaultProductShouldNotBeFound("available.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where quantity equals to DEFAULT_QUANTITY
        defaultProductShouldBeFound("quantity.equals=" + DEFAULT_QUANTITY);

        // Get all the productList where quantity equals to UPDATED_QUANTITY
        defaultProductShouldNotBeFound("quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where quantity not equals to DEFAULT_QUANTITY
        defaultProductShouldNotBeFound("quantity.notEquals=" + DEFAULT_QUANTITY);

        // Get all the productList where quantity not equals to UPDATED_QUANTITY
        defaultProductShouldBeFound("quantity.notEquals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where quantity in DEFAULT_QUANTITY or UPDATED_QUANTITY
        defaultProductShouldBeFound("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY);

        // Get all the productList where quantity equals to UPDATED_QUANTITY
        defaultProductShouldNotBeFound("quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where quantity is not null
        defaultProductShouldBeFound("quantity.specified=true");

        // Get all the productList where quantity is null
        defaultProductShouldNotBeFound("quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where quantity is greater than or equal to DEFAULT_QUANTITY
        defaultProductShouldBeFound("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the productList where quantity is greater than or equal to UPDATED_QUANTITY
        defaultProductShouldNotBeFound("quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where quantity is less than or equal to DEFAULT_QUANTITY
        defaultProductShouldBeFound("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the productList where quantity is less than or equal to SMALLER_QUANTITY
        defaultProductShouldNotBeFound("quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where quantity is less than DEFAULT_QUANTITY
        defaultProductShouldNotBeFound("quantity.lessThan=" + DEFAULT_QUANTITY);

        // Get all the productList where quantity is less than UPDATED_QUANTITY
        defaultProductShouldBeFound("quantity.lessThan=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where quantity is greater than DEFAULT_QUANTITY
        defaultProductShouldNotBeFound("quantity.greaterThan=" + DEFAULT_QUANTITY);

        // Get all the productList where quantity is greater than SMALLER_QUANTITY
        defaultProductShouldBeFound("quantity.greaterThan=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByProductUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where productUnit equals to DEFAULT_PRODUCT_UNIT
        defaultProductShouldBeFound("productUnit.equals=" + DEFAULT_PRODUCT_UNIT);

        // Get all the productList where productUnit equals to UPDATED_PRODUCT_UNIT
        defaultProductShouldNotBeFound("productUnit.equals=" + UPDATED_PRODUCT_UNIT);
    }

    @Test
    @Transactional
    void getAllProductsByProductUnitIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where productUnit not equals to DEFAULT_PRODUCT_UNIT
        defaultProductShouldNotBeFound("productUnit.notEquals=" + DEFAULT_PRODUCT_UNIT);

        // Get all the productList where productUnit not equals to UPDATED_PRODUCT_UNIT
        defaultProductShouldBeFound("productUnit.notEquals=" + UPDATED_PRODUCT_UNIT);
    }

    @Test
    @Transactional
    void getAllProductsByProductUnitIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where productUnit in DEFAULT_PRODUCT_UNIT or UPDATED_PRODUCT_UNIT
        defaultProductShouldBeFound("productUnit.in=" + DEFAULT_PRODUCT_UNIT + "," + UPDATED_PRODUCT_UNIT);

        // Get all the productList where productUnit equals to UPDATED_PRODUCT_UNIT
        defaultProductShouldNotBeFound("productUnit.in=" + UPDATED_PRODUCT_UNIT);
    }

    @Test
    @Transactional
    void getAllProductsByProductUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where productUnit is not null
        defaultProductShouldBeFound("productUnit.specified=true");

        // Get all the productList where productUnit is null
        defaultProductShouldNotBeFound("productUnit.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByProductUnitContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where productUnit contains DEFAULT_PRODUCT_UNIT
        defaultProductShouldBeFound("productUnit.contains=" + DEFAULT_PRODUCT_UNIT);

        // Get all the productList where productUnit contains UPDATED_PRODUCT_UNIT
        defaultProductShouldNotBeFound("productUnit.contains=" + UPDATED_PRODUCT_UNIT);
    }

    @Test
    @Transactional
    void getAllProductsByProductUnitNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where productUnit does not contain DEFAULT_PRODUCT_UNIT
        defaultProductShouldNotBeFound("productUnit.doesNotContain=" + DEFAULT_PRODUCT_UNIT);

        // Get all the productList where productUnit does not contain UPDATED_PRODUCT_UNIT
        defaultProductShouldBeFound("productUnit.doesNotContain=" + UPDATED_PRODUCT_UNIT);
    }

    @Test
    @Transactional
    void getAllProductsByDetailsIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where details equals to DEFAULT_DETAILS
        defaultProductShouldBeFound("details.equals=" + DEFAULT_DETAILS);

        // Get all the productList where details equals to UPDATED_DETAILS
        defaultProductShouldNotBeFound("details.equals=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    void getAllProductsByDetailsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where details not equals to DEFAULT_DETAILS
        defaultProductShouldNotBeFound("details.notEquals=" + DEFAULT_DETAILS);

        // Get all the productList where details not equals to UPDATED_DETAILS
        defaultProductShouldBeFound("details.notEquals=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    void getAllProductsByDetailsIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where details in DEFAULT_DETAILS or UPDATED_DETAILS
        defaultProductShouldBeFound("details.in=" + DEFAULT_DETAILS + "," + UPDATED_DETAILS);

        // Get all the productList where details equals to UPDATED_DETAILS
        defaultProductShouldNotBeFound("details.in=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    void getAllProductsByDetailsIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where details is not null
        defaultProductShouldBeFound("details.specified=true");

        // Get all the productList where details is null
        defaultProductShouldNotBeFound("details.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByDetailsContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where details contains DEFAULT_DETAILS
        defaultProductShouldBeFound("details.contains=" + DEFAULT_DETAILS);

        // Get all the productList where details contains UPDATED_DETAILS
        defaultProductShouldNotBeFound("details.contains=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    void getAllProductsByDetailsNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where details does not contain DEFAULT_DETAILS
        defaultProductShouldNotBeFound("details.doesNotContain=" + DEFAULT_DETAILS);

        // Get all the productList where details does not contain UPDATED_DETAILS
        defaultProductShouldBeFound("details.doesNotContain=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    void getAllProductsByDiscountIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where discount equals to DEFAULT_DISCOUNT
        defaultProductShouldBeFound("discount.equals=" + DEFAULT_DISCOUNT);

        // Get all the productList where discount equals to UPDATED_DISCOUNT
        defaultProductShouldNotBeFound("discount.equals=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllProductsByDiscountIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where discount not equals to DEFAULT_DISCOUNT
        defaultProductShouldNotBeFound("discount.notEquals=" + DEFAULT_DISCOUNT);

        // Get all the productList where discount not equals to UPDATED_DISCOUNT
        defaultProductShouldBeFound("discount.notEquals=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllProductsByDiscountIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where discount in DEFAULT_DISCOUNT or UPDATED_DISCOUNT
        defaultProductShouldBeFound("discount.in=" + DEFAULT_DISCOUNT + "," + UPDATED_DISCOUNT);

        // Get all the productList where discount equals to UPDATED_DISCOUNT
        defaultProductShouldNotBeFound("discount.in=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllProductsByDiscountIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where discount is not null
        defaultProductShouldBeFound("discount.specified=true");

        // Get all the productList where discount is null
        defaultProductShouldNotBeFound("discount.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByDiscountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where discount is greater than or equal to DEFAULT_DISCOUNT
        defaultProductShouldBeFound("discount.greaterThanOrEqual=" + DEFAULT_DISCOUNT);

        // Get all the productList where discount is greater than or equal to (DEFAULT_DISCOUNT + 1)
        defaultProductShouldNotBeFound("discount.greaterThanOrEqual=" + (DEFAULT_DISCOUNT + 1));
    }

    @Test
    @Transactional
    void getAllProductsByDiscountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where discount is less than or equal to DEFAULT_DISCOUNT
        defaultProductShouldBeFound("discount.lessThanOrEqual=" + DEFAULT_DISCOUNT);

        // Get all the productList where discount is less than or equal to SMALLER_DISCOUNT
        defaultProductShouldNotBeFound("discount.lessThanOrEqual=" + SMALLER_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllProductsByDiscountIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where discount is less than DEFAULT_DISCOUNT
        defaultProductShouldNotBeFound("discount.lessThan=" + DEFAULT_DISCOUNT);

        // Get all the productList where discount is less than (DEFAULT_DISCOUNT + 1)
        defaultProductShouldBeFound("discount.lessThan=" + (DEFAULT_DISCOUNT + 1));
    }

    @Test
    @Transactional
    void getAllProductsByDiscountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where discount is greater than DEFAULT_DISCOUNT
        defaultProductShouldNotBeFound("discount.greaterThan=" + DEFAULT_DISCOUNT);

        // Get all the productList where discount is greater than SMALLER_DISCOUNT
        defaultProductShouldBeFound("discount.greaterThan=" + SMALLER_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllProductsByImagesIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);
        Image images = ImageResourceIT.createEntity(em);
        em.persist(images);
        em.flush();
        product.addImages(images);
        productRepository.saveAndFlush(product);
        Long imagesId = images.getId();

        // Get all the productList where images equals to imagesId
        defaultProductShouldBeFound("imagesId.equals=" + imagesId);

        // Get all the productList where images equals to (imagesId + 1)
        defaultProductShouldNotBeFound("imagesId.equals=" + (imagesId + 1));
    }

    @Test
    @Transactional
    void getAllProductsByCategoriesIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);
        ProductCategory categories = ProductCategoryResourceIT.createEntity(em);
        em.persist(categories);
        em.flush();
        product.addCategories(categories);
        productRepository.saveAndFlush(product);
        Long categoriesId = categories.getId();

        // Get all the productList where categories equals to categoriesId
        defaultProductShouldBeFound("categoriesId.equals=" + categoriesId);

        // Get all the productList where categories equals to (categoriesId + 1)
        defaultProductShouldNotBeFound("categoriesId.equals=" + (categoriesId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductShouldBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].available").value(hasItem(DEFAULT_AVAILABLE.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].productUnit").value(hasItem(DEFAULT_PRODUCT_UNIT)))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS)))
            .andExpect(jsonPath("$.[*].discount").value(hasItem(DEFAULT_DISCOUNT)));

        // Check, that the count call also returns 1
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductShouldNotBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProduct() throws Exception {
        // Get the product
        restProductMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product
        Product updatedProduct = productRepository.findById(product.getId()).get();
        // Disconnect from session so that the updates on updatedProduct are not directly saved in db
        em.detach(updatedProduct);
        updatedProduct
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .available(UPDATED_AVAILABLE)
            .quantity(UPDATED_QUANTITY)
            .productUnit(UPDATED_PRODUCT_UNIT)
            .details(UPDATED_DETAILS)
            .discount(UPDATED_DISCOUNT);
        ProductDTO productDTO = productMapper.toDto(updatedProduct);

        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProduct.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testProduct.getAvailable()).isEqualTo(UPDATED_AVAILABLE);
        assertThat(testProduct.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testProduct.getProductUnit()).isEqualTo(UPDATED_PRODUCT_UNIT);
        assertThat(testProduct.getDetails()).isEqualTo(UPDATED_DETAILS);
        assertThat(testProduct.getDiscount()).isEqualTo(UPDATED_DISCOUNT);

        // Validate the Product in Elasticsearch
        // verify(mockProductSearchRepository).save(testProduct);
    }

    @Test
    @Transactional
    void putNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(0)).save(product);
    }

    @Test
    @Transactional
    void putWithIdMismatchProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(0)).save(product);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(0)).save(product);
    }

    @Test
    @Transactional
    void partialUpdateProductWithPatch() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct.productUnit(UPDATED_PRODUCT_UNIT).details(UPDATED_DETAILS);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProduct.getPrice()).isEqualByComparingTo(DEFAULT_PRICE);
        assertThat(testProduct.getAvailable()).isEqualTo(DEFAULT_AVAILABLE);
        assertThat(testProduct.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testProduct.getProductUnit()).isEqualTo(UPDATED_PRODUCT_UNIT);
        assertThat(testProduct.getDetails()).isEqualTo(UPDATED_DETAILS);
        assertThat(testProduct.getDiscount()).isEqualTo(DEFAULT_DISCOUNT);
    }

    @Test
    @Transactional
    void fullUpdateProductWithPatch() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .available(UPDATED_AVAILABLE)
            .quantity(UPDATED_QUANTITY)
            .productUnit(UPDATED_PRODUCT_UNIT)
            .details(UPDATED_DETAILS)
            .discount(UPDATED_DISCOUNT);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProduct.getPrice()).isEqualByComparingTo(UPDATED_PRICE);
        assertThat(testProduct.getAvailable()).isEqualTo(UPDATED_AVAILABLE);
        assertThat(testProduct.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testProduct.getProductUnit()).isEqualTo(UPDATED_PRODUCT_UNIT);
        assertThat(testProduct.getDetails()).isEqualTo(UPDATED_DETAILS);
        assertThat(testProduct.getDiscount()).isEqualTo(UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void patchNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(0)).save(product);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(0)).save(product);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(0)).save(product);
    }

    @Test
    @Transactional
    void deleteProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeDelete = productRepository.findAll().size();

        // Delete the product
        restProductMockMvc
            .perform(delete(ENTITY_API_URL_ID, product.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Product in Elasticsearch
        // verify(mockProductSearchRepository, times(1)).deleteById(product.getId());
    }

    @Test
    @Transactional
    void searchProduct() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        productRepository.saveAndFlush(product);
        when(mockProductSearchRepository.search(queryStringQuery("id:" + product.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(product), PageRequest.of(0, 1), 1));

        // Search the product
        restProductMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].available").value(hasItem(DEFAULT_AVAILABLE.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].productUnit").value(hasItem(DEFAULT_PRODUCT_UNIT)))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS)))
            .andExpect(jsonPath("$.[*].discount").value(hasItem(DEFAULT_DISCOUNT)));
    }
}
