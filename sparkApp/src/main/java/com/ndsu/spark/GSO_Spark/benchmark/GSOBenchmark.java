package com.ndsu.spark.GSO_Spark.benchmark;

public class GSOBenchmark {
	
	public double min;
	public double max;
	public double r;
	
	public GSOBenchmark(String bench,double px[])
	{
		
		if(bench.compareTo("CF4")==0)
		{
			CF4 s=new CF4();
			r=s.evaluate(px);
			min=s.getMin();
			max=s.getMax();
			
		}
		else if(bench.compareTo("Alpine")==0)
		{ //Alpine
			Alpine s= new Alpine();
			r=s.evaluate(px);
			min=s.getMin();
			max=s.getMax();
		}
		
		else if(bench.compareTo("peaks")==0)
		{ //peaks
			Peaks s= new Peaks();
			r=s.evaluate(px);
			min=s.getMin();
			max=s.getMax();
		}

		else if(bench.compareTo("Rosenbrock")==0)
		{ //peaks
			Rosenbrock s= new Rosenbrock();
			r=s.evaluate(px);
			min=s.getMin();
			max=s.getMax();
		}
		else if(bench.compareTo("ackley")==0)
		{ //peaks
			Ackley s= new Ackley();
			r=s.evaluate(px);
			min=s.getMin();
			max=s.getMax();
		}
		
		else if(bench.compareTo("equalpeaksA")==0)
		{ //peaks
			EqualPeaksA s= new EqualPeaksA();
			r=s.evaluate(px);
			min=s.getMin();
			max=s.getMax();
		}
		else if(bench.compareTo("Rastrigin")==0)
		{ //Rastrigin
			Rastrigin s= new Rastrigin();
			r=s.evaluate(px);
			min=s.getMin();
			max=s.getMax();
		}
		else if(bench.compareTo("Schaffer")==0)
		{ //Schaffer
			Schaffer s= new Schaffer();
			r=s.evaluate(px);
			min=s.getMin();
			max=s.getMax();
		}
		else if(bench.compareTo("Sphere")==0)
		{
			Sphere s=new Sphere();
			r=s.evaluate(px);
			min=s.getMin();
			max=s.getMax();
			
			
		}
		else if(bench.compareTo("SimpleQuad")==0)
		{
			SimpleQuad s=new SimpleQuad();
			r=s.evaluate(px);
			min=s.getMin();
			max=s.getMax();
			
		}
		
		
	}
	
	public GSOBenchmark(String bench)
	{
		if(bench.compareTo("CF4")==0)
		{
			CF4 s=new CF4();
//			r=s.evaluate(px);
			min=s.getMin();
			max=s.getMax();
			
		}
		else if(bench.compareTo("Rastrigin")==0)
		{ //Alpine
			Rastrigin s= new Rastrigin();
			min=s.getMin();
			max=s.getMax();
		}
		else if(bench.compareTo("Rosenbrock")==0)
		{ //peaks
			Rosenbrock s= new Rosenbrock();
		    min=s.getMin();
			max=s.getMax();
		}
		else if(bench.compareTo("ackley")==0)
		{ //peaks
			Ackley s= new Ackley();
			min=s.getMin();
			max=s.getMax();
		}
		else if(bench.compareTo("peaks")==0)
		{ //peaks
			Peaks s= new Peaks();
		    min=s.getMin();
			max=s.getMax();
		}
		else if(bench.compareTo("equalpeaksA")==0)
		{ //peaks
			EqualPeaksA s= new EqualPeaksA();
		    min=s.getMin();
			max=s.getMax();
		}
		else if(bench.compareTo("Schaffer")==0)
		{ //Alpine
			Schaffer s= new Schaffer();
			min=s.getMin();
			max=s.getMax();
		}
		else if(bench.compareTo("Alpine")==0)
		{ //Alpine
			Alpine s= new Alpine();
			min=s.getMin();
			max=s.getMax();
		}
		else if(bench.compareTo("Sphere")==0)
		{
			Sphere s=new Sphere();
			min=s.getMin();
			max=s.getMax();
			
			
		}
		else if(bench.compareTo("SimpleQuad")==0)
		{
			SimpleQuad s=new SimpleQuad();
			min=s.getMin();
			max=s.getMax();
			
		}
				
		
	}
	public double getMax() {
		return max;
	}

	public double getr() {
		return r;
	}

	public double getMin() {
		return min;
	}
	
}
