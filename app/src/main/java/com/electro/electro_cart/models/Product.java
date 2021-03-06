package com.electro.electro_cart.models;

import com.electro.electro_cart.utils.EnumProductType;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Singular;

@Builder
public class Product implements Serializable {
    private @DocumentId String id;
    private String name;
    private int price;
    private EnumProductType productType;
    private String storeId;
    private String description;
    private Specification specification;
    @Singular private List<String> image_links;
    private String ar_link;
    private @ServerTimestamp Date timestamp;
    private Promotion promotion;
    private int rating;

    public Product() {
    }

    public Product(String id, String name, int price, EnumProductType productType, String storeId, String description, Specification specification, List<String> image_links, String ar_link, Date timestamp, Promotion promotion, int rating) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.productType = productType;
        this.storeId = storeId;
        this.description = description;
        this.specification = specification;
        this.image_links = image_links;
        this.ar_link = ar_link;
        this.timestamp = timestamp;
        this.promotion = promotion;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public EnumProductType getProductType() {
        return productType;
    }

    public void setProductType(EnumProductType productType) {
        this.productType = productType;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public List<String> getImage_links() {
        return image_links;
    }

    public void setImage_links(List<String> image_links) {
        this.image_links = image_links;
    }

    public String getAr_link() {
        return ar_link;
    }

    public void setAr_link(String ar_link) {
        this.ar_link = ar_link;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", productType=" + productType +
                ", storeId='" + storeId + '\'' +
                ", description='" + description + '\'' +
                ", specification=" + specification +
                ", image_links=" + image_links +
                ", ar_link='" + ar_link + '\'' +
                ", timestamp=" + timestamp +
                ", promotion=" + promotion +
                ", rating=" + rating +
                '}';
    }
}

