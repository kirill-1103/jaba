package sovcombank.jabka.studyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "sovcombank.jabka")
public class StudyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyServiceApplication.class, args);
    }

}
