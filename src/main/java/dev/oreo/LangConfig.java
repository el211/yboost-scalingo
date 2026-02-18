package dev.oreo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "lang")
public class LangConfig {

    private String pageTitle = "Guestbook";
    private String heading = "Guestbook";
    private String subtitle = "Leave a message for the world to see.";
    private String formHeading = "Write a message";
    private String namePlaceholder = "Your name";
    private String messagePlaceholder = "Your message";
    private String sendButton = "Send Message";
    private String noMessages = "No messages yet. Be the first!";

    public String getPageTitle() { return pageTitle; }
    public String getHeading() { return heading; }
    public String getSubtitle() { return subtitle; }
    public String getFormHeading() { return formHeading; }
    public String getNamePlaceholder() { return namePlaceholder; }
    public String getMessagePlaceholder() { return messagePlaceholder; }
    public String getSendButton() { return sendButton; }
    public String getNoMessages() { return noMessages; }

    public void setPageTitle(String pageTitle) { this.pageTitle = pageTitle; }
    public void setHeading(String heading) { this.heading = heading; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public void setFormHeading(String formHeading) { this.formHeading = formHeading; }
    public void setNamePlaceholder(String namePlaceholder) { this.namePlaceholder = namePlaceholder; }
    public void setMessagePlaceholder(String messagePlaceholder) { this.messagePlaceholder = messagePlaceholder; }
    public void setSendButton(String sendButton) { this.sendButton = sendButton; }
    public void setNoMessages(String noMessages) { this.noMessages = noMessages; }
}