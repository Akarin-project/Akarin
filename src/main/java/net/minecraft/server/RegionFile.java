package net.minecraft.server;

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

    private static final byte[] a = new byte[4096];
    private final File b;
    private RandomAccessFile c;
    private final int[] d = new int[1024];
    private final int[] e = new int[1024];
    private List<Boolean> f;
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
            if (this.c.length() < 4096L) {
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

            for (j = 0; j < 1024; ++j) {
                k = this.c.readInt();
                this.d[j] = k;
                if (k != 0 && (k >> 8) + (k & 255) <= this.f.size()) {
                    for (int l = 0; l < (k & 255); ++l) {
                        this.f.set((k >> 8) + l, false);
                    }
                }
            }

            for (j = 0; j < 1024; ++j) {
                k = this.c.readInt();
                this.e[j] = k;
            }
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }

    }

    @Nullable
    public synchronized DataInputStream a(int i, int j) {
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

                    if (l + i1 > this.f.size()) {
                        return null;
                    } else {
                        this.c.seek((long) (l * 4096));
                        int j1 = this.c.readInt();

                        if (j1 > 4096 * i1) {
                            return null;
                        } else if (j1 <= 0) {
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
    public DataOutputStream c(int i, int j) {
        return this.e(i, j) ? null : new DataOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new RegionFile.ChunkBuffer(i, j))));
    }

    protected synchronized void a(int i, int j, byte[] abyte, int k) {
        try {
            int l = this.getOffset(i, j);
            int i1 = l >> 8;
            int j1 = l & 255;
            int k1 = (k + 5) / 4096 + 1;

            if (k1 >= 256) {
                return;
            }

            if (i1 != 0 && j1 == k1) {
                this.a(i1, abyte, k);
            } else {
                int l1;

                for (l1 = 0; l1 < j1; ++l1) {
                    this.f.set(i1 + l1, true);
                }

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
                    this.a(i, j, l1 << 8 | k1);

                    for (j2 = 0; j2 < k1; ++j2) {
                        this.f.set(i1 + j2, false);
                    }

                    this.a(i1, abyte, k);
                } else {
                    this.c.seek(this.c.length());
                    i1 = this.f.size();

                    for (j2 = 0; j2 < k1; ++j2) {
                        this.c.write(RegionFile.a);
                        this.f.add(false);
                    }

                    this.g += 4096 * k1;
                    this.a(i1, abyte, k);
                    this.a(i, j, i1 << 8 | k1);
                }
            }

            this.b(i, j, (int) (SystemUtils.getTimeMillis() / 1000L));
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }

    }

    private void a(int i, byte[] abyte, int j) throws IOException {
        this.c.seek((long) (i * 4096));
        this.c.writeInt(j + 1);
        this.c.writeByte(2);
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

    private void a(int i, int j, int k) throws IOException {
        this.d[i + j * 32] = k;
        this.c.seek((long) ((i + j * 32) * 4));
        this.c.writeInt(k);
    }

    private void b(int i, int j, int k) throws IOException {
        this.e[i + j * 32] = k;
        this.c.seek((long) (4096 + (i + j * 32) * 4));
        this.c.writeInt(k);
    }

    public void close() throws IOException {
        if (this.c != null) {
            this.c.close();
        }

    }

    class ChunkBuffer extends ByteArrayOutputStream {

        private final int b;
        private final int c;

        public ChunkBuffer(int i, int j) {
            super(8096);
            this.b = i;
            this.c = j;
        }

        public void close() {
            RegionFile.this.a(this.b, this.c, this.buf, this.count);
        }
    }
}
