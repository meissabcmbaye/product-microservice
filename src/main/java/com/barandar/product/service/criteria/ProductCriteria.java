package com.barandar.product.service.criteria;

import com.barandar.product.domain.enumeration.ProductStatus;
import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BigDecimalFilter;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.barandar.product.domain.Product} entity. This class is used
 * in {@link com.barandar.product.web.rest.ProductResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProductCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ProductStatus
     */
    public static class ProductStatusFilter extends Filter<ProductStatus> {

        public ProductStatusFilter() {}

        public ProductStatusFilter(ProductStatusFilter filter) {
            super(filter);
        }

        @Override
        public ProductStatusFilter copy() {
            return new ProductStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter description;

    private BigDecimalFilter price;

    private ProductStatusFilter available;

    private IntegerFilter quantity;

    private StringFilter productUnit;

    private StringFilter details;

    private IntegerFilter discount;

    private LongFilter imagesId;

    private LongFilter categoriesId;

    public ProductCriteria() {}

    public ProductCriteria(ProductCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.price = other.price == null ? null : other.price.copy();
        this.available = other.available == null ? null : other.available.copy();
        this.quantity = other.quantity == null ? null : other.quantity.copy();
        this.productUnit = other.productUnit == null ? null : other.productUnit.copy();
        this.details = other.details == null ? null : other.details.copy();
        this.discount = other.discount == null ? null : other.discount.copy();
        this.imagesId = other.imagesId == null ? null : other.imagesId.copy();
        this.categoriesId = other.categoriesId == null ? null : other.categoriesId.copy();
    }

    @Override
    public ProductCriteria copy() {
        return new ProductCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public BigDecimalFilter getPrice() {
        return price;
    }

    public BigDecimalFilter price() {
        if (price == null) {
            price = new BigDecimalFilter();
        }
        return price;
    }

    public void setPrice(BigDecimalFilter price) {
        this.price = price;
    }

    public ProductStatusFilter getAvailable() {
        return available;
    }

    public ProductStatusFilter available() {
        if (available == null) {
            available = new ProductStatusFilter();
        }
        return available;
    }

    public void setAvailable(ProductStatusFilter available) {
        this.available = available;
    }

    public IntegerFilter getQuantity() {
        return quantity;
    }

    public IntegerFilter quantity() {
        if (quantity == null) {
            quantity = new IntegerFilter();
        }
        return quantity;
    }

    public void setQuantity(IntegerFilter quantity) {
        this.quantity = quantity;
    }

    public StringFilter getProductUnit() {
        return productUnit;
    }

    public StringFilter productUnit() {
        if (productUnit == null) {
            productUnit = new StringFilter();
        }
        return productUnit;
    }

    public void setProductUnit(StringFilter productUnit) {
        this.productUnit = productUnit;
    }

    public StringFilter getDetails() {
        return details;
    }

    public StringFilter details() {
        if (details == null) {
            details = new StringFilter();
        }
        return details;
    }

    public void setDetails(StringFilter details) {
        this.details = details;
    }

    public IntegerFilter getDiscount() {
        return discount;
    }

    public IntegerFilter discount() {
        if (discount == null) {
            discount = new IntegerFilter();
        }
        return discount;
    }

    public void setDiscount(IntegerFilter discount) {
        this.discount = discount;
    }

    public LongFilter getImagesId() {
        return imagesId;
    }

    public LongFilter imagesId() {
        if (imagesId == null) {
            imagesId = new LongFilter();
        }
        return imagesId;
    }

    public void setImagesId(LongFilter imagesId) {
        this.imagesId = imagesId;
    }

    public LongFilter getCategoriesId() {
        return categoriesId;
    }

    public LongFilter categoriesId() {
        if (categoriesId == null) {
            categoriesId = new LongFilter();
        }
        return categoriesId;
    }

    public void setCategoriesId(LongFilter categoriesId) {
        this.categoriesId = categoriesId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductCriteria that = (ProductCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(description, that.description) &&
            Objects.equals(price, that.price) &&
            Objects.equals(available, that.available) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(productUnit, that.productUnit) &&
            Objects.equals(details, that.details) &&
            Objects.equals(discount, that.discount) &&
            Objects.equals(imagesId, that.imagesId) &&
            Objects.equals(categoriesId, that.categoriesId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, price, available, quantity, productUnit, details, discount, imagesId, categoriesId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (price != null ? "price=" + price + ", " : "") +
            (available != null ? "available=" + available + ", " : "") +
            (quantity != null ? "quantity=" + quantity + ", " : "") +
            (productUnit != null ? "productUnit=" + productUnit + ", " : "") +
            (details != null ? "details=" + details + ", " : "") +
            (discount != null ? "discount=" + discount + ", " : "") +
            (imagesId != null ? "imagesId=" + imagesId + ", " : "") +
            (categoriesId != null ? "categoriesId=" + categoriesId + ", " : "") +
            "}";
    }
}
