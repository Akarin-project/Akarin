package io.akarin.api.internal.utils.random;

/**
 * A simple interface for RandomnessSources that have the additional property of a state that can be re-set.
 * Created by Tommy Ettinger on 9/15/2015.
 */
public interface StatefulRandomness extends RandomnessSource {
    /**
     * Get the current internal state of the StatefulRandomness as a long.
     * @return the current internal state of this object.
     */
    long getState();

    /**
     * Set the current internal state of this StatefulRandomness with a long.
     *
     * @param state a 64-bit long. You should avoid passing 0, even though some implementations can handle that.
     */
    void setState(long state);
}