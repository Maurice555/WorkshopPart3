package com.workshop3.service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.TransactionalException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.workshop3.dao.DAOIface;
import com.workshop3.model.EntityIface;

@Dependent
public abstract class AbstractEntityService<E extends EntityIface> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected static final String DATE = "{datum : [\\d]{4}-[\\d]{2}-[\\d]{2}}";
	
	protected static final String PERIOD = "{period : [\\d]+(,[ \\d]){0,3}}";
		
	protected static final String ID = "Id={id}";
	
	
	@Inject
	private DAOIface<E> entityDAO;
	
	public AbstractEntityService(DAOIface<E> dao) {
		this.entityDAO = dao;
	}
	
	
	public DAOIface<E> getDAO() {return this.entityDAO;}
	
	public void setDAO(DAOIface<E> dao) {this.entityDAO = dao;}
	
	
	@GET
	@Path("main/" + ID)
	@Produces(MediaType.APPLICATION_JSON)
	public E get(@PathParam("id") long id) {
		return this.entityDAO.get(id);
	}
	
	public E getUnique(String[] uniqueValues) {
		return this.entityDAO.getUnique(uniqueValues);
	}
	
	public Set<E> get(Map<String, String> keyValues) {
		return new HashSet<E>(this.entityDAO.get(keyValues));
	}
	
	public long add(E e) {
		try {
			this.entityDAO.save(e);
			return e.getId();
		} catch (SQLException sqlexc) {
			if (isDuplicateKeyError(sqlexc)) {
				return getUnique(e.uniqueValue()).getId();
			}
		} catch (TransactionalException txexc) {
			if (isDuplicateKeyError(isSQLCauseForRollback(txexc))) {
				return getUnique(e.uniqueValue()).getId();
			}
			return txExc;
		}
		return saveExc;
	}
	
	public long update(E e) {
		try {
			this.entityDAO.update(e);
			return e.getId();
		} catch (SQLException sqlexc) {
			if (isDuplicateKeyError(sqlexc))
				return getUnique(e.uniqueValue()).getId();
		} catch (TransactionalException txexc) {
			if (isDuplicateKeyError(isSQLCauseForRollback(txexc))) {
				return getUnique(e.uniqueValue()).getId();
			}
			return txExc;
		}
		return saveExc;
	}

	public E delete(long id) {
		E e = get(id);
		this.entityDAO.delete(id);
		return e;
	}
	
	@GET
	@Path("main/fetch")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<E> fetch() {
		return new HashSet<E>(this.entityDAO.getAll());
	}
	
	
// Een hack voor die gewrapte exceptions	
	protected static SQLException isSQLCauseForRollback(TransactionalException te) {
		Throwable cause = te.getCause();
		while (cause.getCause() != null) {
			cause = cause.getCause();
		}
		if (cause instanceof SQLException) {
			return (SQLException) cause; 
		}
		return null;
	}
	
	protected static boolean isDuplicateKeyError(SQLException sqlexc) {
		return sqlexc != null && sqlexc.getErrorCode() == duplicateKey;
	}
	
	protected static final int txExc = -2;
	
	protected static final int saveExc = 0;
	
	
	protected static final int duplicateKey = 1062;



	
	
	public static long getSerialversionuid() {return serialVersionUID;}


		
}
