package model;

import java.io.Serializable;

public class Message implements Serializable {
    private MessageType type;
    private String Content;

    public Message(MessageType type, String content) {
        this.type = type;
        Content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
