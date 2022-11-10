package com.example.kafkaavrotestsexample.domain;

public class Message {
    private String content;

    public Message(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
