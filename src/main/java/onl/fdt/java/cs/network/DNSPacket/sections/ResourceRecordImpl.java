package onl.fdt.java.cs.network.DNSPacket.sections;

import io.netty.buffer.ByteBuf;
import onl.fdt.java.cs.network.DNSPacket.enums.CLASS;
import onl.fdt.java.cs.network.DNSPacket.enums.TYPE;

public class ResourceRecordImpl implements ResourceRecord {

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

    public int getFullByteLength() {
        return fullByteLength;
    }

    public byte[] getFullBytes() {
        return fullBytes;
    }

    private final int fullByteLength;
    private final byte[] fullBytes;

    private byte[] readRDATABytesForBufSource() {
        byte[] t = new byte[rDLength];
        for (int i = 0; i < rDLength; ++i) {
            t[i] = this.buf.getByte(startIndex + 10 + this.nameType + i);
        }
        return t;
    }

    public ResourceRecordImpl(final ByteBuf buf, final int startIndex) {
        this.buf = buf;
        this.startIndex = startIndex;
        this.name = this.buf.getByte(startIndex) == (byte) 0xc0 ? (short) (this.buf.getShort(startIndex) & 0x3fff) : this.buf.getByte(startIndex);
        this.nameType = this.buf.getByte(startIndex) == (byte) 0xc0 ? 2 : 1;
        this.type = TYPE.fromShort(this.buf.getShort(startIndex + this.nameType));
        this.rDClass = CLASS.fromShort(this.buf.getShort(startIndex + this.nameType + 2));
        this.ttl = this.buf.getInt(startIndex + this.nameType + 4);
        this.rDLength = this.buf.getShort(startIndex + this.nameType + 8);
        this.rDATA = readRDATABytesForBufSource();
        this.fullByteLength = this.nameType + 10 + this.rDATA.length;

        this.fullBytes = buildFullBytes();
    }

    private byte[] buildFullBytes() {
        byte[] t = new byte[this.fullByteLength];
        int i = 0;
        t[i++] = (byte) ((this.name & 0xff00) >> 4);
        if (this.nameType == 2) {
            t[i++] = (byte) (this.name & 0xff);
        }
        t[i++] = (byte) ((this.type.value & 0xff00) >> 4);
        t[i++] = (byte) (this.type.value & 0xff);
        t[i++] = (byte) ((this.rDClass.value & 0xff00) >> 4);
        t[i++] = (byte) (this.rDClass.value & 0xff);
        t[i++] = (byte) ((this.ttl & 0xff000000) >> 12);
        t[i++] = (byte) ((this.ttl & 0xff0000) >> 8);
        t[i++] = (byte) ((this.ttl & 0xff00) >> 4);
        t[i++] = (byte) (this.ttl & 0xff);
        t[i++] = (byte) ((this.rDLength & 0xff00) >> 4);
        t[i++] = (byte) (this.rDLength & 0xff);
        for (int j = 0; i < this.fullByteLength; ++i, ++j) {
            t[i] = this.rDATA[j];
        }
        return t;
    }

    public ResourceRecordImpl(final int startIndex,
                              final short name,
                              final TYPE type,
                              final CLASS rDClass,
                              final int ttl,
                              final short rDLength,
                              final byte[] rDATA) {
        this.buf = null;
        this.startIndex = startIndex;
        this.name = name;
        this.nameType = (this.name & 0xb0) == 0xb0 ? 2 : 1;
        this.type = type;
        this.rDClass = rDClass;
        this.ttl = ttl;
        this.rDLength = rDLength;
        this.rDATA = rDATA;
        this.fullByteLength = this.nameType + 10 + this.rDATA.length;

        this.fullBytes = buildFullBytes();
    }
}
