package onl.fdt.java.cs.network.DNSPacket.enums;

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