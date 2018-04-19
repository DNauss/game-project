package com.github.core.util;

/**
 * Generic methods to abstract usage of the java Math class, this is to improve code readability due to less casting in
 * the actual application code.
 * 
 * Additionally, these methods are placeholders until a good work around is found.
 * 
 * 
 * @author David
 *
 */
public class Utils {
	
	private static final float PI_180 = (float) (Math.PI / 180.0f);
		
	public static float toRadians(float angdeg) {
		return angdeg * PI_180;
		//return (float) Math.toRadians(angdeg);
		//return angdeg / 180.0 * PI;
	}

	
	public static int floor(final double a) {
		int ai = (int) a;
		return a < ai ? ai - 1 : ai;
		//return (int) Math.floor(a);
	}
	
	public static float sin(float a) {
		return (float) Math.sin(a);
	}
	
	public static float cos(float a) {
		return (float) Math.cos(a);
	}

	public static double tan(double angdeg) {
		return Math.tan(angdeg);
	}
	
	public static final float invSqrt(float a) {
		final float half = 0.5F * a;
		int i = Float.floatToIntBits(a);
		i = 0x5f375a86 - (i >> 1);
		a = Float.intBitsToFloat(i);
		return a * (1.5F - half * a * a);
	}

	public static float sqrt(float a) {
		return 1f / invSqrt(a);
		//return (float) Math.sqrt(a);
	}
	
	public static float clamp(float v, float min, float max) {
		return v < min ? min : v > max ? max : v;
	}

	public static float ternary(boolean bool, float var) {
		return bool ? var : -var;
	}
		

}
