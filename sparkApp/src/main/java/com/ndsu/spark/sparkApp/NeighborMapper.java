package com.ndsu.spark.sparkApp;

import org.apache.spark.api.java.function.Function;

import com.ndsu.spark.GSO_Spark.Beans.Worm;

public class NeighborMapper implements Function<Worm, Worm>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Worm worm;
	private int dimensions;

	public NeighborMapper(Worm worm, int dimensions) {
		this.worm = worm;
		this.dimensions = dimensions;
	}

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

}
