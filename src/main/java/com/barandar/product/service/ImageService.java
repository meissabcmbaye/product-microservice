package com.barandar.product.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.barandar.product.domain.Image;
import com.barandar.product.repository.ImageRepository;
import com.barandar.product.repository.search.ImageSearchRepository;
import com.barandar.product.service.dto.ImageDTO;
import com.barandar.product.service.mapper.ImageMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Image}.
 */
@Service
@Transactional
public class ImageService {

    private final Logger log = LoggerFactory.getLogger(ImageService.class);

    private final ImageRepository imageRepository;

    private final ImageMapper imageMapper;

    private final ImageSearchRepository imageSearchRepository;

    public ImageService(ImageRepository imageRepository, ImageMapper imageMapper, ImageSearchRepository imageSearchRepository) {
        this.imageRepository = imageRepository;
        this.imageMapper = imageMapper;
        this.imageSearchRepository = imageSearchRepository;
    }

    /**
     * Save a image.
     *
     * @param imageDTO the entity to save.
     * @return the persisted entity.
     */
    public ImageDTO save(ImageDTO imageDTO) {
        log.debug("Request to save Image : {}", imageDTO);
        Image image = imageMapper.toEntity(imageDTO);
        image = imageRepository.save(image);
        ImageDTO result = imageMapper.toDto(image);
        imageSearchRepository.save(image);
        return result;
    }

    /**
     * Partially update a image.
     *
     * @param imageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ImageDTO> partialUpdate(ImageDTO imageDTO) {
        log.debug("Request to partially update Image : {}", imageDTO);

        return imageRepository
            .findById(imageDTO.getId())
            .map(
                existingImage -> {
                    imageMapper.partialUpdate(existingImage, imageDTO);

                    return existingImage;
                }
            )
            .map(imageRepository::save)
            .map(
                savedImage -> {
                    imageSearchRepository.save(savedImage);

                    return savedImage;
                }
            )
            .map(imageMapper::toDto);
    }

    /**
     * Get all the images.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ImageDTO> findAll() {
        log.debug("Request to get all Images");
        return imageRepository.findAll().stream().map(imageMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one image by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ImageDTO> findOne(Long id) {
        log.debug("Request to get Image : {}", id);
        return imageRepository.findById(id).map(imageMapper::toDto);
    }

    /**
     * Delete the image by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Image : {}", id);
        imageRepository.deleteById(id);
        imageSearchRepository.deleteById(id);
    }

    /**
     * Search for the image corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ImageDTO> search(String query) {
        log.debug("Request to search Images for query {}", query);
        return StreamSupport
            .stream(imageSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(imageMapper::toDto)
            .collect(Collectors.toList());
    }
}
