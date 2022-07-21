package com.countgandi.engine.utils;

import java.util.Random;

public class PerlinNoise {

	private static int mid = Integer.MAX_VALUE / 2;
	private float AMPLITUDE;
	private int OCTAVES;
	private float ROUGHNESS;

	private Random random = new Random();
	private int seed;
	private float xOffset = 0;
	private float zOffset = 0;

	public PerlinNoise(float gridX, float gridZ, int vertexCount, float amplitude, int octaves, float roughness, int seed) {
		this.seed = seed;
		this.AMPLITUDE = amplitude;
		this.OCTAVES = octaves;
		this.ROUGHNESS = roughness;
		xOffset = mid + gridX * (vertexCount - 1);
		zOffset = mid + gridZ * (vertexCount - 1);
	}

	public float generateHeight(double x, double z) {
		double total = 0;
		double d = (double) Math.pow(2, OCTAVES - 1);
		for (int i = 0; i < OCTAVES; i++) {
			double freq = (double) (Math.pow(2, i) / d);
			double amp = (double) Math.pow(ROUGHNESS, i) * AMPLITUDE;
			total += getInterpolatedNoise((x + xOffset) * freq, (z + zOffset) * freq) * amp;
		}
		return (float) total;
	}

	private double getInterpolatedNoise(double x, double z) {
		int intX = (int) x;
		int intZ = (int) z;
		double fracX = x - intX;
		double fracZ = z - intZ;

		double v1 = getSmoothNoise(intX, intZ);
		double v2 = getSmoothNoise(intX + 1, intZ);
		double v3 = getSmoothNoise(intX, intZ + 1);
		double v4 = getSmoothNoise(intX + 1, intZ + 1);
		double i1 = interpolate(v1, v2, fracX);
		double i2 = interpolate(v3, v4, fracX);
		return interpolate(i1, i2, fracZ);
	}

	private double interpolate(double a, double b, double blend) {
		double theta = blend * Math.PI;
		double f = (double) ((1f - Math.cos(theta)) * 0.5F);
		return a * (1f - f) + b * f;
	}

	private double getSmoothNoise(int x, int z) {
		double corners = (getNoise(x - 1, z - 1) + getNoise(x + 1, z - 1) + getNoise(x - 1, z + 1) + getNoise(x + 1, z + 1)) / 16;
		double sides = (getNoise(x - 1, z) + getNoise(x, z - 1) + getNoise(x + 1, z) + getNoise(x, z + 1)) / 8;
		double center = getNoise(x, z) / 4;
		return corners + sides + center;
	}

	private double getNoise(int x, int z) {
		random.setSeed(x * 2345122457686785l + z * 34567867868315136l + seed);
		return random.nextDouble() * 8 - 4;
	}

}
