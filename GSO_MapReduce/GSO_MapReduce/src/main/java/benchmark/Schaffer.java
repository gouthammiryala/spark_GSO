package benchmark;

/**
 * Schaffer function: f( x1 , x2 ) = 0.5 + ( sin( Sqrt( x1^2 + x2^2 ))^2 - 0.5 )
 * / (1 + 0.001 * ( x1^2 * x2^2 ))^2 (is only 2-dimensional!!!)
 * @author Simone Ludwig
 * @version 1.0
 */

public class Schaffer {

	private double min = -100.0;
	private double max = 100.0;

	/**
	 * Evaluates a particles at a given position
	 * 
	 * @param position
	 *            : Particle's position
	 * @return Fitness function for a particle
	 */
	public double evaluate(double position[]) {
		double x1 = position[0];
		double x2 = position[1];
		double sum = x1 * x1 + x2 * x2;

		return 0.5 + (Math.pow(Math.sin(Math.sqrt(sum)), 2) - 0.5)
				/ Math.pow(1 + 0.001 * sum, 2);
	}

	public double getMax() {
		return max;
	}

	public double getMin() {
		return min;
	}

}
