package net.minecraft.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RemoteStatusReply {

    private final ByteArrayOutputStream a;
    private final DataOutputStream b;

    public RemoteStatusReply(int i) {
        this.a = new ByteArrayOutputStream(i);
        this.b = new DataOutputStream(this.a);
    }

    public void a(byte[] abyte) throws IOException {
        this.b.write(abyte, 0, abyte.length);
    }

    public void writeString(String string) throws IOException { this.a(string); } // Paper - OBFHELPER
    public void a(String s) throws IOException {
        this.b.writeBytes(s);
        this.b.write(0);
    }
    // Paper start - unchecked exception variant to use in Stream API
    public void writeStringUnchecked(String string) {
        try {
            writeString(string);
        } catch (IOException e) {
            com.destroystokyo.paper.util.SneakyThrow.sneaky(e);
        }
    }
    // Paper end

    public void writeInt(int i) throws IOException { this.a(i); } // Paper - OBFHELPER
    public void a(int i) throws IOException {
        this.b.write(i);
    }

    public void writeShort(short i) throws IOException { this.a(i); } // Paper - OBFHELPER
    public void a(short short0) throws IOException {
        this.b.writeShort(Short.reverseBytes(short0));
    }

    public byte[] a() {
        return this.a.toByteArray();
    }

    public void b() {
        this.a.reset();
    }
}
