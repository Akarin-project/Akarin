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

public class RegionFile implements AutoCloseable {

    // Spigot start
    // Minecraft is limited to 256 sections per chunk. So 1MB. This can easily be overriden.
    // So we extend this to use the REAL size when the count is maxed by seeking to that section and reading the length.
    private static final boolean ENABLE_EXTENDED_SAVE = Boolean.parseBoolean(System.getProperty("net.minecraft.server.RegionFile.enableExtendedSave", "true"));
    final File file; // Paper - private -> package
    // Spigot end
    private static final byte[] a = new byte[4096];
    private final RandomAccessFile b; private RandomAccessFile getDataFile() { return this.b; } // Paper - OBFHELPER // PAIL dataFile
    private final int[] c = new int[1024]; private final int[] offsets = c; // Paper - OBFHELPER
    private final int[] d = new int[1024]; private final int[] timestamps = d; // Paper - OBFHELPER
    private final List<Boolean> e; // PAIL freeSectors

    // Paper start - Cache chunk status
    private final ChunkStatus[] statuses = new ChunkStatus[32 * 32];

    private boolean closed;

    // invoked on write/read
    public void setStatus(int x, int z, ChunkStatus status) {
        if (this.closed) {
            // We've used an invalid region file.
            throw new IllegalStateException("RegionFile is closed");
        }
        this.statuses[this.getChunkLocation(new ChunkCoordIntPair(x, z))] = status;
    }

    public ChunkStatus getStatusIfCached(int x, int z) {
        if (this.closed) {
            // We've used an invalid region file.
            throw new IllegalStateException("RegionFile is closed");
        }
        final int location = this.getChunkLocation(new ChunkCoordIntPair(x, z));
        return this.statuses[location];
    }
    // Paper end

    public RegionFile(File file) throws IOException {
        this.b = new RandomAccessFile(file, "rw");
        this.file = file; // Spigot // Paper - We need this earlier
        if (this.b.length() < 8192L) { // Paper - headers should be 8192
            this.b.write(RegionFile.a);
            this.b.write(RegionFile.a);
        }

        int i;

        if ((this.b.length() & 4095L) != 0L) {
            for (i = 0; (long) i < (this.b.length() & 4095L); ++i) {
                this.b.write(0);
            }
        }

        i = (int) this.b.length() / 4096;
        this.e = Lists.newArrayListWithCapacity(i);

        int j;

        for (j = 0; j < i; ++j) {
            this.e.add(true);
        }

        this.e.set(0, false);
        this.e.set(1, false);
        this.b.seek(0L);

        // Paper Start
        java.nio.ByteBuffer header = java.nio.ByteBuffer.allocate(8192);
        while (header.hasRemaining())  {
            if (this.getDataFile().getChannel().read(header) == -1) throw new java.io.EOFException();
        }
        ((java.nio.Buffer) header).clear();
        java.nio.IntBuffer headerAsInts = header.asIntBuffer();
        initOversizedState();
        // Paper End

        int k;

        for (j = 0; j < 1024; ++j) {
            k = headerAsInts.get(); // Paper
            this.c[j] = k;
            // Spigot start
            int length = k & 255;
            if (length == 255) {
                if ((k >> 8) <= this.e.size()) {
                     // We're maxed out, so we need to read the proper length from the section
                    this.b.seek((k >> 8) * 4096);
                    length = (this.b.readInt() + 4) / 4096 + 1;
                    this.b.seek(j * 4 + 4); // Go back to where we were
                }
            }
            if (k > 0 && (k >> 8) > 1 && (k >> 8) + (length) <= this.e.size()) { // Paper >= 1 as 0/1 are the headers, and negative isnt valid
                for (int l = 0; l < (length); ++l) {
                    // Spigot end
                    this.e.set((k >> 8) + l, false);
                }
            }
            // Spigot start
            else if (length > 0) {
                org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.WARNING, "Invalid chunk: ({0}, {1}) Offset: {2} Length: {3} runs off end file. {4}", new Object[]{j % 32, (int) (j / 32), k >> 8, length, file});
                deleteChunk(j); // Paper
            }
            // Spigot end
        }

        for (j = 0; j < 1024; ++j) {
            k = headerAsInts.get(); // Paper
            if (this.offsets[j] != 0) this.timestamps[j] = k; // Paper - don't set timestamp if it got 0'd above due to corruption
        }

        // Paper - we need this earlier
    }

    @Nullable
    public synchronized DataInputStream getReadStream(ChunkCoordIntPair chunkcoordintpair) { return this.a(chunkcoordintpair); } public synchronized DataInputStream a(ChunkCoordIntPair chunkcoordintpair) { // Paper - OBFHELPER
        try {
            int i = this.getOffset(chunkcoordintpair);

            if (i == 0) {
                return null;
            } else {
                int j = i >> 8;
                int k = i & 255;
                // Spigot start
                if (k == 255) {
                    this.b.seek(j * 4096);
                    k = (this.b.readInt() + 4) / 4096 + 1;
                }
                // Spigot end

                if (j + k > this.e.size()) {
                    return null;
                } else {
                    this.b.seek((long) (j * 4096));
                    int l = this.b.readInt();

                    if (l > 4096 * k) {
                        org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.WARNING, "Invalid chunk: ({0}) Offset: {1} Invalid Size: {2}>{3} {4}", new Object[]{chunkcoordintpair, j, l, k * 4096, this.file}); // Spigot
                        return null;
                    } else if (l <= 0) {
                        org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.WARNING, "Invalid chunk: ({0}) Offset: {1} Invalid Size: {2} {3}", new Object[]{chunkcoordintpair, j, l, this.file}); // Spigot
                        return null;
                    } else {
                        byte b0 = this.b.readByte();
                        byte[] abyte;

                        if (b0 == 1) {
                            abyte = new byte[l - 1];
                            this.b.read(abyte);
                            return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(abyte))));
                        } else if (b0 == 2) {
                            abyte = new byte[l - 1];
                            this.b.read(abyte);
                            return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(abyte))));
                        } else {
                            return null;
                        }
                    }
                }
            }
        } catch (IOException ioexception) {
            ServerInternalException.reportInternalException(ioexception); // Paper
            return null;
        }
    }

    public boolean b(ChunkCoordIntPair chunkcoordintpair) {
        int i = this.getOffset(chunkcoordintpair);

        if (i == 0) {
            return false;
        } else {
            int j = i >> 8;
            int k = i & 255;

            if (j + k > this.e.size()) {
                return false;
            } else {
                try {
                    this.b.seek((long) (j * 4096));
                    int l = this.b.readInt();

                    return l > 4096 * k ? false : l > 0;
                } catch (IOException ioexception) {
                    return false;
                }
            }
        }
    }

    public DataOutputStream getWriteStream(ChunkCoordIntPair chunkcoordintpair) { return this.c(chunkcoordintpair); } public DataOutputStream c(ChunkCoordIntPair chunkcoordintpair) { // Paper - OBFHELPER
        return new DataOutputStream(new RegionFile.ChunkBuffer(chunkcoordintpair)); // Paper - remove middleware, move deflate to .close() for dynamic levels
    }

    protected synchronized void a(ChunkCoordIntPair chunkcoordintpair, byte[] abyte, int i) {
        try {
            int j = this.getOffset(chunkcoordintpair);
            int k = j >> 8; final int oldSectorOffset = k; // Spigot - store variable for later
            int l = j & 255; final int oldSectorCount; // Spigot - store variable for later
            // Spigot start
            if (l == 255) {
                this.b.seek(k * 4096);
                l = (this.b.readInt() + 4) / 4096 + 1;
            }
            // Spigot end
            int i1 = (i + 5) / 4096 + 1;
            oldSectorCount = l; // Spigot - store variable for later (watch out for re-assignments of l)

            if (i1 >= 256) {
                // Spigot start
                if (!USE_SPIGOT_OVERSIZED_METHOD && !RegionFileCache.isOverzealous()) throw new ChunkTooLargeException(chunkcoordintpair.x, chunkcoordintpair.z, l); // Paper - throw error instead
                org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.WARNING,"Large Chunk Detected: ({0}) Size: {1} {2}", new Object[]{chunkcoordintpair, i1, this.file});
                if (!ENABLE_EXTENDED_SAVE) throw new RuntimeException(String.format("Too big to save, %d > 1048576", i)); // Paper - move after our check
                // Spigot end
            }

            if (false && k != 0 && l == i1) { // Spigot - We never want to overrite old data
                this.a(k, abyte, i);
            } else {
                int j1;

                // Spigot start - We do not free old sectors until we are done writing the new chunk data
                /*
                for (j1 = 0; j1 < l; ++j1) {
                    this.e.set(k + j1, true);
                }
                 */
                // Spigot end

                j1 = this.e.indexOf(true);
                int k1 = 0;
                int l1;

                if (j1 != -1) {
                    for (l1 = j1; l1 < this.e.size(); ++l1) {
                        if (k1 != 0) {
                            if ((Boolean) this.e.get(l1)) {
                                ++k1;
                            } else {
                                k1 = 0;
                            }
                        } else if ((Boolean) this.e.get(l1)) {
                            j1 = l1;
                            k1 = 1;
                        }

                        if (k1 >= i1) {
                            break;
                        }
                    }
                }

                if (k1 >= i1) {
                    k = j1;
                    // this.a(chunkcoordintpair, j1 << 8 | (i1 > 255 ? 255 : i1)); // Spigot // Spigot - We only write to header after we've written chunk data

                    for (l1 = 0; l1 < i1; ++l1) {
                        this.e.set(k + l1, false);
                    }

                    this.writeChunk(chunkcoordintpair, j1 << 8 | (i1 > 255 ? 255 : i1), k, abyte, i); // Spigot - Ensure we do not corrupt region files
                } else {
                    this.b.seek(this.b.length());
                    k = this.e.size();

                    for (l1 = 0; l1 < i1; ++l1) {
                        this.b.write(RegionFile.a);
                        this.e.add(false);
                    }

                    this.writeChunk(chunkcoordintpair, k << 8 | (i1 > 255 ? 255 : i1), k, abyte, i); // Spigot - Ensure we do not corrupt region files
                }

                // Spigot start - Now that we've written the new chunk we can free the old data
                for (int off = 0; off < oldSectorCount; ++off) {
                    this.e.set(oldSectorOffset + off, true);
                }
                // Spigot end
            }

            // this.b(chunkcoordintpair, (int) (SystemUtils.getTimeMillis() / 1000L)); // Spigot - move this into writeChunk
        } catch (IOException ioexception) {
            com.destroystokyo.paper.util.SneakyThrow.sneaky(ioexception); // Paper - we want the upper try/catch to retry this
        }

    }

    private void a(int i, byte[] abyte, int j) throws IOException { // PAIL writeChunkData
        this.b.seek((long) (i * 4096));
        this.writeIntAndByte(j + 1, (byte)2); // Spigot - Avoid 4 io write calls
        this.b.write(abyte, 0, j);
    }

    private int getOffset(ChunkCoordIntPair chunkcoordintpair) {
        return this.c[this.f(chunkcoordintpair)];
    }

    public final boolean chunkExists(ChunkCoordIntPair chunkPos) { return this.d(chunkPos); } // Paper - OBFHELPER
    public boolean d(ChunkCoordIntPair chunkcoordintpair) {
        return this.getOffset(chunkcoordintpair) != 0;
    }

    private void a(ChunkCoordIntPair chunkcoordintpair, int i) throws IOException { // PAIL updateChunkHeader
        int j = this.f(chunkcoordintpair);

        //this.c[j] = i; // Spigot - move this to after the write
        this.b.seek((long) (j * 4));
        this.writeInt(i); // Spigot - Avoid 3 io write calls
        this.c[j] = i; // Spigot - move this to after the write
    }

    private final int getChunkLocation(ChunkCoordIntPair chunkcoordintpair) { return this.f(chunkcoordintpair); } // Paper - OBFHELPER
    private int f(ChunkCoordIntPair chunkcoordintpair) {
        return chunkcoordintpair.j() + chunkcoordintpair.k() * 32;
    }

    private void b(ChunkCoordIntPair chunkcoordintpair, int i) throws IOException { // PAIL updateChunkTime
        int j = this.f(chunkcoordintpair);

        // this.d[j] = i; // Spigot - move this to after the write
        this.b.seek((long) (4096 + j * 4));
        this.writeInt(i); // Spigot - Avoid 3 io write calls
        this.d[j] = i; // Spigot - move this to after the write
    }

    public synchronized void close() throws IOException { // Paper - synchronize
        this.closed = true; // Paper
        this.b.close();
    }

    // Spigot start - Make region files reliable
    private static final boolean FLUSH_ON_SAVE = Boolean.getBoolean("spigot.flush-on-save") || Boolean.getBoolean("paper.flush-on-save"); // Paper - preserve old flag
    private void syncRegionFile() throws IOException {
        if (!FLUSH_ON_SAVE) {
            return;
        }
        this.b.getFD().sync(); // rethrow exception as we want to avoid corrupting a regionfile
    }

    private final java.nio.ByteBuffer scratchBuffer = java.nio.ByteBuffer.allocate(8);

    private void writeInt(final int value) throws IOException {
        this.scratchBuffer.putInt(0, value);
        this.b.write(this.scratchBuffer.array(), 0, 4);
    }

    // writes v1 then v2
    private void writeIntAndByte(final int v1, final byte v2) throws IOException {
        this.scratchBuffer.putInt(0, v1);
        this.scratchBuffer.put(4, v2);
        this.b.write(this.scratchBuffer.array(), 0, 5);
    }

    private void writeChunk(final ChunkCoordIntPair chunk, final int chunkHeaderData, final int chunkOffset, final byte[] chunkData, final int chunkDataLength) throws IOException {
        this.a(chunkOffset, chunkData, chunkDataLength); // chunk data
        this.syncRegionFile(); // Sync is required to ensure the previous data is written successfully
        this.b(chunk, (int) (SystemUtils.getTimeMillis() / 1000L)); // chunk time
        this.a(chunk, chunkHeaderData); // chunk header
        this.syncRegionFile(); // Ensure header changes go through
    }
    // Spigot end

    // Paper start
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
            org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.SEVERE, "Deleted corrupt chunk (" + debug + ") " + this.file.getAbsolutePath(), e);
        } catch (IOException e) {

            org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.SEVERE, "Error deleting corrupt chunk (" + debug + ") " + this.file.getAbsolutePath(), e);
        }
    }
    private boolean backedUp = false;
    private synchronized void backup() {
        if (backedUp) {
            return;
        }
        backedUp = true;
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
        return new File(this.file.getParentFile(), this.file.getName().replaceAll("\\.mca$", "") + ".oversized.nbt");
    }

    private File getOversizedFile(int x, int z) {
        return new File(this.file.getParentFile(), this.file.getName().replaceAll("\\.mca$", "") + "_oversized_" + x + "_" + z + ".nbt");
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
            super("Chunk " + x + "," + z + " of " + RegionFile.this.file.toString() + " is too large (" + sectors + "/255)");
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

        private final ChunkCoordIntPair b;

        public ChunkBuffer(ChunkCoordIntPair chunkcoordintpair) {
            super(8096);
            this.b = chunkcoordintpair;
        }

        public void close() throws IOException {
            // Paper start - apply dynamic compression
            int origLength = this.count;
            byte[] buf = this.buf;
            DirectByteArrayOutputStream out = compressData(buf, origLength);
            byte[] bytes = out.getBuffer();
            int length = out.size();

            RegionFile.this.a(this.b, bytes, length); // Paper - change to bytes/length
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
