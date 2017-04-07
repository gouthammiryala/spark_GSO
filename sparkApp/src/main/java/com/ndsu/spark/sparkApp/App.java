package com.ndsu.spark.sparkApp;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.broadcast.Broadcast;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.ndsu.spark.GSO_Spark.Beans.Worm;

import scala.Tuple2;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.Accumulator;
import org.apache.spark.SparkConf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAccumulator;

/**
 * Hello world!
 *
 */
public class App 
{
	 public static void main(String[] args) throws JsonSyntaxException, JsonIOException, IOException {
		 
		 ArrayList<Worm> swarm = new ArrayList<Worm>();//Worm[gsoConfig.getSwarmSize()];
			File f = new File("D:\\NDSU\\DrSimones_Lab\\git\\sparkApp\\src\\main\\java\\com\\ndsu\\spark\\sparkApp\\part-00000");
			//for(File f : dir.listFiles()){
			BufferedReader br = new BufferedReader(new FileReader("D:\\NDSU\\DrSimones_Lab\\git\\sparkApp\\src\\main\\java\\com\\ndsu\\spark\\sparkApp\\part-00000"));

			try {
			    StringBuilder sb = new StringBuilder();
			    String line = br.readLine();

			    while (line != null) {
			    	Gson gson = new Gson();
			    	swarm.add(gson.fromJson(line, Worm.class));
			        line = br.readLine();
			    }
			   // String everything = sb.toString();
			} finally {
			    br.close();
			}
//					
//					Gson gson = new Gson();
//					Worm[] swarmTemp = gson.fromJson(new FileReader(f), Worm[].class);
//					swarm.addAll(Arrays.asList(swarmTemp));
//				}
			//}

			System.out.println(swarm.size());

	    }	 
	
}
