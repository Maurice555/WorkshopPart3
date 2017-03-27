package com.workshop3.dao.mysql;

import java.io.Serializable;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

import javax.inject.Inject;
import javax.persistence.*;
import javax.transaction.Transactional;

import com.workshop3.dao.DAOIface;


@Transactional(rollbackOn = SQLIntegrityConstraintViolationException.class)
public abstract class DAO<E extends Serializable> implements DAOIface<E> {

	private static final long serialVersionUID = 1001L;
	
	@PersistenceContext(unitName = "CustomER")
	private EntityManager em;
	
	private Class<E> entity;
	
	@Inject
	public DAO(Class<E> entity) {
		this.entity = entity;
	}
	
	
	public EntityManager getEm() {return this.em;}
	
	public void setEm(EntityManager em) {this.em = em;}
	
	public Class<E> getEntity() {return this.entity;}

	public void setEntity(Class<E> entity) {this.entity = entity;}

	
	@Override
	public void save(E e) throws RollbackException, SQLIntegrityConstraintViolationException {
		this.em.persist(e);
	}
	
	@Override
	public E get(long id) {
		return this.em.find(this.entity, id);	
	}
//	//default behaviour for returning single result.. in this case with long id.. meant for overriding
//	public E getUnique(String[] uniqueValues) {
//		return get(Long.parseLong(uniqueValues[0]));
//	}
	
	@Override
	public E getUnique(Map<String, String> identifyingProps) {
		return get(identifyingProps).get(0);
	}
	
	@Override
	public List<E> getAll(Map<String, String> paramValues) {
		List<E> rows = new ArrayList<E>();
		for (Map.Entry<String, String> entry : paramValues.entrySet()) {
			rows.addAll(this.em.createNativeQuery(
					"select * from " + this.entity.getSimpleName() + " where " + entry.getKey() + 
					" = '" + entry.getValue() + "'", this.entity)
					.getResultList());
		}
		return rows;
	}

	@Override
	public List<E> get(Map<String, String> paramValues) {
		StringBuilder query = new StringBuilder("select * from " + this.entity.getSimpleName() + " where ");
		for (Map.Entry<String, String> entry : paramValues.entrySet()) {
			query.append(entry.getKey() + " = '" + entry.getValue() + "' and ");
		}
		return this.em.createNativeQuery(query.toString().substring(0, query.length() -4), this.entity).getResultList();
	}
	
	@Override
	public List<E> getAll() {
		return this.em.createNativeQuery(
				"select * from " + this.entity.getSimpleName(), this.entity)
				.getResultList();
	}
	
	@Override
	public void delete(long id) {
		this.em.remove(get(id));
	}
	
	@Override
	public void update(E e) throws RollbackException, SQLIntegrityConstraintViolationException {
		this.em.merge(e);
	}
	
	
	
	
	public static long getSerialversionuid() {return serialVersionUID;}

	
	
	
	
}
