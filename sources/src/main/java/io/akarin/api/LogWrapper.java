package io.akarin.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LogWrapper {
    /**
     * A common logger used by mixin classes
     */
    public final static Logger logger = LogManager.getLogger("Akarin");
    
    /**
     * Temporarily disable desync timings error, moreover it's worthless to trace async operation
     */
    public static volatile boolean silentTiming;
}
