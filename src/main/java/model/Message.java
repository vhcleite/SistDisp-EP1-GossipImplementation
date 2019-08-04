package model;

import java.io.Serializable;

public class Message implements Serializable {
    private MessageType type;
    private String content;

    public Message(MessageType type, String content) {
        this.type = type;
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message [type=" + type + ", Content=" + content + "]";
    }

    public boolean isFormatted() {
        return this.type != null && this.content != null;
    }
}
