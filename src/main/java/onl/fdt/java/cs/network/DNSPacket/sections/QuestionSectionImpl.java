package onl.fdt.java.cs.network.DNSPacket.sections;

import io.netty.buffer.ByteBuf;
import onl.fdt.java.cs.network.DNSPacket.enums.CLASS;
import onl.fdt.java.cs.network.DNSPacket.enums.TYPE;
import onl.fdt.java.cs.network.DNSPacket.util.DomainByteUtil;

public class QuestionSectionImpl implements QuestionSection {

    private final ByteBuf buf;
    private final int startIndex;

    public String getDomainName() {
        return domainName;
    }

    public byte[] getDomainNameBytes() {
        return domainNameBytes;
    }

    public int getFullByteLength() {
        return fullByteLength;
    }

    public TYPE getqType() {
        return qType;
    }

    public CLASS getqClass() {
        return qClass;
    }

    private final String domainName;
    private final byte[] domainNameBytes;
    private final int fullByteLength;
    private final TYPE qType;
    private final CLASS qClass;

    public byte[] getFullQuestionSectionBytes() {
        return fullQuestionSectionBytes;
    }

    private final byte[] fullQuestionSectionBytes;

    public int getStartIndex() {
        return startIndex;
    }

    private byte[] buildFullQuestionSectionBytes() {
        byte[] t = new byte[this.domainNameBytes.length + 4];
        int i = 0;
        for (; i < domainNameBytes.length; ++i) {
            t[i] = domainNameBytes[i];
        }
        t[i++] = (byte) (qType.value >> 2);
        t[i++] = (byte) (qType.value & 0xff);
        t[i++] = (byte) (qClass.value >> 2);
        t[i] = (byte) (qClass.value & 0xff);
        return t;
    }

    public QuestionSectionImpl(ByteBuf buf, int startIndex) {
        this.buf = buf;
        this.startIndex = startIndex;
        this.domainName = DomainByteUtil.domainNameBytesToString(this.buf, this.startIndex);
        this.domainNameBytes = DomainByteUtil.domainNameStringToBytes(this.domainName);
        this.fullByteLength = this.domainNameBytes.length + 4;
        this.qType = TYPE.fromShort(this.buf.getShort(this.startIndex + this.domainNameBytes.length));
        this.qClass = CLASS.fromShort(this.buf.getShort(this.startIndex + this.domainNameBytes.length + 2));

        this.fullQuestionSectionBytes = buildFullQuestionSectionBytes();
    }

    public QuestionSectionImpl(final int startIndex, final String domainName, final TYPE qType, final CLASS qClass) {
        this.buf = null;
        this.startIndex = startIndex;
        this.domainName = domainName;
        this.domainNameBytes = DomainByteUtil.domainNameStringToBytes(this.domainName);
        this.fullByteLength = this.domainNameBytes.length + 4;
        this.qType = qType;
        this.qClass = qClass;

        this.fullQuestionSectionBytes = buildFullQuestionSectionBytes();

    }
}