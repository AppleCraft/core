package com.applecraftserver.plugins.core.util;

public class MathUtils {

	public static int min(int... nums) {
		int min = Integer.MAX_VALUE;
		for (int num : nums) {
			min = Math.min(min, num);
		}
		return min;
	}

	public static boolean isInt(double f) {
		return Math.floor(f) == f;
	}
}
