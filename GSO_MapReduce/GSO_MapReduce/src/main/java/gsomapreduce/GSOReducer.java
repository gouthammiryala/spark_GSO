package gsomapreduce;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

import benchmark.benchmark;

// calculate a new clustercenter for these vertices
public class GSOReducer extends Reducer<IntWritable, Text, IntWritable, Text> {
	// private static final Log LOG = LogFactory.getLog(KMeansReducer.class);
	int x = 0;
	int swarmSize;
	int wormDim;
	double p_const;
	double gama;
	double B;
	double nt;
	double step;
	double r0;
	double rs;
	double l0;
	String benchName;

	public static enum Counter1 {
		CONVERGED
	}

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		super.setup(context);
		Configuration conf = context.getConfiguration();
		FileSystem fs = FileSystem.get(conf);

		wormDim = Integer.parseInt(conf.get("worm.dim")); // dimensions
		swarmSize = Integer.parseInt(conf.get("swarm.size")); // swarm size
		p_const = Double.parseDouble(conf.get("p"));
		gama = Double.parseDouble(conf.get("gama"));
		B = Double.parseDouble(conf.get("B"));
		nt = Double.parseDouble(conf.get("nt"));
		step = Double.parseDouble(conf.get("step"));
		l0 = Double.parseDouble(conf.get("l0"));
		r0 = Double.parseDouble(conf.get("r0"));
		rs = Double.parseDouble(conf.get("rs"));
		benchName = conf.get("benchName");
	}

	@Override
	protected void reduce(IntWritable key, Iterable<Text> values,
			Context context) throws IOException, InterruptedException {

		worm newworm = new worm();
		worm tmp = new worm();
		double nbsize = 0;
		for (Text value : values) {
			String[] line = value.toString().split(";");
			if (line.length == 5) { // nb case
				double position[] = new double[wormDim];
				nbsize = Double.parseDouble(line[0]);
				String[] line1 = line[1].split(",");
				for (int a = 0; a < wormDim; a++) {
					position[a] = Double.parseDouble(line1[a]);
				}
				tmp.setPosition(position);
				tmp.setJx(Double.parseDouble(line[2]));
				tmp.setluc(Double.parseDouble(line[3]));
				tmp.setRd(Double.parseDouble(line[4]));
			} else // worm case
			{
				double position[] = new double[wormDim];
				String[] line0 = line[0].split(",");
				for (int a = 0; a < wormDim; a++) {
					position[a] = Double.parseDouble(line0[a]);
				}
				newworm.setID(Integer.parseInt("" + key));
				newworm.setPosition(position);
				newworm.setJx(Double.parseDouble(line[1]));
				newworm.setluc(Double.parseDouble(line[2]));
				newworm.setRd(Double.parseDouble(line[3]));
			}
		}
		// System.out.println("worm-->"+key+"  ");
		// newworm.printWorm();
		// System.out.println("nb-->"+key+" -- "+nbsize);
		// tmp.printWorm();

		if (nbsize != 0) {
			double Euclid;
			double sumEuclid = 0;
			double newposition[] = new double[wormDim];
			for (int a = 0; a < wormDim; a++) {
				sumEuclid = sumEuclid
						+ Math.pow(tmp.getposition()[a]
								- newworm.getposition()[a], 2);
			}
			Euclid = Math.sqrt(sumEuclid);

			for (int a = 0; a < wormDim; a++) {
				newposition[a] = newworm.getposition()[a]
						+ step
						* ((tmp.getposition()[a] - newworm.getposition()[a]) / Euclid);
			}

			newworm.setPosition(newposition);

			//double m = newworm.getRd() + B * (nt - nbsize);
			//double max = Math.max(0.0, m);
			//double newRd = Math.min(rs, max);
			//newworm.setRd(newRd);
			newworm.setRd(newworm.getRd());

		}

		benchmark bench = new benchmark(benchName, newworm.getposition()); 
		double Jx = bench.getr();
		newworm.setJx(Jx);
		double l = (1 - p_const) * newworm.getluc() + gama * newworm.getJx();
		newworm.setluc(l);

		String st = "";
		for (int w = 0; w < wormDim - 1; w++) {
			st = st + (newworm.getposition()[w] + ",");
		}
		st = st + (newworm.getposition()[wormDim - 1] + ";");
		st = st + (newworm.getJx() + ";");
		st = st + (newworm.getluc() + ";");
		st = st + (newworm.getRd() + ";");

		context.write(key, new Text(st));
	}

	@Override
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		super.cleanup(context);
	}
}
