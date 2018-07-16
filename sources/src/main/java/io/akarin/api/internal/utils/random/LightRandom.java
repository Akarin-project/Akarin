package io.akarin.api.internal.utils.random;

import java.util.Random;

/**
 * This is a "fake" LightRandom, backed by the LightRNG.
 * 
 * This is useful if you want to quickly replace a Random variable with
 * LightRNG without breaking every code.
 */
public class LightRandom extends Random {
	public static LightRNG light = new LightRNG(); // LightRNG, static.
	
	private static final long serialVersionUID = 1L;

	@Override
	public int next(int bits) {
		return light.next(bits);
	}
	
	@Override
	public void nextBytes(byte[] bytes) {
		light.nextBytes(bytes);
	}
	
	@Override
	public int nextInt() {
		return light.nextInt();
	}
	
	@Override
	public int nextInt(int n) {
		return light.nextInt(n);
	}
	
	@Override
	public long nextLong() {
		return light.nextLong();
	}
	
	@Override
	public boolean nextBoolean() {
		return light.nextBoolean();
	}
	
	@Override
	public float nextFloat() {
		return light.nextFloat();
	}
	
	@Override
	public double nextDouble() {
		return light.nextDouble();
	}
	
	// Akarin start
	@Override
	public void setSeed(long seed) {
        light.setSeed(seed);
    }
	// Akarin end
}