package com.electro.electro_cart.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;

@Builder
public class Question implements Serializable {
    private @DocumentId String id;
    private String question;
    private DocumentReference questionOwner;
    private String answer;
    private DocumentReference answerOwner;
    private @ServerTimestamp Date timestamp;
    private int votes;

    public Question() {
    }

    public Question(String id, String question, DocumentReference questionOwner, String answer, DocumentReference answerOwner, Date timestamp, int votes) {
        this.id = id;
        this.question = question;
        this.questionOwner = questionOwner;
        this.answer = answer;
        this.answerOwner = answerOwner;
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

    public DocumentReference getQuestionOwner() {
        return questionOwner;
    }

    public void setQuestionOwner(DocumentReference questionOwner) {
        this.questionOwner = questionOwner;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public DocumentReference getAnswerOwner() {
        return answerOwner;
    }

    public void setAnswerOwner(DocumentReference answerOwner) {
        this.answerOwner = answerOwner;
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
                ", questionOwner=" + questionOwner +
                ", answer='" + answer + '\'' +
                ", answerOwner=" + answerOwner +
                ", timestamp=" + timestamp +
                ", votes=" + votes +
                '}';
    }
}
