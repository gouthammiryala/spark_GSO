package benchmark;

/**
 * Sphere function: f( x ) = sum_{i=1}^{n} { (x_i-1)^2 }
 * @author Simone Ludwig
 * @version 1.0
 */

public class Sphere {

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
		//System.out.print("-------->"+(position.length));
		for (int i = 0; i < (position.length); i++)
			result += position[i] * position[i];
		return result;
	}

	public double getMax() {
		return max;
	}

	public double getMin() {
		return min;
	}

}
