package ng.osun.his.platform.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limiting filter per role and route.
 */
@Component
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    
    // Rate limits per role (requests per minute)
    private static final Map<String, Integer> RATE_LIMITS = Map.of(
        "ADMIN", 100,
        "DOCTOR", 50,
        "NURSE", 30,
        "CASHIER", 40,
        "PHARMACIST", 30,
        "LAB_SCIENTIST", 20,
        "RADIOLOGIST", 20,
        "HMO_OFFICER", 50
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        String remoteAddr = request.getRemoteAddr();
        
        // Check IP allowlist for admin routes
        if (path.startsWith("/admin") || path.startsWith("/actuator")) {
            if (!isAllowedIp(remoteAddr)) {
                log.warn("Blocked admin access from {}", remoteAddr);
                response.setStatus(403);
                return;
            }
        }
        
        // Basic rate limiting (simplified - use Redis in production)
        String key = remoteAddr + ":" + path;
        AtomicInteger count = requestCounts.computeIfAbsent(key, k -> new AtomicInteger(0));
        
        if (count.incrementAndGet() > 100) { // Generic limit
            log.warn("Rate limit exceeded for {}", key);
            response.setStatus(429);
            response.setHeader("Retry-After", "60");
            return;
        }
        
        filterChain.doFilter(request, response);
    }

    private boolean isAllowedIp(String ip) {
        // In production, load from config or database
        return ip.startsWith("10.") || ip.startsWith("192.168.") || ip.equals("127.0.0.1");
    }
}

