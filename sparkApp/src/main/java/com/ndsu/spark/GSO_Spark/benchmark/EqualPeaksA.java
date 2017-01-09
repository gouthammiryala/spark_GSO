package com.ndsu.spark.GSO_Spark.benchmark;


/**
 * Rastrigin's function: 
 * f(x) = sum([x.^2-10*cos(2*pi*x) + 10], 2);
 * @author Simone Ludwig
 * @version 1.0
 */

public class EqualPeaksA{

	private double min = -3.14;
	private double max = 3.14;

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
			result += Math.pow(Math.cos(position[i]),2); 
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
