package io.akarin.api;

import java.util.concurrent.ThreadFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public abstract class LogWrapper {
    /**
     * A common logger used by mixin classes
     */
    public final static Logger logger = LogManager.getLogger("Akarin");
    
    /**
     * Temporarily disable desync timings error, moreover it's worthless to trace async operation
     */
    public static volatile boolean silentTiming;
    
    /**
     * A common thread pool factory
     */
    public static final ThreadFactory STAGE_FACTORY = new ThreadFactoryBuilder().setNameFormat("Akarin Schedule Thread - %1$d").build();
}
