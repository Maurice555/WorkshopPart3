package com.workshop3.model;

public interface EntityIface extends java.io.Serializable {
	
	long getId();
	void setId(long id);
	default String[] uniqueValue() { return new String[] {getId() +""}; }

}
