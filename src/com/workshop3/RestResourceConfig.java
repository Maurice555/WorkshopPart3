package com.workshop3;

import javax.annotation.Resource;
import javax.enterprise.context.Dependent;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.ext.MessageBodyWriter;

import org.glassfish.jersey.server.ResourceConfig;

import com.workshop3.model.EntityIface;


@ApplicationPath("WorkshopPart3")
@Dependent
public class RestResourceConfig extends ResourceConfig implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@Resource
	private MessageBodyWriter<EntityIface> writer;
		
	public RestResourceConfig() {
		packages("com.workshop3.model;com.workshop3.service");
	}
	
	
}
