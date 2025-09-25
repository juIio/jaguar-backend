package cc.jagind.jaguar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class JaguarApplication {

    private static final String VERSION = "0.0.1-SNAPSHOT";

    public static void main(String[] args) {
        SpringApplication.run(JaguarApplication.class, args);
    }

    @Component
    static class StartupLogger {

        @Value("${server.port}")
        private String serverPort;

        @Value("${frontend.url}")
        private String frontendUrl;

        @EventListener(ApplicationReadyEvent.class)
        public void logInfo() {
            System.out.println("\n***********************************");
            System.out.println("Running JAGUAR v" + VERSION);
            System.out.println("Listening on http://localhost:" + serverPort + "/");
            System.out.println("Frontend URL " + frontendUrl);
            System.out.println("***********************************");
        }
    }
}
