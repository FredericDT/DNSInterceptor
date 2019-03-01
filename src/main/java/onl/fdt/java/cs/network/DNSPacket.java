package onl.fdt.java.cs.network;

import io.netty.buffer.ByteBuf;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DNSPacket {

    private static final Logger LOGGER = Logger.getLogger(DNSPacket.class);

    private ByteBuf buf;

    public DNSPacket(ByteBuf buf) {
        this.buf = buf.copy();
        this.parseQuestionSection();
    }

    private void parseQuestionSection() {
        assert this.getQDCOUNT() == 1 || this.getQDCOUNT() == 0;
        if (this.getQDCOUNT() == 1) {
            this.questionSectionList.add(new QuestionSection(this.buf, 12));
        }
    }

    public ByteBuf getRawByteBuf() {
        return this.buf;
    }

    /**
     * A 16 bit identifier assigned by the program that
     * generates any kind of query.  This identifier is copied
     * the corresponding reply and can be used by the requester
     * to match up replies to outstanding queries.
     *
     * @return short
     */
    public short getID() {
        return this.buf.getShort(0);
    }

    public enum QR {

        QUERY((byte) 0x00),
        RESPONSE((byte) 0x80);

        public final byte value;

        QR(final byte value) {
            this.value = value;
        }

        public static QR fromByte(byte b) {
            return (b & 0x80) == 0x80 ? QR.RESPONSE : QR.QUERY;
        }
    }

    /**
     * A one bit field that specifies whether this message is a
     * query (0), or a response (1).
     *
     * @return QR
     */
    public QR getQRType() {
        return QR.fromByte(this.buf.getByte(2));
    }

    public DNSPacket setQRType(QR qrType) {
        if (qrType == QR.RESPONSE) {
            this.buf.setByte(2, this.buf.getByte(2) | 0x80);
        } else {
            this.buf.setByte(2, this.buf.getByte(2) & 0x7f);
        }
        return this;
    }

    public enum OPCODE {
        QUERY("STANDARD_QUERY", (byte) 0x00),
        IQUERY("INVERSE_QUERY", (byte) 0x08),
        STATUS("SERVER_STATUS_REUEST", (byte) 0x10),
        REVERSED("REVERSED", (byte) 0x78);
        public final String displayName;
        public final byte value;

        OPCODE(final String displayName, final byte value) {
            this.displayName = displayName;
            this.value = value;
        }

        public static OPCODE fromByte(byte b) {
            int t = b & 0x78;
            switch (t) {
                case 0x00:
                    return OPCODE.QUERY;
                case 0x08:
                    return OPCODE.IQUERY;
                case 0x10:
                    return OPCODE.STATUS;
                default:
                    return OPCODE.REVERSED;
            }
        }
    }

    public OPCODE getOPCODE() {
        return OPCODE.fromByte(this.buf.getByte(2));
    }

    public boolean isAuthoritativeAnswer() {
        return (this.buf.getByte(2) & 0x04) == 0x04;
    }

    public DNSPacket setAuthoritativeAnswer(boolean is) {
        this.buf.setByte(2, is ? this.buf.getByte(2) | 0x04 : this.buf.getByte(2) & 0xfb);
        return this;
    }

    public boolean isTruncated() {
        return (this.buf.getByte(2) & 0x02) == 0x02;
    }

    public DNSPacket setTruncated(boolean truncated) {
        this.buf.setByte(2, truncated ? this.buf.getByte(2) | 0x02 : this.buf.getByte(2) & 0xfc);
        return this;
    }

    public boolean isRecursionDesired() {
        return (this.buf.getByte(2) & 0x01) == 0x01;
    }

    public DNSPacket setRecursionDesired(boolean rd) {
        this.buf.setByte(2, rd ? this.buf.getByte(2) | 0x01 : this.buf.getByte(2) & 0xfe);
        return this;
    }

    public boolean isRecursionAvailable() {
        return (this.buf.getByte(3) & 0x80) == 0x80;
    }

    public DNSPacket setRecursionAvailable(boolean ra) {
        this.buf.setByte(3, ra ? this.buf.getByte(3) | 0x80 : this.buf.getByte(3) & 0x7f);
        return this;
    }

    public enum RESPONSE_CODE {
        NO_ERROR("No error", (byte) 0x00),
        FORMAT_ERROR("Format error", (byte) 0x01),
        SERVER_FAILURE("Server failure", (byte) 0x02),
        NAME_ERROR("Name Error", (byte) 0x03),
        NOT_IMPLEMENTED("Not Implemented", (byte) 0x04),
        REFUSED("Refused", (byte) 0x05),
        UNKNOWN("Unknown", (byte) 0xff);
        public final String displayName;
        public final byte value;

        RESPONSE_CODE(final String displayName, final byte value) {
            this.displayName = displayName;
            this.value = value;
        }

        public static RESPONSE_CODE fromByte(final byte b) {
            int t = b & 0x0f;
            switch (t) {
                case 0x00:
                    return RESPONSE_CODE.NO_ERROR;
                case 0x01:
                    return RESPONSE_CODE.FORMAT_ERROR;
                case 0x02:
                    return RESPONSE_CODE.SERVER_FAILURE;
                case 0x03:
                    return RESPONSE_CODE.NAME_ERROR;
                case 0x04:
                    return RESPONSE_CODE.NOT_IMPLEMENTED;
                case 0x05:
                    return RESPONSE_CODE.REFUSED;
                default:
                    return RESPONSE_CODE.UNKNOWN;
            }
        }
    }

    public RESPONSE_CODE getRCODE() {
        return RESPONSE_CODE.fromByte(this.buf.getByte(3));
    }

    public DNSPacket setRCODE(RESPONSE_CODE rcode) {
        this.buf.setByte(3, this.buf.getByte(3) & 0xf0 | rcode.value);
        return this;
    }

    /**
     * an unsigned 16 bit integer specifying the number of
     * entries in the question section.
     *
     * @return short
     */
    public short getQDCOUNT() {
        return this.buf.getShort(4);
    }

    public DNSPacket setQDCOUNT(short qdcount) {
        assert qdcount == 1 || qdcount == 0; // According to BIND
        this.buf.setShort(4, qdcount);
        return this;
    }

    /**
     * an unsigned 16 bit integer specifying the number of
     * resource records in the answer section.
     *
     * @return short
     */
    public short getANCOUNT() {
        return this.buf.getShort(6);
    }

    public DNSPacket setANCOUNT(short ancount) {
        this.buf.setShort(6, ancount);
        return this;
    }

    /**
     * an unsigned 16 bit integer specifying the number of name
     * server resource records in the authority records
     * section.
     *
     * @return short
     */
    public short getNSCOUNT() {
        return this.buf.getShort(8);
    }

    public DNSPacket setNSCOUNT(short nscount) {
        this.buf.setShort(8, nscount);
        return this;
    }

    /**
     * an unsigned 16 bit integer specifying the number of
     * resource records in the additional records section.
     *
     * @return short
     */
    public short getARCOUNT() {
        return this.buf.getShort(10);
    }

    public DNSPacket setARCOUNT(short arcount) {
        this.buf.setShort(10, arcount);
        return this;
    }

    public enum TYPE {
        A("A", 1),
        NS("NS", 2),
        MD("MD", 3),
        MF("MF", 4),
        CNAME("CNAME", 5),
        SOA("SOA", 6),
        MB("MB", 7),
        MG("MG", 8),
        MR("MR", 9),
        NULL("NULL", 10),
        WKS("WKS", 11),
        PTR("PTR", 12),
        HINFO("HINFO", 13),
        MINFO("MINFO", 14),
        MX("MX", 15),
        TXT("TXT", 16),
        // Following are QTYPE
        AXFR("AXFR", 252),
        MAILB("MAILB", 253),
        MAILA("MAILA", 254),
        ALL("ALL", 255);
        public final String displayName;
        public final short value;

        TYPE(final String displayName, final int value) {
            this.displayName = displayName;
            this.value = (short) value;
        }

        public static TYPE fromShort(final short v) {
            switch ((int) v) {
                case 1:
                    return TYPE.A;
                case 2:
                    return TYPE.NS;
                case 3:
                    return TYPE.MD;
                case 4:
                    return TYPE.MF;
                case 5:
                    return TYPE.CNAME;
                case 6:
                    return TYPE.SOA;
                case 7:
                    return TYPE.MB;
                case 8:
                    return TYPE.MG;
                case 9:
                    return TYPE.MR;
                case 10:
                    return TYPE.NULL;
                case 11:
                    return TYPE.WKS;
                case 12:
                    return TYPE.PTR;
                case 13:
                    return TYPE.HINFO;
                case 14:
                    return TYPE.MINFO;
                case 15:
                    return TYPE.MX;
                case 16:
                    return TYPE.TXT;
                case 252:
                    return TYPE.AXFR;
                case 253:
                    return TYPE.MAILB;
                case 254:
                    return TYPE.MAILA;
                case 255:
                    return TYPE.ALL;
                default:
                    throw new IllegalArgumentException("illegal value " + v);
            }
        }

    }

    public enum CLASS {
        IN("Internet", 1),
        CS("CSNET", 2),
        CH("CHAOS", 3),
        HS("Hesiod", 4),
        // Following are QCLASS
        ANY("Any", 255);
        public final String displayName;
        public final short value;

        CLASS(final String displayName, final int value) {
            this.displayName = displayName;
            this.value = (short) value;
        }

        public static CLASS fromShort(final short v) {
            switch ((int) v) {
                case 1:
                    return CLASS.IN;
                case 2:
                    return CLASS.CS;
                case 3:
                    return CLASS.CH;
                case 4:
                    return CLASS.HS;
                case 255:
                    return CLASS.ANY;
                default:
                    throw new IllegalArgumentException("illegal value " + v);
            }
        }

    }

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
        throw new IllegalStateException("DNSPacket not complete");
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

    private List<QuestionSection> questionSectionList = new ArrayList<QuestionSection>();

    public List<QuestionSection> getQuestionSectionList() {
        return this.questionSectionList;
    }

    public static class QuestionSection {

        private final ByteBuf buf;
        public final int startIndex;
        public final String domainName;
        public final byte[] domainNameBytes;
        public final int fullByteLength;
        public final TYPE qType;
        public final CLASS qClass;

        public final byte[] fullQuestionSectionBytes;

        private byte[] buildFullQuestionSectionBytes() {
            byte[] t = new byte[this.domainNameBytes.length + 4];
            int i = 0;
            for (; i < domainNameBytes.length; ++i) {
                t[i] = domainNameBytes[i];
            }
            t[i++] = (byte) (qType.value >> 2);
            t[i++] = (byte) (qType.value & 0xff);
            t[i++] = (byte) (qClass.value >> 2);
            t[i] = (byte) (qClass.value & 0xff);
            return t;
        }

        public QuestionSection(ByteBuf buf, int startIndex) {
            this.buf = buf;
            this.startIndex = startIndex;
            this.domainName = domainNameBytesToString(this.buf, this.startIndex);
            this.domainNameBytes = domainNameStringToBytes(this.domainName);
            this.fullByteLength = this.domainNameBytes.length + 4;
            this.qType = TYPE.fromShort(this.buf.getShort(this.startIndex + this.domainNameBytes.length));
            this.qClass = CLASS.fromShort(this.buf.getShort(this.startIndex + this.domainNameBytes.length + 2));

            this.fullQuestionSectionBytes = buildFullQuestionSectionBytes();
        }

        public QuestionSection(final int startIndex, final String domainName, final TYPE qType, final CLASS qClass) {
            this.buf = null;
            this.startIndex = startIndex;
            this.domainName = domainName;
            this.domainNameBytes = domainNameStringToBytes(this.domainName);
            this.fullByteLength = this.domainNameBytes.length + 4;
            this.qType = qType;
            this.qClass = qClass;

            this.fullQuestionSectionBytes = buildFullQuestionSectionBytes();

        }
    }

    public static class ResourceRecord {

        private abstract class RDATA {
            public abstract byte[] getFullRDATABytes();
        }

        public class RDATA_A extends RDATA {

            private final byte[] address;

            public RDATA_A(final byte[] address) {
                this.address = address;
            }

            @Override
            public byte[] getFullRDATABytes() {
                return this.address;
            }
        }

        public class RDATA_SOA extends RDATA {

            //TODO

            private RDATA_SOA() {
            }

            @Override
            public byte[] getFullRDATABytes() {
                return null;
            }
        }

        private final ByteBuf buf;
        private final int startIndex;
        private final short name;
        private final TYPE type;
        private final CLASS rDClass;
        private final int ttl;
        private final short rDLength;
        private final int nameType;
        //TODO: implement every RDATA type
        private final byte[] rDATA;

        private byte[] readRDATABytesForBufSource() {
            byte[] t = new byte[rDLength];
            for (int i = 0; i < rDLength; ++i) {
                t[i] = this.buf.getByte(startIndex + 10 + this.nameType + i);
            }
            return t;
        }

        public ResourceRecord(final ByteBuf buf, final int startIndex) {
            this.buf = buf;
            this.startIndex = startIndex;
            this.name = this.buf.getByte(startIndex) == (byte) 0xc0 ? (short) (this.buf.getShort(startIndex) & 0x3fff) : this.buf.getByte(startIndex);
            this.nameType = this.buf.getByte(startIndex) == (byte) 0xc0 ? 2 : 1;
            this.type = TYPE.fromShort(this.buf.getShort(startIndex + this.nameType));
            this.rDClass = CLASS.fromShort(this.buf.getShort(startIndex + this.nameType + 2));
            this.ttl = this.buf.getInt(startIndex + this.nameType + 4);
            this.rDLength = this.buf.getShort(startIndex + this.nameType + 8);
            this.rDATA = readRDATABytesForBufSource();
        }
    }


}