package onl.fdt.java.cs.network.DNSPacket.enums;

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