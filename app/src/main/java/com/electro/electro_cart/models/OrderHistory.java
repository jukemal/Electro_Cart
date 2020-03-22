package com.electro.electro_cart.models;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Builder;

@Builder
public class OrderHistory implements Serializable {
    private List<CartItem> cartItemList;
    private @ServerTimestamp Date timestamp;

    public OrderHistory() {
    }

    public OrderHistory(List<CartItem> cartItemList, Date timestamp) {
        this.cartItemList = cartItemList;
        this.timestamp = timestamp;
    }

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "OrderHistory{" +
                "cartItemList=" + cartItemList +
                ", timestamp=" + timestamp +
                '}';
    }
}
