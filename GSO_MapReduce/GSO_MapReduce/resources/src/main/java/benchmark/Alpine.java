package benchmark;

/**
 * Alpine function: Math.abs((x1 * Math.sin(x1) + 0.1 * x1)
 *				+ (x2 * Math.sin(x2) + 0.1 * x2)) 
 * @author Simone Ludwig
 * @version 1.0
 */

public class Alpine{

	private double min = -10.0;
	private double max = 10.0;
	
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
			result += Math.abs((position[i] * Math.sin(position[i]) + 0.1 * position[i]));
		return result;
	}

	public double getMax() {
		return max;
	}

	public double getMin() {
		return min;
	}

}
