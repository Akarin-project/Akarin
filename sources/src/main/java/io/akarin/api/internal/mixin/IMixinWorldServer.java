package io.akarin.api.internal.mixin;

import java.util.Random;

public interface IMixinWorldServer {
    public Object tickLock();
    public Random rand();
    public Object trackLock();
}