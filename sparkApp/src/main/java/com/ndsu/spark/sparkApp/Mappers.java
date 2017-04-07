package com.ndsu.spark.sparkApp;

import java.io.Serializable;

import org.apache.spark.api.java.function.Function;

import com.ndsu.spark.GSO_Spark.Beans.Worm;

public class Mappers implements Serializable{

	public static <T> Function<Worm, Boolean> neighbors(Worm worm, int dimensions)
	{
		return new Function<Worm, Boolean>() {
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		
			@Override
			public Boolean call(Worm wormInSwarm) throws Exception {
				// TODO Auto-generated method stub
				//Worm neighborWorm = null;
				if (worm.getID() != wormInSwarm.getID()){
					double distance, sum = 0;
					for (int a = 0; a < dimensions; a++) {
						sum = sum + Math.pow(wormInSwarm.getposition()[a] - worm.getposition()[a], 2);					
					}
					distance = Math.sqrt(sum); // Euclidean Distance

					if (distance < worm.getRd()
							&& worm.getluc() < wormInSwarm.getluc()) {
						return true;
					}
					
				}
				return false;
			}
		  };
	}
	
	public <T> Function<Worm, Worm> euclidianDistance(Worm worm, int dimensions)
	{
		return new Function<Worm, Worm>() {
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		
			@Override
			public Worm call(Worm wormInSwarm) throws Exception {
				// TODO Auto-generated method stub
				//Worm neighborWorm = null;
				if (worm.getID() != wormInSwarm.getID()){
					double distance, sum = 0;
					for (int a = 0; a < dimensions; a++) {
						sum = sum + Math.pow(wormInSwarm.getposition()[a] - worm.getposition()[a], 2);					
					}
					distance = Math.sqrt(sum); // Euclidean Distance

					if (distance < worm.getRd()
							&& worm.getluc() < wormInSwarm.getluc()) {
						return wormInSwarm;
					}
					
				}
				return null;
			}
		  };
	} 

}
