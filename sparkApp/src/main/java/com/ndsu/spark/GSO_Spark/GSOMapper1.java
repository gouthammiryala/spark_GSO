package com.ndsu.spark.GSO_Spark;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;

import com.ndsu.spark.GSO_Spark.Beans.GSOConfig;
import com.ndsu.spark.GSO_Spark.Beans.Worm;

import scala.Tuple2;

public class GSOMapper1 implements Function<Worm, Worm>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(GSOMapper1.class);
	private GSOConfig gsoConfig;
	List<Worm> swarm;
	Broadcast<List<Worm>> brSwarm;

	
	public GSOMapper1(GSOConfig gsoConfig, Broadcast<List<Worm>> brSwarm){
	//	System.out.println("********************YO! I AM HERE*******************************************");
		this.gsoConfig = gsoConfig;
		this.swarm = brSwarm.getValue();
	}

	@Override
	public Worm call(Worm worm) throws Exception {
		//Calculating Euclidian distance between the given worm and all the other worms in swarm
		//to find neighbors of the given worm
	//	logger.setLevel(Level.DEBUG);
//		logger.debug("************worm: "+worm.getID());
		
		List<Worm> neighborWorms = new ArrayList<Worm>();
		for (Worm wormInSwarm : swarm){			
			if (worm.getID() != wormInSwarm.getID()){
				double distance, sum = 0;
				for (int a = 0; a < gsoConfig.getDimension(); a++) {
					sum = sum + Math.pow(wormInSwarm.getposition()[a] - worm.getposition()[a], 2);					
				}
				distance = Math.sqrt(sum); // Euclidean Distance

				if (distance < worm.getRd()
						&& worm.getluc() < wormInSwarm.getluc()) {
					neighborWorms.add(wormInSwarm);
				}
			}
			
			if (neighborWorms.size()!= 0) {
				double[] p = new double[neighborWorms.size()];
				double acsum = 0;

				for (int c = 0; c < neighborWorms.size(); c++) {
					acsum = acsum
							+ Math.abs(neighborWorms.get(c).getluc()
									- worm.getluc());
				}

				for (int c = 0; c < neighborWorms.size(); c++) {
					p[c] = Math.abs(neighborWorms.get(c).getluc() - worm.getluc())
							/ acsum;
				}

				int index = 0;
				Random aRandom = new Random();
				double randn = aRandom.nextDouble();
				double high = p[0];
				double summ = 0.0;
				int ii = 0;
				while (summ <= randn) {
					summ = summ + p[ii];
					++ii;
				}
				index = ii - 1;
				Worm neighbourWorm = neighborWorms.get(index);
				neighbourWorm.setNeightbourWorm(null);
				worm.setNeightbourWorm(neighbourWorm);
				worm.setNeighbourWormSize(neighborWorms.size());
			}

			else {
				worm.setNeightbourWorm(new Worm(new double[]{-1,-1}, -1, -1, -1, neighborWorms.size()));				
			}
			
		}
		return worm;
	}

}
