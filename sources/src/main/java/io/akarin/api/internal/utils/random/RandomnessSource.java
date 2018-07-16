package io.akarin.api.internal.utils.random;

import java.io.Serializable;

/**
 * This interface defines the interactions required of a random number
 * generator. It is a replacement for Java's built-in Random because for
 * improved performance.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public interface RandomnessSource extends Serializable {

    /**
     * Using this method, any algorithm that might use the built-in Java Random
     * can interface with this randomness source.
     *
     * @param bits the number of bits to be returned
     * @return the integer containing the appropriate number of bits
     */
    int next(int bits);

    /**
     *
     * Using this method, any algorithm that needs to efficiently generate more
     * than 32 bits of random data can interface with this randomness source.
     *
     * Get a random long between Long.MIN_VALUE and Long.MAX_VALUE (both inclusive).
     * @return a random long between Long.MIN_VALUE and Long.MAX_VALUE (both inclusive)
     */
    long nextLong();

    /**
     * Produces a copy of this RandomnessSource that, if next() and/or nextLong() are called on this object and the
     * copy, both will generate the same sequence of random numbers from the point copy() was called. This just need to
     * copy the state so it isn't shared, usually, and produce a new value with the same exact state.
     * @return a copy of this RandomnessSource
     */
    RandomnessSource copy();
}