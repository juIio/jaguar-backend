package cc.jagind.jaguar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JaguarApplication {

    private static final String VERSION = "0.0.1-SNAPSHOT";

    public static void main(String[] args) {
        SpringApplication.run(JaguarApplication.class, args);

        System.out.println("\n***********************************");
        System.out.println("Running JAGUAR v" + VERSION);
        System.out.println("Listening on http://localhost:8080/");
        System.out.println("***********************************");
    }

}
