package onl.fdt.java.cs.network.DNSPacket;

import io.netty.buffer.ByteBuf;
import onl.fdt.java.cs.network.DNSPacket.enums.OPCODE;
import onl.fdt.java.cs.network.DNSPacket.enums.QR;
import onl.fdt.java.cs.network.DNSPacket.enums.RESPONSE_CODE;
import onl.fdt.java.cs.network.DNSPacket.sections.QuestionSection;
import onl.fdt.java.cs.network.DNSPacket.sections.ResourceRecord;

import java.util.List;

public interface DNSPacket {

    ByteBuf getRawByteBuf();

    short getID();

    QR getQRType();

    DNSPacket setQRType(QR qrType);

    OPCODE getOPCODE();

    boolean isAuthoritativeAnswer();

    DNSPacket setAuthoritativeAnswer(boolean is);

    boolean isTruncated();

    DNSPacket setTruncated(boolean truncated);

    boolean isRecursionDesired();

    DNSPacket setRecursionDesired(boolean rd);

    boolean isRecursionAvailable();

    DNSPacket setRecursionAvailable(boolean ra);

    RESPONSE_CODE getRCODE();

    DNSPacketImpl setRCODE(RESPONSE_CODE rcode);

    short getQDCOUNT();

    DNSPacket setQDCOUNT(short qdcount);

    short getANCOUNT();

    DNSPacket setANCOUNT(short ancount);

    short getNSCOUNT();

    DNSPacket setNSCOUNT(short nscount);

    short getARCOUNT();

    DNSPacket setARCOUNT(short arcount);

    List<? extends QuestionSection> getQuestionSectionList();

    List<? extends ResourceRecord> getAnswerSectionList();

    List<? extends ResourceRecord> getAuthoritySectionList();

    List<? extends ResourceRecord> getAdditionalSectionList();
}
