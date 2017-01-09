package com.ndsu.spark.GSO_Spark.benchmark;

/**
 * Sphere function: f( x ) = sum_{i=1}^{n} { (x_i-1)^2 }
 * 
 * @author Simone Ludwig
 * @version 1.0
 */

public class Ackley {

	private double min = -32.768;
	private double max = 32.768;

	/**
	 * Evaluates a particles at a given position
	 * 
	 * @param position
	 *            : Particle's position
	 * @return Fitness function for a particle
	 */
	public double evaluate(double position[]) {
		double fvalue;

		fvalue = 0.0;

		double gvalue = 0.0;
		double hvalue = 0.0;
		for (int index = 0; index < position.length; index++) {
			gvalue = gvalue + Math.pow((position[index]), 2.0);
			hvalue = hvalue + Math.cos(position[index] * 2 * 3.14159265359);

		}

		fvalue = -20
				* Math.exp(-0.2 * Math.pow((gvalue / position.length), 0.5))
				- Math.exp(hvalue / position.length) + 20 + Math.exp(1);

		return fvalue;
	}

	public double getMax() {
		return max;
	}

	public double getMin() {
		return min;
	}

}
