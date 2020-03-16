package com.electro.electro_cart.models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;

@Builder
public class CartItem implements Serializable {
    private String ProductID;
    private DocumentReference productReference;
    private int itemCount;
    private @ServerTimestamp Date timestamp;

    public CartItem() {
    }

    public CartItem(String productID, DocumentReference productReference, int itemCount, Date timestamp) {
        ProductID = productID;
        this.productReference = productReference;
        this.itemCount = itemCount;
        this.timestamp = timestamp;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public DocumentReference getProductReference() {
        return productReference;
    }

    public void setProductReference(DocumentReference productReference) {
        this.productReference = productReference;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "ProductID='" + ProductID + '\'' +
                ", productReference=" + productReference +
                ", itemCount=" + itemCount +
                ", timestamp=" + timestamp +
                '}';
    }
}
