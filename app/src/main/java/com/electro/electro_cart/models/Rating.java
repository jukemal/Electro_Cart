package com.electro.electro_cart.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Builder
public class Rating implements Serializable {
    private @DocumentId String id;
    private String header;
    private String description;
    private int score;
    private String name;
    private @ServerTimestamp
    Date timestamp;
    private int votes;

    public Rating() {
    }

    public Rating(String id, String header, String description, int score, String name, Date timestamp, int votes) {
        this.id = id;
        this.header = header;
        this.description = description;
        this.score = score;
        this.name = name;
        this.timestamp = timestamp;
        this.votes = votes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "id='" + id + '\'' +
                ", header='" + header + '\'' +
                ", description='" + description + '\'' +
                ", score=" + score +
                ", name='" + name + '\'' +
                ", timestamp=" + timestamp +
                ", votes=" + votes +
                '}';
    }
}
