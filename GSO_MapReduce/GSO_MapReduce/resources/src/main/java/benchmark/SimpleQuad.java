package benchmark;


/**
 * Sphere function: f( x ) = sum_{i=1}^{n} { (x_i-1)^2 }
 * @author Simone Ludwig
 * @version 1.0
 */

public class SimpleQuad{

	private double min = 0;
	private double max = 5;

	/**
	 * Evaluates a particles at a given position
	 * 
	 * @param position
	 *            : Particle's position
	 * @return Fitness function for a particle
	 */
	public double evaluate(double position[]) {
		double result = 0.0;
		result=Math.pow((2.8125 - position[0] + position[0] * Math.pow(position[1], 4)), 2) +
		       Math.pow((2.25 - position[0] + position[0] * Math.pow(position[1], 2)), 2) +
	           Math.pow((1.5 - position[0] + position[0] * position[1]), 2);
		return result;
	}

	public double getMax() {
		return max;
	}

	public double getMin() {
		return min;
	}

}
