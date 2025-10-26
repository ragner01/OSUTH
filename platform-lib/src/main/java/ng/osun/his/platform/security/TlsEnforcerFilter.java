package ng.osun.his.platform.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Enforce TLS/HTTPS for production.
 */
@Component
@Slf4j
public class TlsEnforcerFilter extends OncePerRequestFilter {

    @Value("${tls.enforce:true}")
    private boolean enforceTls;

    @Value("${tls.allow-localhost:true}")
    private boolean allowLocalhost;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        if (enforceTls && !isSecure(request)) {
            if (!allowLocalhost || !request.getRemoteAddr().equals("127.0.0.1")) {
                log.warn("Blocked insecure connection from {}", request.getRemoteAddr());
                response.setStatus(426); // Upgrade Required
                response.setHeader("Upgrade", "TLS/1.2");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private boolean isSecure(HttpServletRequest request) {
        // Check for HTTPS
        String protocol = request.getProtocol();
        String scheme = request.getScheme();
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        
        return "https".equals(scheme) || "https".equals(forwardedProto);
    }
}

