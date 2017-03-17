package com.ndsu.spark.GSO_Spark.benchmark;

public interface GSOBenchmark {
	
//	public double min =0;
//	public double max =0;
//	public double r =0;
	
	public double evaluate(double position[]);
	
	public double getMax();

	public double getMin();
	
	//public double evaluate(double position[]);
	
}
