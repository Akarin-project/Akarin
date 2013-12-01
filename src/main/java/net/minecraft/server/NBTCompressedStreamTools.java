package net.minecraft.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NBTCompressedStreamTools {

    public static NBTTagCompound a(InputStream inputstream) throws IOException {
        DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(inputstream)));

        NBTTagCompound nbttagcompound;

        try {
            nbttagcompound = a((DataInput) datainputstream, NBTReadLimiter.a);
        } finally {
            datainputstream.close();
        }

        return nbttagcompound;
    }

    public static void a(NBTTagCompound nbttagcompound, OutputStream outputstream) throws IOException {
        DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputstream)));

        try {
            a(nbttagcompound, (DataOutput) dataoutputstream);
        } finally {
            dataoutputstream.close();
        }

    }

    public static NBTTagCompound a(DataInputStream datainputstream) throws IOException {
        return a((DataInput) datainputstream, NBTReadLimiter.a);
    }

    public static NBTTagCompound a(DataInput datainput, NBTReadLimiter nbtreadlimiter) throws IOException {
        NBTBase nbtbase = a(datainput, 0, nbtreadlimiter);

        if (nbtbase instanceof NBTTagCompound) {
            return (NBTTagCompound) nbtbase;
        } else {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static void a(NBTTagCompound nbttagcompound, DataOutput dataoutput) throws IOException {
        a((NBTBase) nbttagcompound, dataoutput);
    }

    private static void a(NBTBase nbtbase, DataOutput dataoutput) throws IOException {
        dataoutput.writeByte(nbtbase.getTypeId());
        if (nbtbase.getTypeId() != 0) {
            dataoutput.writeUTF("");
            nbtbase.write(dataoutput);
        }
    }

    private static NBTBase a(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        byte b0 = datainput.readByte();

        if (b0 == 0) {
            return new NBTTagEnd();
        } else {
            datainput.readUTF();
            NBTBase nbtbase = NBTBase.createTag(b0);

            try {
                nbtbase.load(datainput, i, nbtreadlimiter);
                return nbtbase;
            } catch (IOException ioexception) {
                CrashReport crashreport = CrashReport.a(ioexception, "Loading NBT data");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("NBT Tag");

                crashreportsystemdetails.a("Tag type", (Object) b0);
                throw new ReportedException(crashreport);
            }
        }
    }
}
