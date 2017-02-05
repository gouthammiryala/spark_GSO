package com.ndsu.spark.GSO_Spark.Beans;

import java.io.Serializable;

public class GSOConfigList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String GSOConfigName;
	private GSOConfig gsoConfig;
	public String getGSOConfigName() {
		return GSOConfigName;
	}
	public void setGSOConfigName(String gSOConfigName) {
		GSOConfigName = gSOConfigName;
	}
	public GSOConfig getGsoConfig() {
		return gsoConfig;
	}
	public void setGsoConfig(GSOConfig gsoConfig) {
		this.gsoConfig = gsoConfig;
	}
	
	

}
