package dev.oreo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("messages")
public class GuestMessage {
    @Id
    private String id;

    private String name;
    private String message;
    private Instant createdAt = Instant.now();

    public GuestMessage() {}

    public GuestMessage(String name, String message) {
        this.name = name;
        this.message = message;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getMessage() { return message; }
    public Instant getCreatedAt() { return createdAt; }

    public String getCreatedAtFormatted() {
        return createdAt.toString().replace("T", " ").substring(0, 19) + " UTC";
    }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setMessage(String message) { this.message = message; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}