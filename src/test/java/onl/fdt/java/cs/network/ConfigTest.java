package onl.fdt.java.cs.network;

import org.junit.Assert;
import org.junit.Test;
import picocli.CommandLine;

public class ConfigTest {
    @Test
    public void configTest() {
        String[] args = {"--target-address", "8.8.4.4", "--target-port", "5353", "--bind-address", "0.0.0.0", "--bind-port", "5653", "--file", "test"};
        new CommandLine(Config.class).parse(args);
        Assert.assertEquals(Config.getListenAddress(), "0.0.0.0");
        Assert.assertEquals(Config.getListenPort(), 5653);
        Assert.assertEquals(Config.getTargetDnsServerAddress(), "8.8.4.4");
        Assert.assertEquals(Config.getTargetDnsServerPort(), 5353);
    }
}
