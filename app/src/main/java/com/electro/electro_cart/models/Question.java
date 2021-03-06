package com.electro.electro_cart.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Builder;

@Builder
public class Question implements Serializable {
    private @DocumentId String id;
    private String question;
    private String ownerId;
    private @ServerTimestamp Date timestamp;
    private int votes;

    public Question() {
    }

    public Question(String id, String question, String ownerId, Date timestamp, int votes) {
        this.id = id;
        this.question = question;
        this.ownerId = ownerId;
        this.timestamp = timestamp;
        this.votes = votes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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
        return "Question{" +
                "id='" + id + '\'' +
                ", question='" + question + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", timestamp=" + timestamp +
                ", votes=" + votes +
                '}';
    }
}
