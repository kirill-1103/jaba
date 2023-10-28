package sovcombank.jabka.newsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"sovcombank.jabka"})
public class NewsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewsServiceApplication.class, args);
    }

}
