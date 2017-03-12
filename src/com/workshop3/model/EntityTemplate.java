package com.workshop3.model;

import javax.enterprise.context.*;

@SessionScoped
public abstract class EntityTemplate implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long id;

	public long getId() {return this.id;}

	public void setId(long entityID) {this.id = entityID;}
	
	public String[] uniqueValue() {
		return new String[] {getId() +""};
	}
	
}
