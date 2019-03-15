package onl.fdt.java.cs.network.DNSPacket.enums;

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