package com.workshop3.service;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.*;
import javax.transaction.RollbackException;
import javax.transaction.TransactionalException;

import com.workshop3.dao.DAOIface;
import com.workshop3.model.EntityTemplate;

@Named
@Dependent
public abstract class AbstractEntityService<E extends EntityTemplate> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private DAOIface<E> entityDAO;
	
	@Inject
	public AbstractEntityService(DAOIface<E> dao) {
		this.entityDAO = dao;
	}
	
	
	public E get(long id) {
		return this.entityDAO.get(id);
	}
	
	public long add(E e) {
		try {
			this.entityDAO.save(e);
			return e.getId();
		} catch (TransactionalException te) {
			return rollbackCheck(te);
		}
	}
	
	public void update(E e, long id) {
		try {
			get(id);
			this.entityDAO.update(e);
		} catch (TransactionalException te) {
			rollbackCheck(te);
		}
	}

	public List<E> fetch() {
		return this.entityDAO.getAll();
	}
	
	public E del(long id) {
		E e = get(id);
		this.entityDAO.delete(id);
		return e;
	}
	
	
	
	protected static long rollbackCheck(TransactionalException te) {
		if (te.getCause() instanceof RollbackException) {
			return -2;
		}
		return 0;
	}

	
	
	public static long getSerialversionuid() {return serialVersionUID;}

	
}
