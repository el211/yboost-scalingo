package dev.oreo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@ConfigurationProperties(prefix = "lang")
public class LangConfig {

    private static final Logger log = LoggerFactory.getLogger(LangConfig.class);

    private String pageTitle = "Livre d'or";
    private String heading = "Livre d'or";
    private String subtitle = "Laissez un message pour le monde entier.";
    private String formHeading = "Écrire un message";
    private String namePlaceholder = "Votre nom";
    private String messagePlaceholder = "Votre message";
    private String sendButton = "Envoyer";
    private String noMessages = "Aucun message pour l'instant. Soyez le premier !";

    private Admin admin = new Admin();

    public static class Admin {
        private String code;
        private String wrongPassword;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getWrongPassword() { return wrongPassword; }
        public void setWrongPassword(String wrongPassword) { this.wrongPassword = wrongPassword; }
    }

    public String getPageTitle() { return pageTitle; }
    public String getHeading() { return heading; }
    public String getSubtitle() { return subtitle; }
    public String getFormHeading() { return formHeading; }
    public String getNamePlaceholder() { return namePlaceholder; }
    public String getMessagePlaceholder() { return messagePlaceholder; }
    public String getSendButton() { return sendButton; }
    public String getNoMessages() { return noMessages; }
    public Admin getAdmin() { return admin; }

    public void setPageTitle(String pageTitle) { this.pageTitle = pageTitle; }
    public void setHeading(String heading) { this.heading = heading; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public void setFormHeading(String formHeading) { this.formHeading = formHeading; }
    public void setNamePlaceholder(String namePlaceholder) { this.namePlaceholder = namePlaceholder; }
    public void setMessagePlaceholder(String messagePlaceholder) { this.messagePlaceholder = messagePlaceholder; }
    public void setSendButton(String sendButton) { this.sendButton = sendButton; }
    public void setNoMessages(String noMessages) { this.noMessages = noMessages; }
    public void setAdmin(Admin admin) { this.admin = admin; }

    @PostConstruct
    public void logLoadedConfig() {
        log.info("LangConfig loaded (YAML/properties binding OK): pageTitle='{}', heading='{}'", pageTitle, heading);

        boolean hasAdminCode = admin != null && admin.getCode() != null && !admin.getCode().isBlank();
        String wrongPwd = (admin != null ? admin.getWrongPassword() : null);

        log.info("Admin config loaded: codePresent={}, wrongPasswordMsgPresent={}",
                hasAdminCode,
                wrongPwd != null && !wrongPwd.isBlank());
    }
}
