package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldUpgrader {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final ThreadFactory b = (new ThreadFactoryBuilder()).setDaemon(true).build();
    private final String c;
    private final boolean d;
    private final WorldNBTStorage e;
    private final Thread f;
    private final File g;
    private volatile boolean h = true;
    private volatile boolean i;
    private volatile float j;
    private volatile int k;
    private volatile int l;
    private volatile int m;
    private final Object2FloatMap<DimensionManager> n = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap(SystemUtils.i()));
    private volatile IChatBaseComponent o = new ChatMessage("optimizeWorld.stage.counting", new Object[0]);
    private static final Pattern p = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
    private final WorldPersistentData q;

    public WorldUpgrader(String s, Convertable convertable, WorldData worlddata, boolean flag) {
        this.c = worlddata.getName();
        this.d = flag;
        this.e = convertable.a(s, (MinecraftServer) null);
        this.e.saveWorldData(worlddata);
        this.q = new WorldPersistentData(new File(DimensionManager.OVERWORLD.a(this.e.getDirectory()), "data"), this.e.getDataFixer());
        this.g = this.e.getDirectory();
        this.f = WorldUpgrader.b.newThread(this::i);
        this.f.setUncaughtExceptionHandler((thread, throwable) -> {
            WorldUpgrader.LOGGER.error("Error upgrading world", throwable);
            this.o = new ChatMessage("optimizeWorld.stage.failed", new Object[0]);
        });
        this.f.start();
    }

    public void a() {
        this.h = false;

        try {
            this.f.join();
        } catch (InterruptedException interruptedexception) {
            ;
        }

    }

    private void i() {
        File file = this.e.getDirectory();

        this.k = 0;
        Builder<DimensionManager, ListIterator<ChunkCoordIntPair>> builder = ImmutableMap.builder();

        List list;

        for (Iterator iterator = DimensionManager.a().iterator(); iterator.hasNext(); this.k += list.size()) {
            DimensionManager dimensionmanager = (DimensionManager) iterator.next();

            list = this.b(dimensionmanager);
            builder.put(dimensionmanager, list.listIterator());
        }

        if (this.k == 0) {
            this.i = true;
        } else {
            float f = (float) this.k;
            ImmutableMap<DimensionManager, ListIterator<ChunkCoordIntPair>> immutablemap = builder.build();
            Builder<DimensionManager, IChunkLoader> builder1 = ImmutableMap.builder();
            Iterator iterator1 = DimensionManager.a().iterator();

            while (iterator1.hasNext()) {
                DimensionManager dimensionmanager1 = (DimensionManager) iterator1.next();
                File file1 = dimensionmanager1.a(file);

                builder1.put(dimensionmanager1, new IChunkLoader(new File(file1, "region"), this.e.getDataFixer()));
            }

            ImmutableMap<DimensionManager, IChunkLoader> immutablemap1 = builder1.build();
            long i = SystemUtils.getMonotonicMillis();

            this.o = new ChatMessage("optimizeWorld.stage.upgrading", new Object[0]);

            while (this.h) {
                boolean flag = false;
                float f1 = 0.0F;

                float f2;

                for (Iterator iterator2 = DimensionManager.a().iterator(); iterator2.hasNext(); f1 += f2) {
                    DimensionManager dimensionmanager2 = (DimensionManager) iterator2.next();
                    ListIterator<ChunkCoordIntPair> listiterator = (ListIterator) immutablemap.get(dimensionmanager2);
                    IChunkLoader ichunkloader = (IChunkLoader) immutablemap1.get(dimensionmanager2);

                    if (listiterator.hasNext()) {
                        ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) listiterator.next();
                        boolean flag1 = false;

                        try {
                            NBTTagCompound nbttagcompound = ichunkloader.read(chunkcoordintpair);

                            if (nbttagcompound != null) {
                                int j = IChunkLoader.a(nbttagcompound);
                                NBTTagCompound nbttagcompound1 = ichunkloader.getChunkData(dimensionmanager2, () -> {
                                    return this.q;
                                }, nbttagcompound, chunkcoordintpair, null); // CraftBukkit
                                boolean flag2 = j < SharedConstants.a().getWorldVersion();

                                if (this.d) {
                                    NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound("Level");

                                    flag2 = flag2 || nbttagcompound2.hasKey("Heightmaps");
                                    nbttagcompound2.remove("Heightmaps");
                                    flag2 = flag2 || nbttagcompound2.hasKey("isLightOn");
                                    nbttagcompound2.remove("isLightOn");
                                }

                                if (flag2) {
                                    ichunkloader.write(chunkcoordintpair, nbttagcompound1);
                                    flag1 = true;
                                }
                            }
                        } catch (ReportedException reportedexception) {
                            Throwable throwable = reportedexception.getCause();

                            if (!(throwable instanceof IOException)) {
                                throw reportedexception;
                            }

                            WorldUpgrader.LOGGER.error("Error upgrading chunk {}", chunkcoordintpair, throwable);
                        } catch (IOException ioexception) {
                            WorldUpgrader.LOGGER.error("Error upgrading chunk {}", chunkcoordintpair, ioexception);
                        }

                        if (flag1) {
                            ++this.l;
                        } else {
                            ++this.m;
                        }

                        flag = true;
                    }

                    f2 = (float) listiterator.nextIndex() / f;
                    this.n.put(dimensionmanager2, f2);
                }

                this.j = f1;
                if (!flag) {
                    this.h = false;
                }
            }

            this.o = new ChatMessage("optimizeWorld.stage.finished", new Object[0]);
            UnmodifiableIterator unmodifiableiterator = immutablemap1.values().iterator();

            while (unmodifiableiterator.hasNext()) {
                IChunkLoader ichunkloader1 = (IChunkLoader) unmodifiableiterator.next();

                try {
                    ichunkloader1.close();
                } catch (IOException ioexception1) {
                    WorldUpgrader.LOGGER.error("Error upgrading chunk", ioexception1);
                }
            }

            this.q.a();
            i = SystemUtils.getMonotonicMillis() - i;
            WorldUpgrader.LOGGER.info("World optimizaton finished after {} ms", i);
            this.i = true;
        }
    }

    private List<ChunkCoordIntPair> b(DimensionManager dimensionmanager) {
        File file = dimensionmanager.a(this.g);
        File file1 = new File(file, "region");
        File[] afile = file1.listFiles((file2, s) -> {
            return s.endsWith(".mca");
        });

        if (afile == null) {
            return ImmutableList.of();
        } else {
            List<ChunkCoordIntPair> list = Lists.newArrayList();
            File[] afile1 = afile;
            int i = afile.length;

            for (int j = 0; j < i; ++j) {
                File file2 = afile1[j];
                Matcher matcher = WorldUpgrader.p.matcher(file2.getName());

                if (matcher.matches()) {
                    int k = Integer.parseInt(matcher.group(1)) << 5;
                    int l = Integer.parseInt(matcher.group(2)) << 5;

                    try {
                        RegionFile regionfile = new RegionFile(file2);
                        Throwable throwable = null;

                        try {
                            for (int i1 = 0; i1 < 32; ++i1) {
                                for (int j1 = 0; j1 < 32; ++j1) {
                                    ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i1 + k, j1 + l);

                                    if (regionfile.b(chunkcoordintpair)) {
                                        list.add(chunkcoordintpair);
                                    }
                                }
                            }
                        } catch (Throwable throwable1) {
                            throwable = throwable1;
                            throw throwable1;
                        } finally {
                            if (regionfile != null) {
                                if (throwable != null) {
                                    try {
                                        regionfile.close();
                                    } catch (Throwable throwable2) {
                                        throwable.addSuppressed(throwable2);
                                    }
                                } else {
                                    regionfile.close();
                                }
                            }

                        }
                    } catch (Throwable throwable3) {
                        ;
                    }
                }
            }

            return list;
        }
    }

    public boolean b() {
        return this.i;
    }

    public int d() {
        return this.k;
    }

    public int e() {
        return this.l;
    }

    public int f() {
        return this.m;
    }

    public IChatBaseComponent g() {
        return this.o;
    }
}
