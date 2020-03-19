package com.electro.electro_cart.models;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;

@Builder
public class User implements Serializable {
    private String id;
    private String email;
    private String telephoneNumber;
    private String name;
    private String location;
    private int points;
    private @ServerTimestamp Date timestamp;

    public User() {
    }

    public User(String id, String email, String telephoneNumber, String name, String location, int points, Date timestamp) {
        this.id = id;
        this.email = email;
        this.telephoneNumber = telephoneNumber;
        this.name = name;
        this.location = location;
        this.points = points;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", telephoneNumber='" + telephoneNumber + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", points=" + points +
                ", timestamp=" + timestamp +
                '}';
    }
}
