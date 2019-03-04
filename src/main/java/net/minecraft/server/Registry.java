package net.minecraft.server;

import java.util.Iterator;
public interface Registry<T> extends Iterable<T> { // Paper - decompile fix

    @Override
    Iterator<T> iterator(); // Paper - decompile fix
}
