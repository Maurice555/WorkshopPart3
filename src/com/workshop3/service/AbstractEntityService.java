package com.workshop3.service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.*;
import javax.transaction.RollbackException;
import javax.transaction.TransactionalException;

import com.workshop3.dao.DAOIface;
import com.workshop3.model.EntityTemplate;

@Dependent
public abstract class AbstractEntityService<E extends EntityTemplate> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private DAOIface<E> entityDAO;
	
	@Inject
	public AbstractEntityService(DAOIface<E> dao) {
		this.entityDAO = dao;
	}
	
	
	public DAOIface<E> getDAO() {return this.entityDAO;}
	
	public void setDAO(DAOIface<E> dao) {this.entityDAO = dao;}
	
	
	public E get(long id) {
		return this.entityDAO.get(id);
	}
	
	public E get(String[] uniqueValues) {
		return this.entityDAO.get(uniqueValues);
	}
	
	public long add(E e) {
		try {
			this.entityDAO.save(e);
			return e.getId();
		} catch (SQLException sqlexc) {
			return errorCodeCheck(sqlexc, e);		
		} catch (TransactionalException txexc) {
			return rollbackCheck(txexc, e);
		}
	}
	
	public void update(E e, long id) {
		try {
			get(id);
			this.entityDAO.update(e);
		} catch (SQLException sqlexc) {
			errorCodeCheck(sqlexc, e);
		} catch (TransactionalException txexc) {
			rollbackCheck(txexc, e);
		}
	}

	public E delete(long id) {
		E e = get(id);
		this.entityDAO.delete(id);
		return e;
	}
	
	public List<E> fetch() {
		return this.entityDAO.getAll();
	}
	
		
	protected long rollbackCheck(TransactionalException te, EntityTemplate e) {
		if (te.getCause() instanceof RollbackException) {
			Throwable cause = te.getCause();
			while (cause.getCause() != null) {
				cause = cause.getCause();
			}
			SQLException sqlexc = (SQLException) cause;
			return errorCodeCheck(sqlexc, e);
		}
		return 0;
	}
	
	protected long errorCodeCheck(SQLException sqlexc, EntityTemplate e) {
		if (sqlexc.getErrorCode() == duplicateKey) {
			return this.entityDAO.get(e.uniqueValue()).getId();
		}
		return 0;
	}



	protected static final int duplicateKey = 1062;



	
	
	public static long getSerialversionuid() {return serialVersionUID;}

	
}
