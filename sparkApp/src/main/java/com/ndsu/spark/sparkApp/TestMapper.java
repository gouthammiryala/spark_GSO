package com.ndsu.spark.sparkApp;

import java.util.List;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.broadcast.Broadcast;

import scala.Tuple2;

public class TestMapper implements PairFunction<String, String, Integer> {

String testBroadcast = "";
	public TestMapper(Broadcast<String> brCenters) { 
		   this.testBroadcast = brCenters.getValue(); 
		  // System.out.println(testBroadcast);
		  } 
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;

	@Override
	public Tuple2<String, Integer> call(String t) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(testBroadcast);
		return new Tuple2<String, Integer>(t, t.length());
	}
	

}
