package com.ndsu.spark.GSO_Spark;

import java.util.List;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;

import com.ndsu.spark.GSO_Spark.Beans.GSOConfig;
import com.ndsu.spark.GSO_Spark.Beans.Worm;
import com.ndsu.spark.GSO_Spark.benchmark.GSOBenchmark;

public class GSOMapper2 implements Function<Worm, Worm>{
	
	private GSOConfig gsoConfig;
	
	public GSOMapper2(GSOConfig gsoConfig){
		this.gsoConfig = gsoConfig;
	}

	@Override
	public Worm call(Worm worm) throws Exception {
		Worm newworm = worm;
		Worm tmp = worm.getNeightbourWorm();
		double nbsize = 0;
		nbsize = worm.getNeighbourWormSize();
		
		if (nbsize != 0) {
			double Euclid;
			double sumEuclid = 0;
			double newposition[] = new double[gsoConfig.getDimension()];
			for (int a = 0; a < gsoConfig.getDimension(); a++) {
				sumEuclid = sumEuclid
						+ Math.pow(tmp.getposition()[a]
								- newworm.getposition()[a], 2);
			}
			Euclid = Math.sqrt(sumEuclid);

			for (int a = 0; a < gsoConfig.getDimension(); a++) {
				newposition[a] = newworm.getposition()[a]
						+ gsoConfig.getStep()
						* ((tmp.getposition()[a] - newworm.getposition()[a]) / Euclid);
			}

			newworm.setPosition(newposition);

			//double m = newworm.getRd() + B * (nt - nbsize);
			//double max = Math.max(0.0, m);
			//double newRd = Math.min(rs, max);
			//newworm.setRd(newRd);
			newworm.setRd(newworm.getRd());

		}

		GSOBenchmark bench = new GSOBenchmark(gsoConfig.getBenchName(), newworm.getposition()); 
		double Jx = bench.getr();
		newworm.setJx(Jx);
		double l = (1 - gsoConfig.getP_const()) * newworm.getluc() + gsoConfig.getGamma() * newworm.getJx();
		newworm.setluc(l);

		return newworm;
	}

}
