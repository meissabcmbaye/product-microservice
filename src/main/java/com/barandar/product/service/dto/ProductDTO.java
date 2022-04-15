package com.barandar.product.service.dto;

import com.barandar.product.domain.enumeration.ProductStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.barandar.product.domain.Product} entity.
 */
public class ProductDTO implements Serializable {

    private Long id;

    private String description;

    @NotNull
    private BigDecimal price;

    @NotNull
    private ProductStatus available;

    @NotNull
    private Integer quantity;

    @NotNull
    private String productUnit;

    private String details;

    @Min(value = 0)
    @Max(value = 100)
    private Integer discount;

    private Set<ProductCategoryDTO> categories = new HashSet<>();

    private Set<ImageDTO> images = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ProductStatus getAvailable() {
        return available;
    }

    public void setAvailable(ProductStatus available) {
        this.available = available;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Set<ProductCategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(Set<ProductCategoryDTO> categories) {
        this.categories = categories;
    }

    public Set<ImageDTO> getImages() {
        return images;
    }

    public void setImages(Set<ImageDTO> images) {
        this.images = images;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductDTO)) {
            return false;
        }

        ProductDTO productDTO = (ProductDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            ", available='" + getAvailable() + "'" +
            ", quantity=" + getQuantity() +
            ", productUnit='" + getProductUnit() + "'" +
            ", details='" + getDetails() + "'" +
            ", discount=" + getDiscount() +
            ", categories=" + getCategories() +
            ", images=" + getImages() +
            "}";
    }
}
