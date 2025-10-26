package ng.osun.his.interop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Interoperability service for FHIR-style read APIs.
 */
@SpringBootApplication
public class InteropApplication {
    public static void main(String[] args) {
        SpringApplication.run(InteropApplication.class, args);
    }
}
