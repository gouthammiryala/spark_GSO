package com.ndsu.spark.GSO_Spark.benchmark;


/**
 * Rastrigin's function: 
 * f(x) = sum([x.^2-10*cos(2*pi*x) + 10], 2);
 * @author Simone Ludwig
 * @version 1.0
 */

public class Rastrigin{

	private double min = -5.12;
	private double max = 5.12;

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
			result += (position[i]*position[i]) - (10*Math.cos(2*Math.PI*position[i]));
		return result + 10*(position.length);
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
