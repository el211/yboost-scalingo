package dev.oreo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
    public String home(Model model) {
        List<GuestMessage> messages = repo.findAll()
                .stream()
                .sorted(Comparator.comparing(GuestMessage::getCreatedAt).reversed())
                .toList();

        model.addAttribute("messages", messages);
        model.addAttribute("lang", lang);
        return "index";
    }

    @PostMapping("/guestbook")
    public String add(@RequestParam String name, @RequestParam String message) {
        repo.save(new GuestMessage(name, message));
        return "redirect:/";
    }

    @PostMapping("/admin/clear")
    public String clearDb(@RequestParam String code) {
        if ("211".equals(code)) {
            repo.deleteAll();
        }
        return "redirect:/";
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}