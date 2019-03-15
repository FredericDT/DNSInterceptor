package onl.fdt.java.cs.network.DNSPacket.util;

import io.netty.buffer.ByteBuf;
import org.apache.log4j.Logger;

public class DomainByteUtil {

    private static final Logger LOGGER = Logger.getLogger(DomainByteUtil.class);

    public static String domainNameBytesToString(ByteBuf buf, int startIndex) {
        StringBuilder ts = new StringBuilder();
        for (int i = 0; i < buf.readableBytes(); ++i) {
            ts.append(String.format("%02x", buf.getByte(i)));
        }
        LOGGER.debug(ts.toString());
        int i = startIndex;
        StringBuilder s = new StringBuilder();
        int c = -1;
        for (; i < buf.readableBytes(); ++i, --c) {
            byte t = buf.getByte(i);
            if (c == -1) {
                c = (int) t;
                if (c == 0) {
                    return s.substring(1).toString();
                }
                s.append('.');
            } else {
                s.append((char) t);
            }
        }
        throw new IllegalStateException("DNSPacketImpl not complete");
    }

    public static byte[] domainNameStringToBytes(String name) {
        final int l = name.length() + 2;
        final String[] nameArray = name.split("\\.");
        byte[] bytes = new byte[l];
        int j = 0;
        for (final String i : nameArray) {
            bytes[j++] = (byte) i.length();
            for (char k : i.toCharArray()) {
                bytes[j++] = (byte) k;
            }
        }
        bytes[j] = 0x00;
        return bytes;
    }
}
