package com.ndsu.spark.GSO_Spark.benchmark;



/**
 * Rosenbrock function: http://mathworld.wolfram.com/RosenbrockFunction.html f(
 * x1 , x2 ) = Sum i=1 to n ; 100*(x(i+1) - xi^2)^2 + (1 - xi)^2
 * @author Simone Ludwig
 * @version 1.0
 */

public class Rosenbrock{

	private double min = -2.048;
	private double max = 2.048;
	
	/**
	 * Evaluates a particles at a given position
	 * 
	 * @param position
	 *            : Particle's position
	 * @return Fitness function for a particle
	 */
	public double evaluate(double position[]) {
		double result = 0.0;
		for (int i = 0; i < (position.length - 1); i++)
			result += (100 * Math.pow((position[i+1] - Math.pow(position[i], 2)), 2) + Math.pow((1 - position[i]), 2));
		return result;
	}

	public double getMax() {
		return max;
	}

	public double getMin() {
		return min;
	}

}
