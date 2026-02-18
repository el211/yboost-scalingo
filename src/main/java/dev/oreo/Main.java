package dev.oreo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@SpringBootApplication
@RestController
public class Main {

    @GetMapping("/")
    public String home() {
        String msg = System.getenv().getOrDefault("MY_MESSAGE", "MY_MESSAGE not set");
        return "Hello World!<br/>MY_MESSAGE = " + msg;
    }

    @GetMapping("/hello/{name}")
    public String helloName(@PathVariable String name) {
        return "Hello " + name + "!";
    }

    @PostMapping("/hello")
    public RedirectView helloPost(@RequestParam String name) {
        return new RedirectView("/hello/" + name);
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
    @GetMapping("/mongo-uri")
    public String mongoUri() {
        return System.getenv().getOrDefault("SPRING_DATA_MONGODB_URI",
                System.getenv().getOrDefault("SCALINGO_MONGO_URL", "no mongo env var"));
    }

}
