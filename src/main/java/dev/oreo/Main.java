package dev.oreo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Comparator;
import java.util.List;

@SpringBootApplication
@RestController
public class Main {

    private final GuestMessageRepo repo;

    public Main(GuestMessageRepo repo) {
        this.repo = repo;
    }

    @GetMapping("/")
    public String home() {
        String myMsg = System.getenv().getOrDefault("MY_MESSAGE", "MY_MESSAGE not set");

        List<GuestMessage> messages = repo.findAll()
                .stream()
                .sorted(Comparator.comparing(GuestMessage::getCreatedAt).reversed())
                .toList();

        StringBuilder html = new StringBuilder();
        html.append("<h1>Hello World</h1>");
        html.append("<p>MY_MESSAGE = ").append(myMsg).append("</p>");

        html.append("""
            <h2>Guestbook</h2>
            <form method="post" action="/guestbook">
              <input name="name" placeholder="Your name" required />
              <input name="message" placeholder="Your message" required />
              <button type="submit">Send</button>
            </form>
            <hr/>
        """);

        if (messages.isEmpty()) {
            html.append("<p><i>No messages yet.</i></p>");
        } else {
            html.append("<ul>");
            for (GuestMessage m : messages) {
                html.append("<li><b>")
                        .append(escape(m.getName()))
                        .append("</b> : ")
                        .append(escape(m.getMessage()))
                        .append(" <small>(").append(m.getCreatedAt()).append(")</small>")
                        .append("</li>");
            }
            html.append("</ul>");
        }

        return html.toString();
    }

    @PostMapping("/guestbook")
    public RedirectView add(@RequestParam String name, @RequestParam String message) {
        repo.save(new GuestMessage(name, message));
        return new RedirectView("/");
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
