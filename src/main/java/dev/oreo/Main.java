package dev.oreo;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Optional;

@SpringBootApplication
@EnableConfigurationProperties
@Controller
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BOLD_RED = "\u001B[1;31m";
    private static final String ANSI_BOLD_YELLOW = "\u001B[1;33m";

    private final GuestMessageRepo repo;
    private final LangConfig lang;

    public Main(GuestMessageRepo repo, LangConfig lang) {
        this.repo = repo;
        this.lang = lang;
    }

    private static String redBold(String msg) {
        return ANSI_BOLD_RED + msg + ANSI_RESET;
    }

    private static String yellowBold(String msg) {
        return ANSI_BOLD_YELLOW + msg + ANSI_RESET;
    }

    private void logUserAction(String template, Object... args) {
        log.info(redBold("[USER] " + template), args);
    }

    private void logAdminAction(String template, Object... args) {
        log.warn(redBold("[ADMIN] " + template), args);
    }

    private void logAdminBlocked(String template, Object... args) {
        log.warn(yellowBold("[ADMIN BLOCKED] " + template), args);
    }

    private static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @PostConstruct
    public void bootTraces() {
        log.info(redBold("=== APP BOOT ==="));
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
    public String home(Model model, HttpSession session, HttpServletRequest request) {
        List<GuestMessage> messages = repo.findAll()
                .stream()
                .sorted(Comparator.comparing(GuestMessage::getCreatedAt).reversed())
                .toList();

        boolean isAdmin = Boolean.TRUE.equals(session.getAttribute("isAdmin"));

        model.addAttribute("messages", messages);
        model.addAttribute("lang", lang);
        model.addAttribute("isAdmin", isAdmin);

        logUserAction("HOME_VIEW ip={} admin={}", clientIp(request), isAdmin);
        return "index";
    }

    @PostMapping("/guestbook")
    public String add(@RequestParam String name, @RequestParam String message, HttpServletRequest request) {
        repo.save(new GuestMessage(name, message));
        logUserAction("POST_MESSAGE ip={} name='{}' chars={}", clientIp(request), name, message == null ? 0 : message.length());
        return "redirect:/";
    }

    @PostMapping("/admin/login")
    public ResponseEntity<Void> login(@RequestParam String code, HttpSession session, HttpServletRequest request) {
        String expected = lang.getAdmin() != null ? lang.getAdmin().getCode() : null;

        if (expected != null && !expected.isBlank() && expected.equals(code)) {
            session.setAttribute("isAdmin", true);
            logAdminAction("LOGIN_SUCCESS ip={}", clientIp(request));
            return ResponseEntity.noContent().build();
        }

        session.removeAttribute("isAdmin");
        logAdminAction("LOGIN_FAIL ip={}", clientIp(request));
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/admin/logout")
    public String logout(HttpSession session, HttpServletRequest request) {
        session.removeAttribute("isAdmin");
        logAdminAction("LOGOUT ip={}", clientIp(request));
        return "redirect:/";
    }

    @PostMapping("/admin/clear")
    public String clearDb(HttpSession session, HttpServletRequest request) {
        if (Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            repo.deleteAll();
            logAdminAction("CLEAR_DB ip={}", clientIp(request));
        } else {
            logAdminBlocked("CLEAR_DB ip={}", clientIp(request));
        }
        return "redirect:/";
    }

    @PostMapping("/admin/delete/{id}")
    public String deleteMessage(@PathVariable String id, HttpSession session, HttpServletRequest request) {
        if (Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            repo.deleteById(id);
            logAdminAction("DELETE_MESSAGE ip={} id={}", clientIp(request), id);
        } else {
            logAdminBlocked("DELETE_MESSAGE ip={} id={}", clientIp(request), id);
        }
        return "redirect:/";
    }

    @PostMapping("/admin/update/{id}")
    public ResponseEntity<Void> updateMessage(
            @PathVariable String id,
            @RequestParam String message,
            HttpSession session,
            HttpServletRequest request
    ) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            logAdminBlocked("UPDATE_MESSAGE ip={} id={}", clientIp(request), id);
            return ResponseEntity.status(401).build();
        }

        String trimmed = message == null ? "" : message.trim();
        if (trimmed.isBlank()) {
            logAdminAction("UPDATE_MESSAGE_BAD_REQUEST ip={} id={} reason=blank", clientIp(request), id);
            return ResponseEntity.badRequest().build();
        }

        Optional<GuestMessage> opt = repo.findById(id);
        if (opt.isEmpty()) {
            logAdminAction("UPDATE_MESSAGE_NOT_FOUND ip={} id={}", clientIp(request), id);
            return ResponseEntity.notFound().build();
        }

        GuestMessage m = opt.get();
        m.setMessage(trimmed);
        repo.save(m);

        logAdminAction("UPDATE_MESSAGE ip={} id={} chars={}", clientIp(request), id, trimmed.length());
        return ResponseEntity.noContent().build();
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
