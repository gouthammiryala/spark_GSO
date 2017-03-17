package com.ndsu.spark.GSO_Spark;

//import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;

import com.ndsu.spark.GSO_Spark.Beans.Worm;

public class GSOReducer implements Function2<Worm, Worm, Worm>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Worm call(Worm v1, Worm v2) throws Exception {
		// TODO Auto-generated method stub
		return v2;
	}
	

}


