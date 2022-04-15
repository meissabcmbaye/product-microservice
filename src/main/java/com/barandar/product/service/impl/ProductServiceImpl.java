package com.barandar.product.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.barandar.product.domain.Image;
import com.barandar.product.domain.Product;
import com.barandar.product.repository.ImageRepository;
import com.barandar.product.repository.ProductRepository;
import com.barandar.product.repository.search.ProductSearchRepository;
import com.barandar.product.service.ProductService;
import com.barandar.product.service.dto.ProductDTO;
import com.barandar.product.service.mapper.ProductMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Product}.
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    private final ImageRepository imageRepository;

    private final ProductMapper productMapper;

    private final ProductSearchRepository productSearchRepository;

    public ProductServiceImpl(
            ProductRepository productRepository,
            ProductMapper productMapper,
            ProductSearchRepository productSearchRepository, ImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.productSearchRepository = productSearchRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    public ProductDTO save(ProductDTO productDTO) {
        log.debug("Request to save Product : {}", productDTO);
        Product product = productMapper.toEntity(productDTO);
        List<Image> images = product.getImages().stream().collect(Collectors.toList());
        if (!images.isEmpty()) {
            Image image = images.get(0);
            image = imageRepository.save(image);
            product.getImages().clear();
            product.addImages(image);
        }
        product = productRepository.save(product);
        return productMapper.toDto(product);
    }

    @Override
    public Optional<ProductDTO> partialUpdate(ProductDTO productDTO) {
        log.debug("Request to partially update Product : {}", productDTO);

        return productRepository
                .findById(productDTO.getId())
                .map(
                        existingProduct -> {
                            productMapper.partialUpdate(existingProduct, productDTO);

                            return existingProduct;
                        })
                .map(productRepository::save)
                .map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Products");
        return productRepository.findAll(pageable).map(productMapper::toDto);
    }

    public Page<ProductDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productRepository.findAllWithEagerRelationships(pageable).map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDTO> findOne(Long id) {
        log.debug("Request to get Product : {}", id);
        return productRepository.findOneWithEagerRelationships(id).map(productMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Product : {}", id);
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Products for query {}", query);
        return productSearchRepository.search(queryStringQuery(query), pageable).map(productMapper::toDto);
    }

}
