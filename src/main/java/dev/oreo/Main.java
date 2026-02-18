package dev.oreo;

import jakarta.servlet.http.HttpSession;
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

    private final GuestMessageRepo repo;
    private final LangConfig lang;

    public Main(GuestMessageRepo repo, LangConfig lang) {
        this.repo = repo;
        this.lang = lang;
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
        return "redirect:/";
    }

    @PostMapping("/admin/login")
    public ResponseEntity<Void> login(@RequestParam String code, HttpSession session) {
        if ("211".equals(code)) {
            session.setAttribute("isAdmin", true);
            return ResponseEntity.noContent().build();
        }
        session.removeAttribute("isAdmin");
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("isAdmin");
        return "redirect:/";
    }

    @PostMapping("/admin/clear")
    public String clearDb(HttpSession session) {
        if (Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            repo.deleteAll();
        }
        return "redirect:/";
    }

    @PostMapping("/admin/delete/{id}")
    public String deleteMessage(@PathVariable String id, HttpSession session) {
        if (Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            repo.deleteById(id);
        }
        return "redirect:/";
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
