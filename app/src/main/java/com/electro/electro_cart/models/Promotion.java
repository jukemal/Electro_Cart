package com.electro.electro_cart.models;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import lombok.Builder;

@Builder
public class Promotion implements Serializable {

    private String description;
    private Date endDate;
    private int discountPercentage;

    public Promotion() {
    }

    public Promotion(String description, Date endDate, int discountPercentage) {
        this.description = description;
        this.endDate = endDate;
        this.discountPercentage = discountPercentage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    @Override
    public String toString() {
        return "Promotion{" +
                "description='" + description + '\'' +
                ", endDate=" + endDate +
                ", discountPercentage=" + discountPercentage +
                '}';
    }
}
