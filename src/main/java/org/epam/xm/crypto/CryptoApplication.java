package org.epam.xm.crypto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the CryptoApplication.
 * <p>
 * This class bootstraps the Spring Boot application and sets up additional configurations.
 * The application is focused on cryptocurrency related operations.
 */
@EnableScheduling          // Enables support for scheduled tasks
@EnableJpaRepositories     // Enables JPA repositories. This is typically inferred automatically but can be specified for clarity.
@SpringBootApplication(   // Indicates a configuration class that declares one or more @Bean methods and also triggers auto-configuration and component scanning.
        scanBasePackages = { "org.epam.xm.crypto" }  // Specifies base packages for component scanning.
)
public class CryptoApplication {

    /**
     * Main method serving as an entry point for the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Launch the Spring application.
        SpringApplication.run(CryptoApplication.class, args);
    }
}
