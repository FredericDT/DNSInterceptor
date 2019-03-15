package onl.fdt.java.cs.network.DNSPacket.sections.resourcerecord.RDATA;

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
