package com.pitputim;

import java.util.Date;

/**
 * A class that represents a single message object.
 */

public class Message {

    private String messageSender;
    private String messageType;
    private String messageContent;
    private long messageTime;


    public Message(String messageSender, String messageType, String messageContent) {
        this.messageSender = messageSender;
        this.messageType = messageType;
        this.messageContent = messageContent;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public Message(){ }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(String messageSender) {
        this.messageSender = messageSender;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
