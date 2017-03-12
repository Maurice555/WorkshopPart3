package com.workshop3.service;

import java.sql.SQLException;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.TransactionalException;

import com.workshop3.dao.DAOIface;
import com.workshop3.model.EntityTemplate;

@Dependent
public abstract class DualEntityService<E extends EntityTemplate, S extends EntityTemplate> 
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
	
	public S getSimple(String[] uniqueValues) {
		return this.simpleDAO.get(uniqueValues);
	}
	
	public long addSimple(S s) {
		try {
			this.simpleDAO.save(s);
			return s.getId();
		} catch (SQLException sqlexc) {
			return errorCodeCheck(sqlexc, s);		
		} catch (TransactionalException txexc) {
			return rollbackCheck(txexc, s);
		}	
	}
		
	public void updateSimple(S s, long id) {
		getSimple(id);
		try {
			this.simpleDAO.update(s);
		} catch (SQLException sqlexc) {
			errorCodeCheck(sqlexc, s);		
		} catch (TransactionalException txexc) {
			rollbackCheck(txexc, s);
		}	
	}
	
	public void deleteSimple(long id) {
		getSimple(id);
		this.simpleDAO.delete(id);
	}
	
	public List<S> fetchSimple() {
		return this.simpleDAO.getAll();
	}
	
	
	@Override
	protected long errorCodeCheck(SQLException sqlexc, EntityTemplate e) {
		if (sqlexc.getErrorCode() == duplicateKey) {
			return this.simpleDAO.get(e.uniqueValue()).getId();
		}
		return 0;
	}
	
}
