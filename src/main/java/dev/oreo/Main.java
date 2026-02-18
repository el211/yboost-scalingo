package dev.oreo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@SpringBootApplication
@Controller
public class Main {

    private final GuestMessageRepo repo;

    public Main(GuestMessageRepo repo) {
        this.repo = repo;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<GuestMessage> messages = repo.findAll()
                .stream()
                .sorted(Comparator.comparing(GuestMessage::getCreatedAt).reversed())
                .toList();

        model.addAttribute("messages", messages);
        return "index";
    }

    @PostMapping("/guestbook")
    public String add(@RequestParam String name, @RequestParam String message) {
        repo.save(new GuestMessage(name, message));
        return "redirect:/";
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}