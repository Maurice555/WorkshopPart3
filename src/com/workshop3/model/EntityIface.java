package com.workshop3.model;

import java.util.*;

public interface EntityIface extends java.io.Serializable {
	
	long getId();
	void setId(long id);
	//default String[] uniqueValue() { return new String[] {getId() +""}; } // oud
	default Map<String, String> identifyingProps() {
		Map<String, String> props = new HashMap<String, String>();
		props.put("id", getId() +"");
		return props;
	}

}
