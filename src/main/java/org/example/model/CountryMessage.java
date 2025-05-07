package org.example.model;

import java.io.Serializable;
import java.util.Date;

public class CountryMessage implements Serializable {
    private String country;
    private String sender;         // "employee" or "client"
    private String content;
    private Date timestamp;
    private boolean isReply;

    public CountryMessage(String country, String sender, String content, Date timestamp, boolean isReply) {
        this.country = country;
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
        this.isReply = isReply;
    }

    public String getCountry() { return country; }
    public String getSender() { return sender; }
    public String getContent() { return content; }
    public Date getTimestamp() { return timestamp; }
    public boolean isReply() { return isReply; }

    @Override
    public String toString() {
        return "Country: " + country.toUpperCase() +
                ", Sender: " + sender +
                ", Time: " + timestamp +
                ", Reply: " + isReply +
                ", Message: " + content;
    }
}
