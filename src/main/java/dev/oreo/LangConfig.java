package dev.oreo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "lang")
public class LangConfig {

    private String pageTitle = "Livre d'or";
    private String heading = "Livre d'or";
    private String subtitle = "Laissez un message pour le monde entier.";
    private String formHeading = "Écrire un message";
    private String namePlaceholder = "Votre nom";
    private String messagePlaceholder = "Votre message";
    private String sendButton = "Envoyer";
    private String noMessages = "Aucun message pour l'instant. Soyez le premier !";

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