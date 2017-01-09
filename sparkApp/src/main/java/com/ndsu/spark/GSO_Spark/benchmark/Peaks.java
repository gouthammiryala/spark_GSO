package com.ndsu.spark.GSO_Spark.benchmark;


public class Peaks{

	private double min = -3;
	private double max = 3;

	/**
	 * Evaluates a particles at a given position
	 * 
	 * @param position
	 * : Particle's position
	 * @return Fitness function for a particle
	 */
	public double evaluate(double position[]) {
		//double result = 0.0;
		double x=position[0];
		double y=position[1];
		//result=3.0 *((1-x)*(1-x)) *
		//	   Math.exp(-((x*x) + (y+1)*(y+1) )) -
		 //      10.0* ((x/5.0)-Math.pow(x, 3)- Math.pow(y, 5)) * 
		  //     (Math.exp(-((x*x) + (y*y))))-
	       //    (1/3.0)*(Math.exp(-((x+1)*(x+1)) + (y*y)));
		
		double a=3.0 * (1-x)*(1-x) *
				Math.exp(-(x*x + (y+1)*(y+1)))-
				10.0 * (x/5.0 - Math.pow(x,3) - Math.pow(y,5)) * 
                 Math.exp(-(x*x + y*y))- 
                 1/3.0 * Math.exp(-((x+1)*(x+1) + y*y));
		//System.out.println(a+"   "+result);
		return a;
	}

	
	public double getMax() {
		return max;
	}

	public double getMin() {
		return min;
	}

}
