package io.akarin.server.misc;

import net.minecraft.server.ChunkCoordIntPair;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashFunction;

/*
 * Reference on spawning mechanics by Colin Godsey <crgodsey@gmail.com>
 * https://github.com/yesdog/Paper/blob/0de3dd84b7e6688feb42af4fe6b4f323ce7e3013/Spigot-Server-Patches/0433-alternate-mob-spawning-mechanic.patch
 */
public class ChunkCoordOrdinalInt3Tuple extends ChunkCoordIntPair {
    public static final HashFunction hashFunc = Hashing.murmur3_32("akarin".hashCode());

    public final int ordinal;
    public final int cachedHashCode;

    public ChunkCoordOrdinalInt3Tuple(int x, int z, int ord) {
        super(x, z);

        this.ordinal = ord;

        cachedHashCode = hashFunc.newHasher()
            .putInt(ordinal)
            .putInt(x)
            .putInt(z)
            .hash().asInt();
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChunkCoordOrdinalInt3Tuple)) {
            return false;
        } else {
            ChunkCoordOrdinalInt3Tuple pair = (ChunkCoordOrdinalInt3Tuple) object;

            return this.x == pair.x && this.z == pair.z && this.ordinal == pair.ordinal;
        }
    }
}