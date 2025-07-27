package shared;

import java.io.Serializable;

public class Message implements Serializable {
    private String sender;
    private String content;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        CODE_EDIT
    }

    private MessageType type;

    public Message(MessageType type, String sender, String content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public MessageType getType() {
        return type;
    }
}
