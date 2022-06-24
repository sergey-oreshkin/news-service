package example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * REST сервер на spring boot с jwt авторизацией
 */
@SpringBootApplication
public class NewsServiceJwtExample {
    public static void main(String[] args) {
        SpringApplication.run(NewsServiceJwtExample.class, args);
    }
}
