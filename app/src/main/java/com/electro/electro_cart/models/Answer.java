package com.electro.electro_cart.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import lombok.Builder;

@Builder
public class Answer implements Serializable {
    private @DocumentId String id;
    private String answer;
    private String ownerId;
    private @ServerTimestamp Date timestamp;

    public Answer() {
    }

    public Answer(String id, String answer, String ownerId, Date timestamp) {
        this.id = id;
        this.answer = answer;
        this.ownerId = ownerId;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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

    @Override
    public String toString() {
        return "Answer{" +
                "id='" + id + '\'' +
                ", answer='" + answer + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
