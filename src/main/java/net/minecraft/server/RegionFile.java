package net.minecraft.server;

import com.destroystokyo.paper.exception.ServerInternalException;
import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;

public class RegionFile {

    // Spigot start
    // Minecraft is limited to 256 sections per chunk. So 1MB. This can easily be overriden.
    // So we extend this to use the REAL size when the count is maxed by seeking to that section and reading the length.
    private static final boolean ENABLE_EXTENDED_SAVE = Boolean.parseBoolean(System.getProperty("net.minecraft.server.RegionFile.enableExtendedSave", "true"));
    // Spigot end
    private static final byte[] a = new byte[4096];
    private final File b;private File getFile() { return b; } // Paper - OBFHELPER
    private RandomAccessFile c;private RandomAccessFile getDataFile() { return c; } // Paper - OBFHELPER
    private final int[] d = new int[1024];private int[] offsets = d; // Paper - OBFHELPER
    private final int[] e = new int[1024];private int[] timestamps = e; // Paper - OBFHELPER
    private List<Boolean> f; private List<Boolean> getFreeSectors() { return this.f; } // Paper - OBFHELPER
    private int g;
    private long h;

    public RegionFile(File file) {
        this.b = file;
        this.g = 0;

        try {
            if (file.exists()) {
                this.h = file.lastModified();
            }

            this.c = new RandomAccessFile(file, "rw");
            if (this.c.length() < 8192L) { // Paper - headers should be 8192
                this.c.write(RegionFile.a);
                this.c.write(RegionFile.a);
                this.g += 8192;
            }

            int i;

            if ((this.c.length() & 4095L) != 0L) {
                for (i = 0; (long) i < (this.c.length() & 4095L); ++i) {
                    this.c.write(0);
                }
            }

            i = (int) this.c.length() / 4096;
            this.f = Lists.newArrayListWithCapacity(i);

            int j;

            for (j = 0; j < i; ++j) {
                this.f.add(true);
            }

            this.f.set(0, false);
            this.f.set(1, false);
            this.c.seek(0L);

            int k;
            // Paper Start
            java.nio.ByteBuffer header = java.nio.ByteBuffer.allocate(8192);
            while (header.hasRemaining())  {
                if (this.c.getChannel().read(header) == -1) throw new java.io.EOFException();
            }
            header.clear();
            java.nio.IntBuffer headerAsInts = header.asIntBuffer();
            initOversizedState();
            // Paper End

            for (j = 0; j < 1024; ++j) {
                k = headerAsInts.get(); // Paper
                this.d[j] = k;
                // Spigot start
                int length = k & 255;
                if (length == 255) {
                    if ((k >> 8) <= this.f.size()) {
                         // We're maxed out, so we need to read the proper length from the section
                        this.c.seek((k >> 8) * 4096);
                        length = (this.c.readInt() + 4) / 4096 + 1;
                        this.c.seek(j * 4 + 4); // Go back to where we were
                    }
                }
                if (k > 0 && (k >> 8) > 1 && (k >> 8) + (length) <= this.f.size()) { // Paper >= 1 as 0/1 are the headers, and negative isnt valid
                    for (int l = 0; l < (length); ++l) {
                        // Spigot end
                        this.f.set((k >> 8) + l, false);
                    }
                }
                // Spigot start
                else if (k != 0) { // Paper
                    org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.SEVERE, "Invalid chunk: ({0}, {1}) Offset: {2} Length: {3} runs off end file. {4}", new Object[]{j % 32, (int) (j / 32), k >> 8, length, file}); // Paper
                    deleteChunk(j); // Paper
                }
                // Spigot end
            }

            for (j = 0; j < 1024; ++j) {
                k = headerAsInts.get(); // Paper
                if (offsets[j] != 0) this.timestamps[j] = k; // Paper - don't set timestamp if it got 0'd above due to corruption
            }
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
            ServerInternalException.reportInternalException(ioexception); // Paper
        }

    }

    @Nullable
    public synchronized DataInputStream getReadStream(int i, int j) { return a(i, j); } @Nullable public synchronized DataInputStream a(int i, int j) { // Paper - OBFHELPER
        if (this.e(i, j)) {
            return null;
        } else {
            try {
                int k = this.getOffset(i, j);

                if (k == 0) {
                    return null;
                } else {
                    int l = k >> 8;
                    int i1 = k & 255;
                    // Spigot start
                    if (i1 == 255) {
                        this.c.seek(l * 4096);
                        i1 = (this.c.readInt() + 4) / 4096 + 1;
                    }
                    // Spigot end

                    if (l + i1 > this.f.size()) {
                        return null;
                    } else {
                        this.c.seek((long) (l * 4096));
                        int j1 = this.c.readInt();

                        if (j1 > 4096 * i1) {
                            org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.SEVERE, "Invalid chunk: ({0}, {1}) Offset: {2} Invalid Size: {3}>{4} {5}", new Object[]{i, j, l, j1, i1 * 4096, this.b}); // Spigot
                            return null;
                        } else if (j1 <= 0) {
                            org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.SEVERE, "Invalid chunk: ({0}, {1}) Offset: {2} Invalid Size: {3} {4}", new Object[]{i, j, l, j1, this.b}); // Spigot
                            return null;
                        } else {
                            byte b0 = this.c.readByte();
                            byte[] abyte;

                            if (b0 == 1) {
                                abyte = new byte[j1 - 1];
                                this.c.read(abyte);
                                return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(abyte))));
                            } else if (b0 == 2) {
                                abyte = new byte[j1 - 1];
                                this.c.read(abyte);
                                return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(abyte))));
                            } else {
                                return null;
                            }
                        }
                    }
                }
            } catch (IOException ioexception) {
                return null;
            }
        }
    }

    public boolean b(int i, int j) {
        if (this.e(i, j)) {
            return false;
        } else {
            int k = this.getOffset(i, j);

            if (k == 0) {
                return false;
            } else {
                int l = k >> 8;
                int i1 = k & 255;

                if (l + i1 > this.f.size()) {
                    return false;
                } else {
                    try {
                        this.c.seek((long) (l * 4096));
                        int j1 = this.c.readInt();

                        return j1 > 4096 * i1 ? false : j1 > 0;
                    } catch (IOException ioexception) {
                        return false;
                    }
                }
            }
        }
    }

    @Nullable
    public DataOutputStream getWriteStream(int i, int j) { return c(i, j); } @Nullable public DataOutputStream c(int i, int j) { // Paper - OBFHELPER
        return this.e(i, j) ? null : new DataOutputStream(new RegionFile.ChunkBuffer(i, j)); // Paper - remove middleware, move deflate to .close() for dynamic levels
    }

    protected synchronized void a(int i, int j, byte[] abyte, int k) {
        try {
            int l = this.getOffset(i, j);
            int i1 = l >> 8; final int oldSectorOffset = i1; // Paper - store variable for later
            int j1 = l & 255; final int oldSectorCount; // Paper - store variable for later
            // Spigot start
            if (j1 == 255) {
                this.c.seek(i1 * 4096);
                j1 = (this.c.readInt() + 4) / 4096 + 1;
            }
            // Spigot end
            oldSectorCount = j1; // Paper - store variable for later (watch out for re-assignments of j1)
            int k1 = (k + 5) / 4096 + 1;

            if (k1 >= 256) {
                // Spigot start
                if (!USE_SPIGOT_OVERSIZED_METHOD && !RegionFileCache.isOverzealous()) throw new ChunkTooLargeException(i, j, k1); // Paper - throw error instead
                org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.WARNING,"Large Chunk Detected: ({0}, {1}) Size: {2} {3}", new Object[]{i, j, k1, this.b});
                if (!ENABLE_EXTENDED_SAVE) return;
                // Spigot end
            }

            if (false && i1 != 0 && j1 == k1) { // Paper - We never want to overrite old data
                this.a(i1, abyte, k);
            } else {
                int l1;

                // Paper - We do not free old sectors until we are done writing the new chunk data

                l1 = this.f.indexOf(true);
                int i2 = 0;
                int j2;

                if (l1 != -1) {
                    for (j2 = l1; j2 < this.f.size(); ++j2) {
                        if (i2 != 0) {
                            if ((Boolean) this.f.get(j2)) {
                                ++i2;
                            } else {
                                i2 = 0;
                            }
                        } else if ((Boolean) this.f.get(j2)) {
                            l1 = j2;
                            i2 = 1;
                        }

                        if (i2 >= k1) {
                            break;
                        }
                    }
                }

                if (i2 >= k1) {
                    i1 = l1;
                    //this.a(i, j, l1 << 8 | (k1 > 255 ? 255 : k1)); // Spigot // Paper - We only write to header after we've written chunk data

                    for (j2 = 0; j2 < k1; ++j2) {
                        this.f.set(i1 + j2, false);
                    }

                    this.writeChunk(i, j,i1 << 8 | (k1 > 255 ? 255 : k1), i1, abyte, k); // Paper - Ensure we do not corrupt region files
                } else {
                    this.c.seek(this.c.length());
                    i1 = this.f.size();

                    for (j2 = 0; j2 < k1; ++j2) {
                        this.c.write(RegionFile.a);
                        this.f.add(false);
                    }

                    this.g += 4096 * k1;
                    this.writeChunk(i, j, i1 << 8 | (k1 > 255 ? 255 : k1), i1, abyte, k); // Paper - Ensure we do not corrupt region files
                }

                // Paper start - Now that we've written the new chunk we can free the old data
                for (int off = 0; off < oldSectorCount; ++off) {
                    this.getFreeSectors().set(oldSectorOffset + off, true);
                }
                // Paper end
            }

            this.b(i, j, (int) (SystemUtils.getTimeMillis() / 1000L));
        } catch (IOException ioexception) {
            com.destroystokyo.paper.util.SneakyThrow.sneaky(ioexception); // Paper - we want the upper try/catch to retry this
        }

    }

    private void writeChunkData(final int sectorOffset, final byte[] data, final int dataLength) throws IOException { this.a(sectorOffset, data, dataLength); } // Paper - OBFHELPER
    private void a(int i, byte[] abyte, int j) throws IOException {
        this.c.seek((long) (i * 4096));
        this.writeIntAndByte(j + 1, (byte)2); // Paper - Avoid 4 io write calls
        this.c.write(abyte, 0, j);
    }

    private boolean e(int i, int j) {
        return i < 0 || i >= 32 || j < 0 || j >= 32;
    }

    private synchronized int getOffset(int i, int j) {
        return this.d[i + j * 32];
    }

    public boolean d(int i, int j) {
        return this.getOffset(i, j) != 0;
    }

    private void updateChunkHeader(final int x, final int z, final int offset) throws IOException { this.a(x, z, offset); } // Paper - OBFHELPER
    private void a(int i, int j, int k) throws IOException {
        this.d[i + j * 32] = k;
        this.c.seek((long) ((i + j * 32) * 4));
        this.writeInt(k); // Paper - Avoid 3 io write calls
    }

    private void b(int i, int j, int k) throws IOException {
        this.e[i + j * 32] = k;
        this.c.seek((long) (4096 + (i + j * 32) * 4));
        this.writeInt(k); // Paper - Avoid 3 io write calls
    }

    public void close() throws IOException {
        if (this.c != null) {
            this.c.close();
        }

    }

    // Paper start
    private static final boolean FLUSH_ON_SAVE = Boolean.getBoolean("paper.flush-on-save");
    private void syncRegionFile() throws IOException {
        if (!FLUSH_ON_SAVE) {
            return;
        }
        this.getDataFile().getFD().sync(); // rethrow exception as we want to avoid corrupting a regionfile
    }

    private final java.nio.ByteBuffer scratchBuffer = java.nio.ByteBuffer.allocate(8);

    private void writeInt(final int value) throws IOException {
        synchronized (scratchBuffer) {
            this.scratchBuffer.putInt(0, value);
            this.getDataFile().write(this.scratchBuffer.array(), 0, 4);
        }
    }

    // writes v1 then v2
    private void writeIntAndByte(final int v1, final byte v2) throws IOException {
        synchronized (scratchBuffer) {
            this.scratchBuffer.putInt(0, v1);
            this.scratchBuffer.put(4, v2);
            this.getDataFile().write(this.scratchBuffer.array(), 0, 5);
        }
    }

    private void writeChunk(final int x, final int z, final int chunkHeaderData,
                            final int chunkOffset, final byte[] chunkData, final int chunkDataLength) throws IOException {
        this.writeChunkData(chunkOffset, chunkData, chunkDataLength);
        this.syncRegionFile(); // Sync is required to ensure the previous data is written successfully
        this.updateChunkHeader(x, z, chunkHeaderData);
        this.syncRegionFile(); // Ensure header changes go through
    }

    public synchronized void deleteChunk(int j1) {
        backup();
        int k = offsets[j1];
        int x = j1 & 1024;
        int z = j1 >> 2;
        int offset = (k >> 8);
        int len = (k & 255);
        String debug = "idx:" + + j1 + " - " + x + "," + z + " - offset: " + offset + " - len: " + len;
        try {
            timestamps[j1] = 0;
            offsets[j1] = 0;
            RandomAccessFile file = getDataFile();
            file.seek(j1 * 4);
            file.writeInt(0);
            // clear the timestamp
            file.seek(4096 + j1 * 4);
            file.writeInt(0);
            org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.SEVERE, "Deleted corrupt chunk (" + debug + ") " + getFile().getAbsolutePath(), e);
        } catch (IOException e) {

            org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.SEVERE, "Error deleting corrupt chunk (" + debug + ") " + getFile().getAbsolutePath(), e);
        }
    }
    private boolean backedUp = false;
    private synchronized void backup() {
        if (backedUp) {
            return;
        }
        backedUp = true;
        File file = this.getFile();
        java.text.DateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date today = new java.util.Date();
        File corrupt = new File(file.getParentFile(), file.getName() + "." + formatter.format(today) + ".corrupt");
        if (corrupt.exists()) {
            return;
        }
        org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger();
        logger.error("Region file " + file.getAbsolutePath() + " was corrupt. Backing up to " + corrupt.getAbsolutePath() + " and repairing");
        try {
            java.nio.file.Files.copy(file.toPath(), corrupt.toPath());

        } catch (IOException e) {
            logger.error("Error backing up corrupt file" + file.getAbsolutePath(), e);
        }
    }

    private final byte[] oversized = new byte[1024];
    private int oversizedCount = 0;

    private synchronized void initOversizedState() throws IOException {
        File metaFile = getOversizedMetaFile();
        if (metaFile.exists()) {
            final byte[] read = java.nio.file.Files.readAllBytes(metaFile.toPath());
            System.arraycopy(read, 0, oversized, 0, oversized.length);
            for (byte temp : oversized) {
                oversizedCount += temp;
            }
        }
    }

    private static int getChunkIndex(int x, int z) {
        return (x & 31) + (z & 31) * 32;
    }
    synchronized boolean isOversized(int x, int z) {
        return this.oversized[getChunkIndex(x, z)] == 1;
    }
    synchronized void setOversized(int x, int z, boolean oversized) throws IOException {
        final int offset = getChunkIndex(x, z);
        boolean previous = this.oversized[offset] == 1;
        this.oversized[offset] = (byte) (oversized ? 1 : 0);
        if (!previous && oversized) {
            oversizedCount++;
        } else if (!oversized && previous) {
            oversizedCount--;
        }
        if (previous && !oversized) {
            File oversizedFile = getOversizedFile(x, z);
            if (oversizedFile.exists()) {
                oversizedFile.delete();
            }
        }
        if (oversizedCount > 0) {
            if (previous != oversized) {
                writeOversizedMeta();
            }
        } else if (previous) {
            File oversizedMetaFile = getOversizedMetaFile();
            if (oversizedMetaFile.exists()) {
                oversizedMetaFile.delete();
            }
        }
    }

    private void writeOversizedMeta() throws IOException {
        java.nio.file.Files.write(getOversizedMetaFile().toPath(), oversized);
    }

    private File getOversizedMetaFile() {
        return new File(getFile().getParentFile(), getFile().getName().replaceAll("\\.mca$", "") + ".oversized.nbt");
    }

    private File getOversizedFile(int x, int z) {
        return new File(this.getFile().getParentFile(), this.getFile().getName().replaceAll("\\.mca$", "") + "_oversized_" + x + "_" + z + ".nbt");
    }

    void writeOversizedData(int x, int z, NBTTagCompound oversizedData) throws IOException {
        File file = getOversizedFile(x, z);
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new java.io.FileOutputStream(file), new java.util.zip.Deflater(java.util.zip.Deflater.BEST_COMPRESSION), 32 * 1024), 32 * 1024))) {
            NBTCompressedStreamTools.writeNBT(oversizedData, out);
        }
        this.setOversized(x, z, true);

    }

    synchronized NBTTagCompound getOversizedData(int x, int z) throws IOException {
        File file = getOversizedFile(x, z);
        try (DataInputStream out = new DataInputStream(new BufferedInputStream(new InflaterInputStream(new java.io.FileInputStream(file))))) {
            return NBTCompressedStreamTools.readNBT(out);
        }

    }

    private static final boolean USE_SPIGOT_OVERSIZED_METHOD = Boolean.getBoolean("Paper.useSpigotExtendedSaveMethod"); // Paper
    static {
        if (USE_SPIGOT_OVERSIZED_METHOD) {
            org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.SEVERE, "====================================");
            org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.SEVERE, "Using Spigot Oversized Chunk save method. Warning this will result in extremely fragmented chunks, as well as making the entire region file unable to be to used in any other software but Forge or Spigot (not usable in Vanilla or CraftBukkit). Paper's method is highly recommended.");
            org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.SEVERE, "====================================");
        }
    }
    public class ChunkTooLargeException extends RuntimeException {
        public ChunkTooLargeException(int x, int z, int sectors) {
            super("Chunk " + x + "," + z + " of " + getFile().toString() + " is too large (" + sectors + "/256)");
        }
    }
    private static class DirectByteArrayOutputStream extends ByteArrayOutputStream {
        public DirectByteArrayOutputStream() {
            super();
        }

        public DirectByteArrayOutputStream(int size) {
            super(size);
        }

        public byte[] getBuffer() {
            return this.buf;
        }
    }
    // Paper end

    class ChunkBuffer extends ByteArrayOutputStream {

        private final int b;
        private final int c;

        public ChunkBuffer(int i, int j) {
            super(8096);
            this.b = i;
            this.c = j;
        }

        public void close() throws IOException {
            // Paper start - apply dynamic compression
            int origLength = this.count;
            byte[] buf = this.buf;
            DirectByteArrayOutputStream out = compressData(buf, origLength);
            byte[] bytes = out.getBuffer();
            int length = out.size();

            RegionFile.this.a(this.b, this.c, bytes, length); // Paper - change to bytes/length
        }
    }

    private static final byte[] compressionBuffer = new byte[1024 * 64]; // 64k fits most standard chunks input size even, ideally 1 pass through zlib
    private static final java.util.zip.Deflater deflater = new java.util.zip.Deflater();
    // since file IO is single threaded, no benefit to using per-region file buffers/synchronization, we can change that later if it becomes viable.
    private static DirectByteArrayOutputStream compressData(byte[] buf, int length) throws IOException {
        synchronized (deflater) {
            deflater.setInput(buf, 0, length);
            deflater.finish();

            DirectByteArrayOutputStream out = new DirectByteArrayOutputStream(length);
            while (!deflater.finished()) {
                out.write(compressionBuffer, 0, deflater.deflate(compressionBuffer));
            }
            out.close();
            deflater.reset();
            return out;
        }
    }
    // Paper end

}
