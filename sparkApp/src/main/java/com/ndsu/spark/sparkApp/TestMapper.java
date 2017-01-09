package com.ndsu.spark.sparkApp;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class TestMapper implements PairFunction<Integer, Integer, Integer> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public Tuple2<Integer, Integer> call(Integer t) throws Exception {
		// TODO Auto-generated method stub
		return new Tuple2(t, t);
	}

}
