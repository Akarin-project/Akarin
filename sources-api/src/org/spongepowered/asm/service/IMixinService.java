/*
 * This file is part of Mixin, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.asm.service;

import java.io.InputStream;
import java.util.Collection;

import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment.CompatibilityLevel;
import org.spongepowered.asm.mixin.MixinEnvironment.Phase;
import org.spongepowered.asm.util.ReEntranceLock;

/**
 * Mixin Service interface. Mixin services connect the mixin subsytem to the
 * underlying environment. It is something of a god interface at present because
 * it contains all of the current functionality accessors for calling into
 * launchwrapper. In the future once support for modlauncher is added, it is
 * anticipated that the interface can be split down into sub-services which
 * handle different aspects of interacting with the environment.
 */
public interface IMixinService {
    
    /**
     * Get the friendly name for this service
     */
    public abstract String getName();

    /**
     * True if this service type is valid in the current environment
     */
    public abstract boolean isValid();
    
    /**
     * Called at subsystem boot
     */
    public abstract void prepare();

    /**
     * Get the initial subsystem phase
     */
    public abstract Phase getInitialPhase();
    
    /**
     * Called when the subsystem is offering internal components to the service,
     * the service can determine whether to retain or ignore the component based
     * on its own requirements.
     * 
     * @param internal Internal component being offered
     */
    public abstract void offer(IMixinInternal internal);
    
    /**
     * Called at the end of subsystem boot
     */
    public abstract void init();

    /**
     * Called whenever a new phase is started 
     */
    public abstract void beginPhase();

    /**
     * Check whether the supplied object is a valid boot source for mixin
     * environment
     * 
     * @param bootSource boot source
     */
    public abstract void checkEnv(Object bootSource);
    
    /**
     * Get the transformer re-entrance lock for this service, the transformer
     * uses this lock to track transformer re-entrance when co-operative load
     * and transform is performed by the service.
     */
    public abstract ReEntranceLock getReEntranceLock();
    
    /**
     * Return the class provider for this service. <b>This component is required
     * and services must not return <tt>null</tt>.</b>
     */
    public abstract IClassProvider getClassProvider();
    
    /**
     * Return the class bytecode provider for this service. <b>This component is
     * required and services must not return <tt>null</tt>.</b>
     */
    public abstract IClassBytecodeProvider getBytecodeProvider();
    
    /**
     * Return the transformer provider for this service. <b>This component is
     * optional and is allowed to be <tt>null</tt> for services which do not
     * support transformers, or don't support interacting with them.</b>
     */
    public abstract ITransformerProvider getTransformerProvider();
    
    /**
     * Return the class tracker for this service. <b>This component is optional
     * and is allowed to be <tt>null</tt> for services which do not support this
     * functionality.</b>
     */
    public abstract IClassTracker getClassTracker();
    
    /**
     * Return the audit trail for this service. <b>This component is optional
     * and is allowed to be <tt>null</tt> for services which do not support this
     * functionality.</b>
     */
    public abstract IMixinAuditTrail getAuditTrail();
    
    /**
     * Get additional platform agents for this service 
     */
    public abstract Collection<String> getPlatformAgents();
    
    /**
     * Get the primary container for the current environment, this is usually
     * the container which contains the Mixin classes but can be another type
     * of container as required by the environment
     */
    public abstract IContainerHandle getPrimaryContainer();
    
    /**
     * Get a collection of containers in the current environment which contain
     * mixins we should process 
     */
    public abstract Collection<IContainerHandle> getMixinContainers();
    
    /**
     * Get a resource as a stream from the appropriate classloader, this is
     * delegated via the service so that the service can choose the correct
     * classloader from which to obtain the resource.
     * 
     * @param name resource path
     * @return input stream or null if resource not found
     */
    public abstract InputStream getResourceAsStream(String name);

    /**
     * Get the detected side name for this environment
     */
    public abstract String getSideName();
    
    /**
     * Get the minimum compatibility level supported by this service. Can return
     * <tt>null</tt> if the service has no specific minimum compatibility level,
     * however if a value is returned, it will be used as the minimum
     * compatibility level and no lower levels will be supported. 
     * 
     * @return minimum supported {@link CompatibilityLevel} or null
     */
    public abstract CompatibilityLevel getMinCompatibilityLevel();
    
    /**
     * Get the maximum compatibility level supported by this service. Can return
     * <tt>null</tt> if the service has no specific maximum compatibility level.
     * If a value is returned, a warning will be raised if a configuration
     * attempts to se a higher compatibility level.
     * 
     * @return minimum supported {@link CompatibilityLevel} or null
     */
    public abstract CompatibilityLevel getMaxCompatibilityLevel();
    
    /**
     * Retrieve a logger adapter with the specified name (id). In general this
     * method will be called many times for a given name so it is anticipated
     * that the returned logger instances are cached by the service.
     * 
     * <p>There is no contractual requirement however that adapters are cached
     * and that the same adapter is returned for every call to this method with
     * the same name.</p>
     * 
     * <p>This methood <em>must not return <tt>null</tt></em>.</p>
     * 
     * <p>Implementations should be thread-safe since loggers may be requested
     * by threads other than the main application thread.</p>
     * 
     * @param name Logger name
     * @return Logger adapter for the underlying logging subsystem
     */
    public abstract ILogger getLogger(final String name);

}
