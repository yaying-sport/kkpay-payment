package com.kaiserkalep.bean;

public class HashMapEntity {
	private String key;
	private String value;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public HashMapEntity(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}
	public HashMapEntity() {
		super();
	}
	
}
