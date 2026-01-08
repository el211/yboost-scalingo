package dev.oreo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@SpringBootApplication
@RestController
public class Main {

    // 1) / affiche Hello World + variable d'env
    @GetMapping("/")
    public String home() {
        String msg = System.getenv().getOrDefault("MY_MESSAGE", "MY_MESSAGE not set");
        return "Hello World!<br/>MY_MESSAGE = " + msg;
    }

    // 2) Route dynamique /hello/{name}
    @GetMapping("/hello/{name}")
    public String helloName(@PathVariable String name) {
        return "Hello " + name + "!";
    }

    // 3) POST (ex: via un formulaire) -> redirige vers /hello/{name}
    @PostMapping("/hello")
    public RedirectView helloPost(@RequestParam String name) {
        return new RedirectView("/hello/" + name);
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
