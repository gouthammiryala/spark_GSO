package com.ndsu.spark.sparkApp;

import org.apache.spark.api.java.function.Function2;

public class TestReducer implements Function2<Integer, Integer, Integer>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	@Override
//	public Object call(Object v1, Object v2) throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public Integer call(Integer v1, Integer v2) throws Exception {
		// TODO Auto-generated method stub
		return v1+v2;
	}

}
