package ar.edu.utn.dds.k3003.config;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class InstanceInfo {
    private final String instanceId;

    public InstanceInfo(Environment env) {
        String id = firstNonBlank(System.getenv("RENDER_INSTANCE_ID"), System.getenv("INSTANCE_NAME"));
        if (id == null) {
            id = resolveHostname();
        }
        String port = env.getProperty("server.port", "8080");
        this.instanceId = id + ":" + port;
    }

    public String getInstanceId() {
        return instanceId;
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) return a;
        if (b != null && !b.isBlank()) return b;
        return null;
    }

    private static String resolveHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown-host";
        }
    }
}