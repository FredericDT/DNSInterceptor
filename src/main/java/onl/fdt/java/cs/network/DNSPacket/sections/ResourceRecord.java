package onl.fdt.java.cs.network.DNSPacket.sections;

public interface ResourceRecord {
    int getFullByteLength();

    byte[] getFullBytes();
}
