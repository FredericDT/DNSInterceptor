package onl.fdt.java.cs.network;

import io.netty.buffer.ByteBuf;

public class DNSPacket {

    private ByteBuf buf;

    public DNSPacket(ByteBuf buf) {
        this.buf = buf.copy();
    }

    public short getId() {
        return this.buf.getShort(0);
    }

    public enum QR {

        QUERY((byte) 0x00),
        RESPONSE((byte) 0x80);

        public final byte value;

        QR(final byte value) {
            this.value = value;
        }

        public static QR fromByte(byte b) {
            return (b & 0x80) == 0x80 ? QR.RESPONSE : QR.QUERY;
        }
    }

    public QR getQRType() {
        return QR.fromByte(this.buf.getByte(2));
    }

    public DNSPacket setQRType(QR qrType) {
        if (qrType == QR.RESPONSE) {
            this.buf.setByte(2, this.buf.getByte(2) | 0x80);
        } else {
            this.buf.setByte(2, this.buf.getByte(2) & 0x7f);
        }
        return this;
    }

    public enum Opcode {
        QUERY("STANDARD_QUERY", (byte) 0x00),
        IQUERY("INVERSE_QUERY", (byte) 0x08),
        STATUS("SERVER_STATUS_REUEST", (byte) 0x10),
        REVERSED("REVERSED", (byte) 0x78);
        public String displayName;
        public final byte value;

        Opcode(final String displayName, final byte value) {
            this.displayName = displayName;
            this.value = value;
        }

        public static Opcode fromByte(byte b) {
            int t = b & 0x78;
            switch (t) {
                case 0x00:
                    return Opcode.QUERY;
                case 0x08:
                    return Opcode.IQUERY;
                case 0x10:
                    return Opcode.STATUS;
                default:
                    return Opcode.REVERSED;
            }
        }
    }

    public Opcode getOpcode() {
        return Opcode.fromByte(this.buf.getByte(2));
    }

    public boolean isAuthoritativeAnswer() {
        return (this.buf.getByte(2) & 0x04) == 0x04;
    }

    public DNSPacket setAuthoritativeAnswer(boolean is) {
        this.buf.setByte(2, is ? this.buf.getByte(2) | 0x04 : this.buf.getByte(2) & 0xfb);
        return this;
    }


}
