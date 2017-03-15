package com.workshop3.service;

import java.sql.SQLException;
import java.util.*;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.TransactionalException;

import com.workshop3.dao.DAOIface;
import com.workshop3.model.EntityIface;

@Dependent
public abstract class DualEntityService<E extends EntityIface, S extends EntityIface> 
		extends AbstractEntityService<E> {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private DAOIface<S> simpleDAO;
	
	@Inject
	public DualEntityService(DAOIface<E> dao1, DAOIface<S> dao2) {
		super(dao1);
		setSimpleDAO(dao2);
	}

	
	public DAOIface<S> getSimpleDAO() {return this.simpleDAO;}
	
	public void setSimpleDAO(DAOIface<S> dao2) {this.simpleDAO = dao2;}
	
	
	public S getSimple(long id) {
		return this.simpleDAO.get(id);
	}
	
	public S getUniqueSimple(String[] uniqueValues) {
		return this.simpleDAO.getUnique(uniqueValues);
	}
	
	public long addSimple(S s) {
		try {
			this.simpleDAO.save(s);
			return s.getId();
		} catch (SQLException sqlexc) {
			if (isDuplicateKeyError(sqlexc)) {
				return getUniqueSimple(s.uniqueValue()).getId();
			}
		} catch (TransactionalException txexc) {
			if (isDuplicateKeyError(isSQLCauseForRollback(txexc))) {
				return getUniqueSimple(s.uniqueValue()).getId();
			}
			return txExc;
		}
		return saveExc;
	}
		
	public long updateSimple(S s) {
		try {
			this.simpleDAO.update(s);
			return s.getId();
		} catch (SQLException sqlexc) {
			if (isDuplicateKeyError(sqlexc)) {
				return getUniqueSimple(s.uniqueValue()).getId();
			}			
		} catch (TransactionalException txexc) {
			if (isDuplicateKeyError(isSQLCauseForRollback(txexc))) {
				return getUniqueSimple(s.uniqueValue()).getId();
			}
			return txExc;
		}
		return saveExc;
	}
	
	public S deleteSimple(long id) {
		S s = getSimple(id);
		this.simpleDAO.delete(id);
		return s;
	}
	
	public Set<S> fetchSimple() {
		return new HashSet<S>(this.simpleDAO.getAll());
	}
	
	
}
