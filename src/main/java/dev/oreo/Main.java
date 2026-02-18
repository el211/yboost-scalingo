package dev.oreo;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties
@Controller
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private final GuestMessageRepo repo;
    private final LangConfig lang;

    public Main(GuestMessageRepo repo, LangConfig lang) {
        this.repo = repo;
        this.lang = lang;
    }

    @PostConstruct
    public void bootTraces() {
        log.info("App boot: starting checks...");
        String adminCode = lang.getAdmin() != null ? lang.getAdmin().getCode() : null;
        log.info("Admin code configured: {}", adminCode != null && !adminCode.isBlank());

        try {
            long count = repo.count();
            log.info("Connected to MongoDB: OK (guest_messages_count={})", count);
        } catch (Exception e) {
            log.error("Connected to MongoDB: FAIL ({})", e.getMessage());
        }
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        List<GuestMessage> messages = repo.findAll()
                .stream()
                .sorted(Comparator.comparing(GuestMessage::getCreatedAt).reversed())
                .toList();

        boolean isAdmin = Boolean.TRUE.equals(session.getAttribute("isAdmin"));

        model.addAttribute("messages", messages);
        model.addAttribute("lang", lang);
        model.addAttribute("isAdmin", isAdmin);
        return "index";
    }

    @PostMapping("/guestbook")
    public String add(@RequestParam String name, @RequestParam String message) {
        repo.save(new GuestMessage(name, message));
        log.info("Guestbook message created: name='{}'", name);
        return "redirect:/";
    }

    @PostMapping("/admin/login")
    public ResponseEntity<Void> login(@RequestParam String code, HttpSession session) {
        String expected = lang.getAdmin() != null ? lang.getAdmin().getCode() : null;

        if (expected != null && !expected.isBlank() && expected.equals(code)) {
            session.setAttribute("isAdmin", true);
            log.info("Admin login: SUCCESS");
            return ResponseEntity.noContent().build();
        }

        session.removeAttribute("isAdmin");
        log.warn("Admin login: FAIL");
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("isAdmin");
        log.info("Admin logout");
        return "redirect:/";
    }

    @PostMapping("/admin/clear")
    public String clearDb(HttpSession session) {
        if (Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            repo.deleteAll();
            log.warn("Admin action: CLEAR_DB");
        } else {
            log.warn("Admin action blocked: CLEAR_DB");
        }
        return "redirect:/";
    }

    @PostMapping("/admin/delete/{id}")
    public String deleteMessage(@PathVariable String id, HttpSession session) {
        if (Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            repo.deleteById(id);
            log.warn("Admin action: DELETE_MESSAGE id={}", id);
        } else {
            log.warn("Admin action blocked: DELETE_MESSAGE id={}", id);
        }
        return "redirect:/";
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
