package onl.fdt.java.cs.network.DNSPacket.enums;

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