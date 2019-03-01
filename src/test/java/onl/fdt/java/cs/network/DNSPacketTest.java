package onl.fdt.java.cs.network;

import io.netty.buffer.Unpooled;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public class DNSPacketTest {

    private static final Logger LOGGER = Logger.getLogger(DNSPacketTest.class);

    private static final byte[] bytes_of_www_bupt_edu_cn = {
            0x03, 0x77,
            0x77, 0x77,
            0x04, 0x62,
            0x75, 0x70,
            0x74, 0x03,
            0x65, 0x64,
            0x75, 0x02,
            0x63, 0x6e,
            0x00
    };

    private static final String string_of_www_bupt_edu_cn = "www.bupt.edu.cn";

    private static final int[] full_dns_packet_int = {
            0xd8, 0x92, 0x81, 0x80,
            0x00, 0x01, 0x00, 0x02,
            0x00, 0x00, 0x00, 0x01,
            0x03, 0x77, 0x77, 0x77,
            0x04, 0x62, 0x75, 0x70,
            0x74, 0x03, 0x65, 0x64,
            0x75, 0x02, 0x63, 0x6e,
            0x00, 0x00, 0x01, 0x00,
            0x01, 0xc0, 0x0c, 0x00,
            0x05, 0x00, 0x01, 0x00,
            0x00, 0x05, 0xcf, 0x00,
            0x05, 0x02, 0x76, 0x6e,
            0xc0, 0x10, 0xc0, 0x2d,
            0x00, 0x01, 0x00, 0x01,
            0x00, 0x00, 0x00, 0x6a,
            0x00, 0x04, 0x0a, 0x03,
            0x09, 0xa1, 0x00, 0x00,
            0x29, 0x10, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00
    };

    private static byte[] full_dns_packet_byte;

    static {
        full_dns_packet_byte = new byte[full_dns_packet_int.length];
        for (int i = 0; i < full_dns_packet_int.length; ++i) {
            full_dns_packet_byte[i] = (byte) full_dns_packet_int[i];
        }
    }

    @Test
    public void testDomainNameBytesToString() {

        String r = DNSPacket.domainNameBytesToString(Unpooled.wrappedBuffer(bytes_of_www_bupt_edu_cn), 0);
        Assert.assertEquals(string_of_www_bupt_edu_cn, r);
    }

    @Test
    public void testDomainNameStringToBytes() {
        byte[] i = DNSPacket.domainNameStringToBytes(string_of_www_bupt_edu_cn);
        Assert.assertArrayEquals(bytes_of_www_bupt_edu_cn, i);
    }

    @Test
    public void testDNSPacket() {
        DNSPacket dnsPacket = new DNSPacket(Unpooled.wrappedBuffer(full_dns_packet_byte));
        LOGGER.debug(String.format("id: %d", dnsPacket.getID()));
        Assert.assertEquals((short) 0xd892, dnsPacket.getID());
        LOGGER.debug(String.format("rd: %b", dnsPacket.isRecursionDesired()));
        Assert.assertEquals(true, dnsPacket.isRecursionDesired());
        String domainName = dnsPacket.getQuestionSectionList().get(0).domainName;
        LOGGER.debug(String.format("domain: %s", domainName));
        Assert.assertEquals("www.bupt.edu.cn", domainName);
    }
}
