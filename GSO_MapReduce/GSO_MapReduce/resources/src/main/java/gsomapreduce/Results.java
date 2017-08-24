package gsomapreduce;

public class Results {
	double [][] resultsD;
	
	
	public Results(int s,int i){ 
		resultsD=new double[s][i];  
    }
	public void setval(int w,int it,double value){ 
		resultsD[w][it]=value;    
    }
	
	public double getval(int w,int it){ 
		return resultsD[w][it];    
    }
}
