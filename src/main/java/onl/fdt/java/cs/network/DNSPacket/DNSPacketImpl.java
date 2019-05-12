package onl.fdt.java.cs.network.DNSPacket;

import io.netty.buffer.ByteBuf;
import onl.fdt.java.cs.network.DNSPacket.enums.*;
import onl.fdt.java.cs.network.DNSPacket.sections.QuestionSectionImpl;
import onl.fdt.java.cs.network.DNSPacket.sections.ResourceRecordImpl;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DNSPacketImpl implements DNSPacket {

    private static final Logger LOGGER = Logger.getLogger(DNSPacketImpl.class);

    private ByteBuf buf;

    /**
     * Parse from ray bytes
     * @param buf
     */
    public DNSPacketImpl(ByteBuf buf) {
        this.buf = buf.copy();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < this.buf.readableBytes(); ++i) {
            s.append(String.format("%02x", this.buf.getByte(i)));
        }
        LOGGER.debug("parsing packet: " + s.toString());
        this.parseSections();
    }

    private void parseSections() {
        LOGGER.debug("QRCOUNT: " + this.getQDCOUNT());
        assert this.getQDCOUNT() == (short) 1 || this.getQDCOUNT() == (short) 0;
        if (this.getQDCOUNT() == 1) {
            this.questionSectionList.add(new QuestionSectionImpl(this.buf, 12));
        }
        int ancont = this.getANCOUNT();
        int nscount = this.getNSCOUNT();
        int arcount = this.getARCOUNT();
        int i = 12 + (this.questionSectionList.isEmpty() ? 0 : this.questionSectionList.get(0).getFullByteLength());
        for (int j = 0; j < ancont; ++j) {
            ResourceRecordImpl rr = new ResourceRecordImpl(this.buf, i);
            this.answerSectionList.add(rr);
            i += rr.getFullByteLength();
        }
        for (int j = 0; j < nscount; ++j) {
            ResourceRecordImpl rr = new ResourceRecordImpl(this.buf, i);
            this.authoritySectionList.add(rr);
            i += rr.getFullByteLength();
        }
        for (int j = 0; j < arcount; ++j) {
            try {
                ResourceRecordImpl rr = new ResourceRecordImpl(this.buf, i);
                this.additionalSectionList.add(rr);
                i += rr.getFullByteLength();
            } catch (Exception e) {
                LOGGER.warn("Parse AdditionalSection Error, dropped", e);
                this.setARCOUNT((short) 0);
            }
        }
    }

    public ByteBuf getRawByteBuf() {
        return this.buf;
    }

    /**
     * A 16 bit identifier assigned by the program that
     * generates any kind of query.  This identifier is copied
     * the corresponding reply and can be used by the requester
     * to match up replies to outstanding queries.
     *
     * @return short
     */
    public short getID() {
        return this.buf.getShort(0);
    }

    /**
     * A one bit field that specifies whether this message is a
     * query (0), or a response (1).
     *
     * @return QR
     */
    public QR getQRType() {
        return QR.fromByte(this.buf.getByte(2));
    }

    public DNSPacketImpl setQRType(QR qrType) {
        if (qrType == QR.RESPONSE) {
            this.buf.setByte(2, this.buf.getByte(2) | 0x80);
        } else {
            this.buf.setByte(2, this.buf.getByte(2) & 0x7f);
        }
        return this;
    }

    public OPCODE getOPCODE() {
        return OPCODE.fromByte(this.buf.getByte(2));
    }

    public boolean isAuthoritativeAnswer() {
        return (this.buf.getByte(2) & 0x04) == 0x04;
    }

    public DNSPacketImpl setAuthoritativeAnswer(boolean is) {
        this.buf.setByte(2, is ? this.buf.getByte(2) | 0x04 : this.buf.getByte(2) & 0xfb);
        return this;
    }

    public boolean isTruncated() {
        return (this.buf.getByte(2) & 0x02) == 0x02;
    }

    public DNSPacketImpl setTruncated(boolean truncated) {
        this.buf.setByte(2, truncated ? this.buf.getByte(2) | 0x02 : this.buf.getByte(2) & 0xfc);
        return this;
    }

    public boolean isRecursionDesired() {
        return (this.buf.getByte(2) & 0x01) == 0x01;
    }

    public DNSPacketImpl setRecursionDesired(boolean rd) {
        this.buf.setByte(2, rd ? this.buf.getByte(2) | 0x01 : this.buf.getByte(2) & 0xfe);
        return this;
    }

    public boolean isRecursionAvailable() {
        return (this.buf.getByte(3) & 0x80) == 0x80;
    }

    public DNSPacketImpl setRecursionAvailable(boolean ra) {
        this.buf.setByte(3, ra ? this.buf.getByte(3) | 0x80 : this.buf.getByte(3) & 0x7f);
        return this;
    }

    public RESPONSE_CODE getRCODE() {
        return RESPONSE_CODE.fromByte(this.buf.getByte(3));
    }

    public DNSPacketImpl setRCODE(RESPONSE_CODE rcode) {
        this.buf.setByte(3, this.buf.getByte(3) & 0xf0 | rcode.value);
        return this;
    }

    /**
     * an unsigned 16 bit integer specifying the number of
     * entries in the question section.
     *
     * @return short
     */
    public short getQDCOUNT() {
        return this.buf.getShort(4);
    }

    public DNSPacketImpl setQDCOUNT(short qdcount) {
        assert qdcount == 1 || qdcount == 0; // According to BIND
        this.buf.setShort(4, qdcount);
        return this;
    }

    /**
     * an unsigned 16 bit integer specifying the number of
     * resource records in the answer section.
     *
     * @return short
     */
    public short getANCOUNT() {
        return this.buf.getShort(6);
    }

    public DNSPacketImpl setANCOUNT(short ancount) {
        this.buf.setShort(6, ancount);
        return this;
    }

    /**
     * an unsigned 16 bit integer specifying the number of name
     * server resource records in the authority records
     * section.
     *
     * @return short
     */
    public short getNSCOUNT() {
        return this.buf.getShort(8);
    }

    public DNSPacketImpl setNSCOUNT(short nscount) {
        this.buf.setShort(8, nscount);
        return this;
    }

    /**
     * an unsigned 16 bit integer specifying the number of
     * resource records in the additional records section.
     *
     * @return short
     */
    public short getARCOUNT() {
        return this.buf.getShort(10);
    }

    public DNSPacketImpl setARCOUNT(short arcount) {
        this.buf.setShort(10, arcount);
        return this;
    }

    private List<QuestionSectionImpl> questionSectionList = new ArrayList<QuestionSectionImpl>();

    public List<QuestionSectionImpl> getQuestionSectionList() {
        return this.questionSectionList;
    }

    public List<ResourceRecordImpl> getAnswerSectionList() {
        return answerSectionList;
    }

    public List<ResourceRecordImpl> getAuthoritySectionList() {
        return authoritySectionList;
    }

    public List<ResourceRecordImpl> getAdditionalSectionList() {
        return additionalSectionList;
    }

    private List<ResourceRecordImpl> answerSectionList = new ArrayList<ResourceRecordImpl>();

    private List<ResourceRecordImpl> authoritySectionList = new ArrayList<ResourceRecordImpl>();

    private List<ResourceRecordImpl> additionalSectionList = new ArrayList<ResourceRecordImpl>();


}