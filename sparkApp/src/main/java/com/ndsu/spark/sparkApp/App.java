package com.ndsu.spark.sparkApp;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.Optional;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.ndsu.spark.GSO_Spark.GSO_SparkMain;
import com.ndsu.spark.GSO_Spark.Beans.GSOConfig;
import com.ndsu.spark.GSO_Spark.Beans.Worm;
import com.ndsu.spark.GSO_Spark.benchmark.GSOBenchmark;
import com.ndsu.spark.GSO_Spark.utils.GSOBenchmarkHelper;

import scala.Function1;
import scala.Tuple2;
import scala.runtime.BoxedUnit;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.SparkConf;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;


/**
 * Hello world!
 *
 */
public class App 
{




	public static void main(String[] args) throws JsonSyntaxException, JsonIOException, IOException, Exception {

		String log4jConfigFile = "resources/log4j.properties";
		PropertyConfigurator.configure(log4jConfigFile);
				SparkConf sparkConf = new SparkConf().setAppName("GSO_Spark");//.setMaster("local[4]");


		HashMap<String, GSOConfig> gsoConfigMap = GSO_SparkMain.initialize();
		GSOConfig gsoConfig = gsoConfigMap.get("10000_2_200");
				SparkSession spark = SparkSession
						.builder()
						.appName("Java Spark SQL basic example")
						.config(sparkConf)
						.getOrCreate();
				JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());


//		SparkConf sparkConf = new SparkConf().setAppName("GSO_Spark").setMaster("local[24]");
//		sparkConf.set("spark.default.parallelism", sparkConf.get("spark.executor.instances", "24"));



//		JavaSparkContext sc = new JavaSparkContext(sparkConf);
		
	
		List<Worm> swarm = GSO_SparkMain.createInitialSwarm(gsoConfig);

		//		Worm w = new Worm(1, new double[]{1.0,2.0}, 0.5, 0.6, 0.7, 0);
		////		w.setNeighbourWorm(new Worm(0, new double[]{1.0,2.0}, 0.5, 0.6, 0.7, 0));
		//		swarm.add(w);


		//		 spark./
		GSOBenchmark benchmark = new GSOBenchmarkHelper(gsoConfig.getBenchName()).getBenchMark();
		//				benchmark = new GSOBenchmarkHelper(gsoConfig.getBenchName());
		for (Worm worm : swarm) {
			// bench = new GSOBenchmark(gsoConfig.getBenchName(),
			// worm.getposition());
			// double Jx = bench.getr();
			worm.setJx(benchmark.evaluate(worm.getposition()));
			double l = (1 - gsoConfig.getP_const()) * worm.getluc() + gsoConfig.getGamma() * worm.getJx();
			worm.setluc((1 - gsoConfig.getP_const()) * worm.getluc() + gsoConfig.getGamma() * worm.getJx());
		}

		System.out.println("Tesintggggggggggg");	
		
		
		
		
//		Dataset<Row> df  = spark.createDataFrame(swarm, Worm.class);
//		
//		df.show();
		
//		DataFrame df = sqlContext.createDataFrame(swarm, Worm.class).cache();
//		DataFrame df = sqlContext.read().json("input/swarm.json").repartition(4).cache();
//		df.take(1);
//		System.out.println(df.count());
//		
//		Dataset<Row> joined = df.crossJoin(df);
//		joined.filter()
//		joined.show();
//		joined.
//		joined.show();
		//sqlContext.conf().;
		
//		Dataset<Row> f =  joined.filter(new FilterFunction<Row>() {
//			
//			@Override
//			public boolean call(Row value) throws Exception {
//				if (value.getInt(0) != value.getInt(6)){
//					if(value.getDouble(2) >= value.getDouble(8)) {
//						return false;
//				}
//					double distance, sum = 0;
//					for (int a = 0; a < gsoConfig.getDimension(); a++) {
//						
//						sum = sum + Math.pow(((double)(value.getList(4).toArray()[a]) - (double)(value.getList(10).toArray()[a])), 2);					
//					}
//					
////						System.out.println("I am here   "+ sum);//+ "                       "+v1._1.getRd());
//
//					distance = Math.sqrt(sum); // Euclidean Distance
//
////					System.out.println("I am here   "+ distance+ "                       "+v1._1.getRd());
//					if (distance <  value.getDouble(5))
////						if(value.getDouble(2) < value.getDouble(8)) {
//							return true;
////					}
////					return true;
//				}
//				return false;				
//			}
//		});
		
		for(int i =0; i<gsoConfig.getMaxIteration(); i++){
			double start = System.currentTimeMillis();

//		sc.pa
		//df.show();
		JavaRDD<Worm> initialRDD = sc.parallelize(swarm, 24);
		JavaPairRDD<Worm, Worm> initialPairRDD = initialRDD.
				mapToPair(x -> new Tuple2<Worm, Worm>
				(x, new Worm(new double[]{-1,-1}, -1, -1, -1, 0)));
		

		JavaPairRDD<Worm, Worm> cartesianRDD = initialRDD.cartesian(initialRDD);
//		 JavaPairRDD<Worm> a = cartesianRDD.collect();
//		 System.out.println(cartesianRDD.collect());
		 
		 System.out.println("count pre");
		 JavaPairRDD<Worm, Worm> neighborsRDD = cartesianRDD.filter(new Function<Tuple2<Worm,Worm>, Boolean>() {
			
			@Override
			public Boolean call(Tuple2<Worm, Worm> v1) throws Exception {
				// TODO Auto-generated method stub
				if (v1._1.getID() != v1._2.getID()){
					double distance, sum = 0;
					for (int a = 0; a < gsoConfig.getDimension(); a++) {
						sum = sum + Math.pow(v1._2.getposition()[a] - v1._1.getposition()[a], 2);					
					}
					distance = Math.sqrt(sum); // Euclidean Distance
					if (distance <  v1._1.getRd())
						if(v1._1.getluc() < v1._2.getluc()) {
							return true;
						}
				}
				return false;
			}
		 });//.red
		 
//		 neighborsRDD.red
		 
		 
		 System.out.println("initial count: "+neighborsRDD.count());
		 
		 
		 
//		 JavaPairRDD<Worm, Worm> neighborRDD = 
//				 neighborsRDD.mapToPair(new PairFunction<Tuple2<Worm,Iterable<Worm>>, Worm, Worm>() {
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public Tuple2<Worm, Worm> call(Tuple2<Worm, Iterable<Worm>> arg0) throws Exception {
//				// TODO Auto-generated method stub
//				List<Worm> neighborWorms = new ArrayList<>();
//				Worm worm = arg0._1;		
//				arg0._2.iterator().forEachRemaining(neighborWorms::add);
//				
//				double[] p = new double[neighborWorms.size()];
//				double acsum = 0;
//				for (int c = 0; c < neighborWorms.size(); c++) {
//					acsum = acsum
//							+ Math.abs(neighborWorms.get(c).getluc()
//									- worm.getluc());
//				}
//
//				for (int c = 0; c < neighborWorms.size(); c++) {
//					p[c] = Math.abs(neighborWorms.get(c).getluc() - worm.getluc())
//							/ acsum;
//				}
//
//
//				int index = 0;
//				Random aRandom = new Random();
//				double randn = aRandom.nextDouble();
//				double high = p[0];
//				double summ = 0.0;
//				int ii = 0;
//				while (summ <= randn) {
//					summ = summ + p[ii];
//					++ii;
//				}
//				index = ii - 1;
//				Worm neighborWorm = neighborWorms.get(index);
////				neighbourWorm.setNeightbourWorm(null);
////				worm.setNeightbourWorm(neighbourWorm);
//				worm.setNeighbourWormSize(neighborWorms.size());
//				Tuple2<Worm, Worm> tuple = new Tuple2<Worm, Worm> (worm, neighborWorm);
//				return tuple;
//			}
//		}).union(initialPairRDD);
//				 
//				 
////				 rightOuterJoin(initialPairRDD).collectAsMap().entrySet()
////				 .forEach(x -> System.out.println(x.getKey()+" \nneighbor: "+x.getValue()._1));
//				 
//				 //rightOuterJoin(initialPairRDD);
//		 Map<Worm, Worm > map = neighborRDD.distinct().collectAsMap();
//		 System.out.println(" count: "+map.size());
////		 map.entrySet().forEach(x -> System.out.println(x.getValue().getID()
////				 +" : "+x.getValue().getID()));
//		 System.out.println(" count: "+neighborRDD.distinct().count());
//		 int a = neighborRDD.collectAsMap().
//		 neighborRDD.collectAsMap().entrySet()
//				 .forEach(x -> System.out.println(x.getKey()+" \nneighbor: "+x.getValue()._1));
		 
//		 System.out.println("post count: "+a);
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
//		sc.parallelize(list)
//
//		//		f.
//		//		11
//
//		//		System.out.println(f.count());
//
//		//		 Dataset<Row> ds1 = spark.createDataFrame(a, Integer.class).toDF();
//		//		 dataset.foreach(x -> x.);
//		//		 dataset.foreach(new ForeachFunction<Row>() {
//		//			
//		//	
//		//			/**
//		//			 * 
//		//			 */
//		//			private static final long serialVersionUID = 1L;
//		//
//		//			@Override
//		//			public void call(Row t) throws Exception {
//		//				// TODO Auto-generated method stub
//		//				
//		//				
//		//			}
//		//		});
//		//		 System.out.println("************initial count: "+initialRDD.count());
//
//
//
//		//		 System.out.println("************initial count: "+initialRDD.count());
//
//		//		 JavaPairRDD<Worm, Iterable<Worm>> cartesianRDD
////		int iteration =0;
////		while ( iteration < gsoConfig.getMaxIteration()){
////			double start = System.currentTimeMillis();
//		
//		initialRDD.cartesian(initialRDD).cache();
//			JavaPairRDD<Worm, Tuple2<com.google.common.base.Optional<Iterable<Worm>>, Iterable<Worm>>> neighborRDD = initialRDD.cartesian(initialRDD)
//					.filter(new Function<Tuple2<Worm,Worm>, Boolean>() {					
//						@Override
//						public Boolean call(Tuple2<Worm, Worm> v1) throws Exception {						
//							if (v1._1.getID() != v1._2.getID()){
//								double distance, sum = 0;
//								for (int a = 0; a < gsoConfig.getDimension(); a++) {
//									sum = sum + Math.pow(v1._2.getposition()[a] - v1._1.getposition()[a], 2);					
//								}
//								distance = Math.sqrt(sum); // Euclidean Distance
//								if (distance <  v1._1.getRd())
//									if(v1._1.getluc() < v1._2.getluc()) {
//										return true;
//									}
//							}
//							return false;
//						}
//					}).groupByKey().rightOuterJoin(initialPairRDD).cache();
//			
////			JavaPairRDD a = neighborRDD.mapValues(new Func);
//			System.out.println(neighborRDD.count());
//			
//			neighborRDD.checkpoint();
			
//			neighborRDD.pair
			
//			neighborRDD.reduceByKey(new Function2<Tuple2<Optional<Iterable<Worm>>,Iterable<Worm>>, Tuple2<Optional<Iterable<Worm>>,Iterable<Worm>>, Tuple2<Optional<Iterable<Worm>>,Iterable<Worm>>>() {
//				
//				@Override
//				public Tuple2<com.google.common.base.Optional<Iterable<Worm>>, Iterable<Worm>> call(
//						Tuple2<com.google.common.base.Optional<Iterable<Worm>>, Iterable<Worm>> arg0,
//						Tuple2<com.google.common.base.Optional<Iterable<Worm>>, Iterable<Worm>> arg1) throws Exception {
//					// TODO Auto-generated method stub
//					return null;
//				}
//
//
//			});
//			System.out.println("***************Count: "+cartesianRDD.countByKey().size());
			//cartes
//			System.out.println("Count: "+cartesianRDD.rightOuterJoin(initialPairRDD).count());
//			neighborRDD.map(new Function<Tuple2<Worm,Iterable<Worm>>, Tuple2<Worm,Worm>>() {
//			});

////			foreach(new VoidFunction<Tuple2<Worm,Iterable<Tuple2<Optional<Iterable<Worm>>,Iterable<Worm>>>>>() {
////
////				@Override
////				public void call(Tuple2<Worm, Iterable<Tuple2<Optional<Iterable<Worm>>, Iterable<Worm>>>> arg0)
////						throws Exception {
////					// TODO Auto-generated method stub
////
////				}
////			});
//			//				 .foreach(new VoidFunction<Tuple2<Worm,Iterable<Tuple2<Optional<Iterable<Worm>>,Iterable<Worm>>>>>() {
//			//					
//			//					@Override
//			//					public void call(Tuple2<Worm, Iterable<Tuple2<Optional<Iterable<Worm>>, Iterable<Worm>>>> t) throws Exception {
//			//						// TODO Auto-generated method stub
//			////						System.out.print("Worm: "+t._1.getID()+"     ");
//			//						t._2.forEach(new Consumer<Tuple2<Optional<Iterable<Worm>>,Iterable<Worm>>>() {
//			//
//			//							@Override
//			//							public void accept(Tuple2<Optional<Iterable<Worm>>,Iterable<Worm>> tb) {
//			//								// TODO Auto-generated method stub
//			////								System.out.print("** ");
//			////								System.out.print(tb._2);
//			////								System.out.print(tb._1);
//			//
//			//								if (tb._1.isPresent()) 
//			//								{
//			////									System.out.println("*********");
//			////									System.out.print(tb._1);
//			//								}
//			//							}
//			//						});
//			//						t._2.forEach(a -> a._2.forEach(b -> System.out.print(t._1.getID()+ "      "+b.getID())));
//			//iterator().forEachRemaining(a -> a._2.forEach(b -> System.out.print(b.getID())));
//			//						System.out.print("\n");
////		}
////	});
	System.out.println("*************************************Total Time: "+(System.currentTimeMillis() - start));}
//
//	iteration = iteration++;
//}
		
//





//		 cartesianRDD.fla
// JavaPairRDD<Worm, Worm> reducedRDD = cartesianRDD.reduceByKey((x,y) -> y);
//	 reducedRDD.foreach(x -> System.out.println("**********"+x._1.toString()+"           "+ x._2.toString()));

// cartesianRDD.mapToPair((x,y) -> new Tuple2<Worm, List<Worm>>(x, )));


//			B
//			
//			spark.sparkContext().broadcast(value, evidence$11)
}	 

public void findNeighbors(Worm worm){

}




}
