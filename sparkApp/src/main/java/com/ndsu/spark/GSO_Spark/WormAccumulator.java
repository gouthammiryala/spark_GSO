package com.ndsu.spark.GSO_Spark;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.spark.util.AccumulatorV2;

import com.ndsu.spark.GSO_Spark.Beans.Worm;


class WormAccumulator extends AccumulatorV2<Worm, List<Worm>> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<Integer, Worm> swarmMap;
	
	public WormAccumulator() {
		swarmMap = new HashMap<Integer, Worm>();
	}
	
	@Override
	public void add(Worm arg0) {
		swarmMap.put(arg0.getID(), arg0);
		// TODO Auto-generated method stub
		
	}



	@Override
	public boolean isZero() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void merge(AccumulatorV2<Worm, List<Worm>> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public List<Worm> value() {
		// TODO Auto-generated method stub
		List<Worm> swarm =
			    new ArrayList<Worm>();
		
		swarm.addAll(swarmMap.values());
		return swarm;
	}

	@Override
	public AccumulatorV2<Worm, List<Worm>> copy() {
		// TODO Auto-generated method stub
		return null;
	}

	}
