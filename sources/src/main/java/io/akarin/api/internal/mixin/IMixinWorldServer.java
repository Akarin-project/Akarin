package io.akarin.api.internal.mixin;

import java.util.Random;

public interface IMixinWorldServer {
    public Object lock();
    public Random rand();
}