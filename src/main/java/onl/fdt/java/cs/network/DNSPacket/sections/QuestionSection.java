package onl.fdt.java.cs.network.DNSPacket.sections;

import onl.fdt.java.cs.network.DNSPacket.enums.CLASS;
import onl.fdt.java.cs.network.DNSPacket.enums.TYPE;

public interface QuestionSection {
    int getStartIndex();

    String getDomainName();

    byte[] getDomainNameBytes();

    int getFullByteLength();

    TYPE getqType();

    CLASS getqClass();

    byte[] getFullQuestionSectionBytes();

}
