package onl.fdt.java.cs.network;

import org.apache.log4j.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Command(description = "UDP DNS interceptor",
        name = "DNSRelay",
        mixinStandardHelpOptions = true,
        version = "1.1-SNAPSHOT")
public class Config {

    private static final Logger LOGGER = Logger.getLogger(Config.class);

    public static String getTargetDnsServerAddress() {
        return TARGET_DNS_SERVER_ADDRESS;
    }

    public static int getTargetDnsServerPort() {
        return TARGET_DNS_SERVER_PORT;
    }

    public static String getListenAddress() {
        return LISTEN_ADDRESS;
    }

    public static int getListenPort() {
        return LISTEN_PORT;
    }

    private static Map<String, byte[]> interceptDomainIPMap = new HashMap<String, byte[]>();

    public static Map<String, byte[]> getInterceptDomainIPMap() {
        return interceptDomainIPMap;
    }

    public static final byte[] BLOCK_DOMAIN_ADDRESS = {0, 0, 0, 0};

    /**
     * initialize intercept map from file
     */
    public static void initConfig() {
        if (FILE == null) {
            LOGGER.warn("--file parameter not set");
            return;
        }

        if (!FILE.exists()) {
            LOGGER.warn("file " + FILE.getAbsolutePath() + " not found");
            return;
        }

        try (Stream<String> stream = Files.lines(FILE.toPath()).parallel()) {
            stream.forEach(o -> {
                o = o.trim();
                String[] p = o.split("\\s+");
                assert p.length == 2;
                String[] ip = p[0].split("\\.");
                assert ip.length == 4;
                byte[] ipBytes = new byte[ip.length];
                for (int i = 0; i < ip.length; ++i) {
                    ipBytes[i] = (byte) Short.parseShort(ip[i]);
                }
                interceptDomainIPMap.put(p[1], ipBytes);
            });
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IllegalArgumentException("file " + FILE.getAbsolutePath() + " read error");
        }
    }

    /**
     * upstream dns server udp address
     */
    @Option(names = {"--target-address"})
    private static String TARGET_DNS_SERVER_ADDRESS = "8.8.8.8";

    /**
     * upstream dns server udp port
     */
    @Option(names = {"--target-port"})
    private static int TARGET_DNS_SERVER_PORT = 53;

    /**
     * listen udp address
     */
    @Option(names = {"--bind-address"})
    private static String LISTEN_ADDRESS = "127.0.0.1";

    /**
     * listen udp port
     */
    @Option(names = {"--bind-port"})
    private static int LISTEN_PORT = 5153;

    /**
     * intercept config file path
     */
    @Option(names = {"--file"})
    private static File FILE;

}
