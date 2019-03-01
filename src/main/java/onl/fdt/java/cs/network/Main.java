package onl.fdt.java.cs.network;

import onl.fdt.java.cs.network.Listener.DNSRelayServer;
import org.apache.log4j.Logger;
import picocli.CommandLine;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        new CommandLine(Config.class).parse(args);

        Config.initConfig();

        DNSRelayServer s = new DNSRelayServer();
        try {
            s.run();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

}
