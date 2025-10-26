package ng.osun.his.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway routing configuration.
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("core-emr", r -> r
                .path("/api/emr/**")
                .uri("http://localhost:8082")
            )
            .route("appointments", r -> r
                .path("/api/appointments/**")
                .uri("http://localhost:8083")
            )
            .route("orders", r -> r
                .path("/api/orders/**")
                .uri("http://localhost:8084")
            )
            .route("pharmacy", r -> r
                .path("/api/pharmacy/**")
                .uri("http://localhost:8085")
            )
            .route("billing", r -> r
                .path("/api/billing/**")
                .uri("http://localhost:8086")
            )
            .build();
    }
}

