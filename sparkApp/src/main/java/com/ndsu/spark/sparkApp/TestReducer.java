package com.ndsu.spark.sparkApp;

import org.apache.spark.api.java.function.Function2;

import scala.Tuple2;
//public class TestReducer implements Function2<Integer, Integer, Integer>{
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//
//	@Override
//	public Object call(Object v1, Object v2) throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}


//	@Override
//	public Tuple2<String, Integer> call(Tuple2<String, Integer> v1, Tuple2<String, Integer> v2) throws Exception {
//		// TODO Auto-generated method stub
//		System.out.println(v2._1()+" : "+v2._2());
//		 //System.out.println(" : "+v2._2());
//		return new Tuple2<String, Integer>(v1._1(), v1._2()+v2._2());
//	}

//@Override
//public Integer call(Integer v1, Integer v2) throws Exception {
//	// TODO Auto-generated method stub
//	System.out.println("v1: "+v1+"    v2: "+v2);
//	return v1+v2;
//}
//
//}
public class TestReducer implements Function2<Tuple2<String,Integer>,Tuple2<String,Integer>,Tuple2<String,Integer>>{

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
	public Tuple2<String, Integer> call(Tuple2<String, Integer> v1, Tuple2<String, Integer> v2) throws Exception {
		// TODO Auto-generated method stub
		//Brod
		System.out.println(v1._1()+" : "+v1._2());
		System.out.println(v2._1()+" : "+v2._2());
		 //System.out.println(" : "+v2._2());
		return new Tuple2<String, Integer>(v2._1(), v1._2()+v2._2());
	}

}
