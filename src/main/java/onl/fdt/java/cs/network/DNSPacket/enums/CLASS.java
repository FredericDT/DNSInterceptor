package onl.fdt.java.cs.network.DNSPacket.enums;

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
