package com.barandar.product.domain;

import com.barandar.product.domain.enumeration.ProductStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    private BigDecimal price;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "available", nullable = false)
    private ProductStatus available;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "product_unit", nullable = false)
    private String productUnit;

    @Column(name = "details")
    private String details;

    @Min(value = 0)
    @Max(value = 100)
    @Column(name = "discount")
    private Integer discount;

    @OneToMany(mappedBy = "product", cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    @JsonBackReference
    private Set<Image> images = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "rel_product__categories", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "categories_id"))
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private Set<ProductCategory> categories = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product id(Long id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public Product description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Product price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ProductStatus getAvailable() {
        return this.available;
    }

    public Product available(ProductStatus available) {
        this.available = available;
        return this;
    }

    public void setAvailable(ProductStatus available) {
        this.available = available;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public Product quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProductUnit() {
        return this.productUnit;
    }

    public Product productUnit(String productUnit) {
        this.productUnit = productUnit;
        return this;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    public String getDetails() {
        return this.details;
    }

    public Product details(String details) {
        this.details = details;
        return this;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Integer getDiscount() {
        return this.discount;
    }

    public Product discount(Integer discount) {
        this.discount = discount;
        return this;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Set<Image> getImages() {
        return this.images;
    }

    public Product images(Set<Image> images) {
        this.setImages(images);
        return this;
    }

    public Product addImages(Image image) {
        this.images.add(image);
        image.setProduct(this);
        return this;
    }

    public Product removeImages(Image image) {
        this.images.remove(image);
        image.setProduct(null);
        return this;
    }

    public void setImages(Set<Image> images) {
        if (this.images != null) {
            this.images.forEach(i -> i.setProduct(null));
        }
        if (images != null) {
            images.forEach(i -> i.setProduct(this));
        }
        this.images = images;
    }

    public Set<ProductCategory> getCategories() {
        return this.categories;
    }

    public Product categories(Set<ProductCategory> productCategories) {
        this.setCategories(productCategories);
        return this;
    }

    public Product addCategories(ProductCategory productCategory) {
        this.categories.add(productCategory);
        productCategory.getProducts().add(this);
        return this;
    }

    public Product removeCategories(ProductCategory productCategory) {
        this.categories.remove(productCategory);
        productCategory.getProducts().remove(this);
        return this;
    }

    public void setCategories(Set<ProductCategory> productCategories) {
        this.categories = productCategories;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        // see
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
                "id=" + getId() +
                ", description='" + getDescription() + "'" +
                ", price=" + getPrice() +
                ", available='" + getAvailable() + "'" +
                ", quantity=" + getQuantity() +
                ", productUnit='" + getProductUnit() + "'" +
                ", details='" + getDetails() + "'" +
                ", discount=" + getDiscount() +
                "}";
    }
}
