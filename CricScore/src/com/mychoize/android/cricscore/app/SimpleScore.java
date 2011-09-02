package com.mychoize.android.cricscore.app;

import java.util.Date;

/**
 * @author sakthipriyan
 *
 */
public class SimpleScore {
	private String simple;
	private String detail;
	private int id;
	private long timestamp;
	private int version;
	public String getSimple() {
		return simple;
	}
	public String getDetail() {
		return detail;
	}
	public int getId() {
		return id;
	}

	
	public long getTimestamp() {
		return timestamp;
	}
	public SimpleScore(String simple, String detail, int id, int version) {
		super();
		this.simple = simple;
		this.detail = detail;
		this.id = id;
		this.version = version;
		this.timestamp = new Date().getTime();
	}
	
	public SimpleScore(String simple, String detail, int id, long timestamp, int version) {
		super();
		this.simple = simple;
		this.detail = detail;
		this.id = id;
		this.timestamp = timestamp;
		this.version = version;
	}
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	
	
	
	
	

}
