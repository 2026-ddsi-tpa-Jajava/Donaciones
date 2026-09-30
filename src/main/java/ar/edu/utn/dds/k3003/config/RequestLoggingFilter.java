package ar.edu.utn.dds.k3003.config;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    private final InstanceInfo instanceInfo;

    public RequestLoggingFilter(InstanceInfo instanceInfo) {
        this.instanceInfo = instanceInfo;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().substring(0, 8);
        }
        String requestId = UUID.randomUUID().toString().substring(0, 8);

        MDC.put("traceId", traceId);
        MDC.put("instanceId", instanceInfo.getInstanceId());
        MDC.put("requestId", requestId);

        long start = System.currentTimeMillis();
        log.info("--> {} {}", request.getMethod(), request.getRequestURI());

        try {
            chain.doFilter(request, response);
        } finally {
            long took = System.currentTimeMillis() - start;
            log.info("<-- {} {} status={} took={}ms", request.getMethod(), request.getRequestURI(), response.getStatus(), took);
            MDC.clear();
        }
    }
}