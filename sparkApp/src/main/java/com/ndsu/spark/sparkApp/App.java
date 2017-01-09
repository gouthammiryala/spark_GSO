package com.ndsu.spark.sparkApp;

import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.LongAccumulator;

/**
 * Hello world!
 *
 */
public class App 
{
	 public static void main(String[] args) {
		 
	     //   get("/hello", (req, res) -> "Hello World");
		 
		 System.setProperty("hadoop.home.dir", "D:\\NDSU\\DrSimones Lab");
		 SparkConf conf = new SparkConf().setAppName("testApp").setMaster("local");
		// conf.s
		 JavaSparkContext sc = new JavaSparkContext(conf);

		 sc.hadoopConfiguration().set("dfs.nameservices","hadooptest");

		 sc.hadoopConfiguration().set("dfs.namenode.name.dir", "file:/hadoop/data/dfs/namenode");
		 sc.hadoopConfiguration().set("dfs.datanode.data.dir","file:/hadoop/data/dfs/datanode");
		 sc.hadoopConfiguration().set("dfs.http.address","127.0.0.1:50070");
		// sc.hadoopConfiguration().set("","");
		// conf
		 //conf.set("fs.default.name", "hdfs://127.0.0.1:9000"); 
//		 List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
////		 JavaRDD<Integer> distData = sc.
//		 JavaRDD<Integer> distData = sc.parallelize(data);
//		 JavaPairRDD<Integer, Integer> testMapper = distData.mapToPair(new TestMapper());
//		 
//		 //		 int totalLength = distData.reduce((a,b) -> a - b);
//		 JavaPairRDD<Integer, Integer> totalLength = testMapper.reduceByKey(new TestReducer());
//		 
//		 int test = distData.reduce(new TestReducer());
//		 		// totalLength.aggregate(new TestReducer());
//		 int sum = 0;
//		    List<Tuple2<Integer, Integer>> output = totalLength.collect();
//		    for (Tuple2<?,?> tuple : output) {
//		    	//sum = sum+(tuple._2());
//		        //System.out.println(tuple._1() + ": " + tuple._2());
//		      }
//System.out.println("**********************Sum: "+sum);
//		 System.out.println("********************** countbyvalue = "+totalLength.countByKey());
//		
//		 System.out.println("********************** count = "+totalLength.count());

		// LongAccumulator n - 
		  
		 JavaRDD<String> lines = sc.textFile("hdfs://127.0.0.1:9000/testFiles/data.txt");
//		 
		 JavaPairRDD<String, Integer> pairs = lines.mapToPair(s -> new Tuple2(s, 1));
		 JavaPairRDD<String, Integer> counts = pairs.reduceByKey(new TestReducer());
		 System.out.println("counts.first()"+counts.first());
////		 counts.saveAsTextFile("d:\\NDSU\\DrSimones Lab\\test\\test.txt");
//		 //saveAsSequenceFile("d:\\test_seq.txt");
//
//		 
//		 System.out.println("counts.countByKey()"+counts.countByKey());
		 

	    }	 
	
}
