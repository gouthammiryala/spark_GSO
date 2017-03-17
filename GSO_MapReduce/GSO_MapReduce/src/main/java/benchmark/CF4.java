package benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

public class CF4 {

	private double min = -5.0;
	private double max = 5.0;
	
	double [] functionsFN;
	int nofunc_=8;
	double C_;// = 2000.0;
	double [] lambda_;
	double [] sigma_;
	double [] bias_;
	double [][] O_;
	double [][][] M_;
	double [] weight_;
	double [] fi_;
	double [] z_;
	double f_bias_=0;
	double [] fmaxi_;
	double [] tmpx_;

	
	private void initializeCFunc(int dim) {
		C_ 		= 2000.0;
		f_bias_ = 0;
		lambda_ = new double[nofunc_];
		sigma_  = new double[nofunc_];
		bias_   = new double[nofunc_];
		O_  	= new double[nofunc_][dim];
		M_		= new double[nofunc_][dim][dim];
		weight_ = new double[nofunc_];
		fi_ 	= new double[nofunc_];
		z_  	= new double[dim];
		fmaxi_  = new double[nofunc_];
		tmpx_   = new double[dim];
	}
	
	public double evaluate(double position[]) {
		//for (int i=0; i<position.length; ++i) {
		//	System.out.print(" p: "+position[i]);
						
	    //}
		
		initializeCFunc(position.length);
		double fvalue;
		fvalue = 0.0;
			
		for (int i=0; i<nofunc_; ++i) {
		      //  sigma_[i] = 1.0;
		        bias_[i]  = 0.0;
		        weight_[i]= 0.0;
		    }
		
		sigma_[0] = 1.0;
	    sigma_[1] = 1.0;
	    sigma_[2] = 1.0;
	    sigma_[3] = 1.0;
	    sigma_[4] = 1.0;
	    sigma_[5] = 2.0;
	    sigma_[6] = 2.0;
	    sigma_[7] = 2.0;
	    lambda_[0] = 4.0;
	    lambda_[1] = 1.0;
	    lambda_[2] = 4.0;
	    lambda_[3] = 1.0;
	    lambda_[4] = 1.0/10.0;
	    lambda_[5] = 1.0/5.0;
	    lambda_[6] = 1.0/10.0;
	    lambda_[7] = 1.0/40.0;
		
		/* funcs_.add(new FRastrigin(boundsFn) );*/
		
	    String fname;
	    fname = "mrresources/data/CF4_M_D" + position.length + "_opt.dat";
//	    fname = "data/CF4_M_D" + position.length + "_opt.dat";
        loadOptima(fname,position.length);
	    fname = "mrresources/data/CF4_M_D" + position.length + ".dat";
//	    fname = "data/CF4_M_D" + position.length + ".dat";
	    loadRotationMatrix(fname,position.length);
	    
	  //  System.out.println("=============================");
	    CalculateFMaxi(position.length);
	   // System.out.println("=============================");
	    //System.out.println("     fvalue  ");
	    fvalue=evaluateInner_(position,position.length);
	    //System.out.println(fvalue);
	    
	    /*	System.out.println("     zzzzz  ");
			System.out.println("z_length"+z_.length);
			for (int i=0; i<position.length; ++i) {
				System.out.println(z_[i]);
				
		    }
			
			
			System.out.println("     kkkkkkkkk  ");
			System.out.println(fmaxi_[0]);
		    
		  
		    funcs_.add(new FRastrigin(boundsFn) );
		    funcs_.add(new FEF8F2(boundsFn) );
		    funcs_.add(new FEF8F2(boundsFn) );
		    funcs_.add(new FWeierstrass(boundsFn) );
		    funcs_.add(new FWeierstrass(boundsFn) );
		    funcs_.add(new FGriewank(boundsFn) );
		    funcs_.add(new FGriewank(boundsFn) );
		  * 
		  * 
		  * 
		  * */
	    //SL
//	    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ fvalue: " + fvalue);
	    
		return fvalue;
	}

	//FRastrigin
	double doEvaluateFRastrigin(double[] position) {
		double result = 0.0;
	    for (int i=0; i<position.length; ++i) {
	        result += (position[i]*position[i] - 10.0*Math.cos(2.0*Math.PI*position[i]) + 10.0);
	    }
	    return result;
	}//FRastrigin
	
	//FWeierstrass
	double doEvaluateFWeierstrass(double[] position) {
	    double result=0.0, sum=0.0, sum2=0.0, a=0.5, b=3.0;
	    int k_max=20;

	    for (int j=0; j<=k_max; ++j) {
	        sum2 += Math.pow(a,j)*Math.cos(2.0*Math.PI*Math.pow(b,j)*(0.5));
	    }
	    for (int i=0; i<position.length; ++i) {
	        sum = 0.0;
	        for (int j=0; j<=k_max; ++j) {
	            sum += Math.pow(a,j)*Math.cos(2.0*Math.PI*Math.pow(b,j)*(position[i]+0.5));
	        }
	        result += sum;
	    }
	    return result - sum2*position.length;
	} //FWeierstrass
	
	//FGriewank
	double doEvaluateFGriewank(double[] position) {
	    double sum=0.0, prod=1.0, result=0.0;

	    for (int i=0; i<position.length; ++i) {
	        sum  += position[i]*position[i]/4000.0;
	        prod *= Math.cos( position[i]/Math.sqrt((1.0+i)) );
	    }
	    result = 1.0 + sum - prod;
	    return result;
	} //FGriewank
	
	//FEF8F2	
	double doEvaluateFEF8F2(double[] position) {
		    double result=0.0;
		    double x=0, y=0, f=0, f2=0;

		    for (int i=0; i<position.length-1; ++i) {
		        x = position[i]   +1;
		        y = position[i+1] +1;

		        f2 = 100.0*(x*x - y)*(x*x - y) + (1.0 - x)*(1.0 - x);
		        f  = 1.0 + f2*f2/4000.0 - Math.cos(f2);

		        result += f;
		    }
		    /* do not forget the (dim-1,0) case! */
		    x = position[position.length-1] +1;
		    y = position[0]     +1;

		    f2 = 100.0*(x*x - y)*(x*x - y) + (1.0 - x)*(1.0 - x);
		    f  = 1.0 + f2*f2/4000.0 - Math.cos(f2);

		    result += f;

		    return result;
		} //FEF8F2
	
	
	
	void loadOptima(final String filename,int dim) 
	{
		File file = new File(filename); 
		try {
			LineNumberReader reader = new LineNumberReader( new FileReader( file ) );
			try {
				String buffer;	
				try {
					for (int i=0; i<this.nofunc_; ++i) {
						buffer = reader.readLine();
						
						double tmp = 0;
						String[] numbers = buffer.split("\t");
						for( int j = 0; j < dim; ++j ) {
							tmp = Double.parseDouble(numbers[j].trim());
							O_[i][j] = tmp;
						}
					}
				} catch (NumberFormatException e) {
					System.err.println("Error: loadOptima, NumberFormatException: " + e.toString());
					e.printStackTrace();
				} catch (IOException e) {
					System.err.println("Error: loadOptima, IOException: " + e.toString());
					e.printStackTrace();
				}
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					System.err.println("Error: loadOptima, Can not close file: IOException: " + e.toString());
					e.printStackTrace();
				}			
			}
		} catch (FileNotFoundException e) {
			System.err.println("Error: loadOptima, Can not find file: " + filename +e.toString());
			e.printStackTrace();
		} 
	}
	
	
	void transformToZ(final double []x, final int index,int dim)
	{
		/* Calculate z_i = (x - o_i)/\lambda_i */
		for (int i=0; i<dim; ++i) {
			tmpx_[i] = (x[i] - O_[index][i])/lambda_[index];
		}
		/* Multiply z_i * M_i */
		for (int i=0; i<dim; ++i) {
			z_[i] = 0;
			for (int j=0; j<dim; ++j) {
				/* in MATLAB: M.M1*tmpx' */
				//z_[i] += M_[index][i][j] * tmpx_[j];

				/* in MATLAB: tmpx*M.M1 */
				z_[i] += M_[index][j][i] * tmpx_[j];
			}
			//System.out.println("i: "+i+" "+tmpx_[i]+" "+x[i]+" "+O_[index][i]+" "+lambda_[index]+" "+z_[i]);
		}
	}
	
	
	void transformToZNoshift(int dim,final double []x, final int index)
	{
		/* Calculate z_i = (x - o_i)/\lambda_i */
		for (int i=0; i<dim; ++i) {
			//tmpx_[i] = (x[i] - O_[index][i])/lambda_[index];
			tmpx_[i] = (x[i])/lambda_[index];
			//System.out.println("inside trans");
			//System.out.println(tmpx_[i]);
		}
		/* Multiply z_i * M_i */
		for (int i=0; i<dim; ++i) {
			z_[i] = 0;
			for (int j=0; j<dim; ++j) {
				z_[i] += M_[index][j][i] * tmpx_[j];
			}
		}
	}
	
	
	void calculateWeights(final double [] x, int dim)
	{
		double sum = 0, maxi = (-Double.POSITIVE_INFINITY), maxindex = 0;

		for (int i=0; i<nofunc_; ++i) {
			sum = 0.0;
			for (int j=0; j<dim; ++j) {
				sum += ( x[j] - O_[i][j] ) * ( x[j] - O_[i][j] );
			}
			weight_[i] = Math.exp( -sum/(2.0 * dim * sigma_[i] * sigma_[i]) );
			if (i==0) { maxi = weight_[i]; }
			if (weight_[i] > maxi) {
				maxi = weight_[i];
				maxindex = i;
			}
			//maxi = max(maxi, weight_[i]);
		}
		sum = 0.0;
		for (int i=0; i<nofunc_; ++i) {
			//if (weight_[i] != maxi) {
			if (i != maxindex) {
				weight_[i] *= (1.0 - Math.pow(maxi, 10.0));
			}
			sum += weight_[i];
		}
		for (int i=0; i<nofunc_; ++i) {
			if (sum == 0.0) {
				weight_[i] = 1.0/(double)nofunc_;
			} else {
				weight_[i] /= sum;
			}
			//System.out.println("weight_"+i+": "+weight_[i]);
		}

	}

	
	void loadRotationMatrix(final String filename, int dim)
	{	
		File file = new File(filename); 
		try {
			LineNumberReader reader = new LineNumberReader( new FileReader( file ) );
			try {
				String buffer;	
				try {
				    double tmp = -1;
				    for (int i=0; i<nofunc_; ++i) {
				        for (int j=0; j<dim; ++j) {
							buffer = reader.readLine();
							String[] numbers = buffer.split("\t");
				            for (int k=0; k<dim; ++k) {
				            	tmp = Double.parseDouble(numbers[k].trim());
				                M_[i][j][k] = tmp;
				             //   System.out.print(","+M_[i][j][k]);
				            }
				        }

				    }
				} catch (NumberFormatException e) {
					System.err.println("Error: loadRotationMatrix, NumberFormatException: " + e.toString());
					e.printStackTrace();
				} catch (IOException e) {
					System.err.println("Error: loadRotationMatrix, IOException: " + e.toString());
					e.printStackTrace();
				}
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					System.err.println("Error: loadRotationMatrix, Can not close file: IOException: " + e.toString());
					e.printStackTrace();
				}			
			}
		} catch (FileNotFoundException e) {
			System.err.println("Error: loadRotationMatrix, Can not find file: " + filename +e.toString());
			e.printStackTrace();
		} 
	}
	
	void CalculateFMaxi(int dim)
	{
		/* functions */
		double [] x5 = new double[dim];
		for (int i=0; i<dim; ++i) { x5[i] = 5 ; }
		
		//FRastrigin
		int index=0;
		transformToZNoshift(dim,x5, index);
		fmaxi_[index] = doEvaluateFRastrigin(z_);
		//System.out.print("fmaxi_ FRastrigin"+fmaxi_[0]);
		++index;
		
		//FRastrigin
		transformToZNoshift(dim,x5, index);
		fmaxi_[index] = doEvaluateFRastrigin(z_);
	    ++index;
        
      //FWeierstrass
        transformToZNoshift(dim,x5, index);
        fmaxi_[index] = doEvaluateFWeierstrass(z_);
	    ++index;
        
      //FWeierstrass
        transformToZNoshift(dim,x5, index);
        fmaxi_[index] = doEvaluateFWeierstrass(z_);
        ++index;
        
      //FGriewank
        transformToZNoshift(dim,x5, index);
        fmaxi_[index] = doEvaluateFGriewank(z_);
        ++index;
        
      //FGriewank
        transformToZNoshift(dim,x5, index);
        fmaxi_[index] = doEvaluateFGriewank(z_);
        ++index;
        
      //FEF8F2
        transformToZNoshift(dim,x5, index);
        fmaxi_[index] = doEvaluateFEF8F2(z_);
        ++index;
        
      //FEF8F2
        transformToZNoshift(dim,x5, index);
        fmaxi_[index] = doEvaluateFEF8F2(z_);
        ++index;
		
	}

	
	double evaluateInner_(final double []x,int dim)
	{
	    
	    calculateWeights(x,dim);
	    //FRastrigin
	    int index=0;
	    transformToZ(x, index,dim);
	    fi_[index] = doEvaluateFRastrigin(z_);
	   // System.out.println("fi_ : "+fi_[0]);
        ++index;
	    
      //FRastrigin
        transformToZ(x, index,dim);
	    fi_[index] = doEvaluateFRastrigin(z_);
        ++index;
        
      //FWeierstrass
        transformToZ(x, index,dim);
	    fi_[index] = doEvaluateFWeierstrass(z_);
        ++index;
        
      //FWeierstrass
        transformToZ(x, index,dim);
	    fi_[index] = doEvaluateFWeierstrass(z_);
        ++index;
        
      //FGriewank
        transformToZ(x, index,dim);
	    fi_[index] = doEvaluateFGriewank(z_);
        ++index;
        
      //FGriewank
        transformToZ(x, index,dim);
	    fi_[index] = doEvaluateFGriewank(z_);
        ++index;
        
      //FEF8F2
        transformToZ(x, index,dim);
	    fi_[index] = doEvaluateFEF8F2(z_);
        ++index;
        
      //FEF8F2
        transformToZ(x, index,dim);
	    fi_[index] = doEvaluateFEF8F2(z_);
        ++index;
        
        double result = 0;
        
        for (int i=0; i<nofunc_; ++i) {
        	//System.out.println("result: "+result+" : "+weight_[i]+" : "+C_+" : "+ fi_[i]+" : "+ fmaxi_[i]+" : " + bias_[i]);
	        result += weight_[i]*( C_ * fi_[i] / fmaxi_[i] + bias_[i] );
	    }
	    //Assuming maximization
        
	    return -1.0*result + f_bias_;
	}
	
	
	public double getMax() {
		return max;
	}

	public double getMin() {
		return min;
	}

}
