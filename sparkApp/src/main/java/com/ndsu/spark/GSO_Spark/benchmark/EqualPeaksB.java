package com.ndsu.spark.GSO_Spark.benchmark;


/**
 * Equal Peaks function: 
 * f(x) = sum(sin^2(x))
 * @author Goutham Miryala
 * @version 1.0
 */

public class EqualPeaksB implements GSOBenchmark{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double min = -1.57;
	private double max = 1.57;

	/**
	 * Evaluates a particles at a given position
	 * 
	 * @param position
	 *            : Particle's position
	 * @return Fitness function for a particle
	 */
	public double evaluate(double position[]) {
		double result = 0.0;
		for (int i = 0; i < (position.length); i++)
			result += Math.pow(Math.sin(position[i]),2); 
		return result;
	}

	public double getMax() {
		return max;
	}

	public double getMin() {
		return min;
	}
	
//	public int getDimension() {
//		return dimension;
//	}
}
