package com.workshop3.model;

import javax.enterprise.context.*;
import javax.inject.Named;

@Named
@SessionScoped
public abstract class EntityTemplate implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long id;

	public long getId() {return this.id;}

	public void setId(long id) {this.id = id;}
	
}
