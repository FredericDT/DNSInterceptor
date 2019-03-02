package onl.fdt.java.cs.network;

import onl.fdt.java.cs.network.Listener.DNSRelayServer;
import org.apache.log4j.Logger;
import picocli.CommandLine;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        CommandLine commandLine = new CommandLine(Config.class);
        commandLine.parse(args);

        if (commandLine.isUsageHelpRequested()) {
            commandLine.usage(System.out);
            return;
        } else if (commandLine.isVersionHelpRequested()) {
            commandLine.printVersionHelp(System.out);
            return;
        }

        Config.initConfig();

        DNSRelayServer s = new DNSRelayServer();
        try {
            s.run();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

}
