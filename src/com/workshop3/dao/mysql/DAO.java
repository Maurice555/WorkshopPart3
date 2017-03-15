package com.workshop3.dao.mysql;

import java.io.Serializable;
import java.util.*;

import javax.inject.Inject;
import javax.persistence.*;
import javax.transaction.Transactional;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

@Transactional(rollbackOn = MySQLIntegrityConstraintViolationException.class)
public abstract class DAO<E extends Serializable> implements Serializable, com.workshop3.dao.DAOIface<E> {

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
	public void save(E e) throws RollbackException, MySQLIntegrityConstraintViolationException {
		this.em.persist(e);
	}
	
	@Override
	public E get(long id) {
		return this.em.find(this.entity, id);	
	}
	//default behaviour for returning single result.. in this case with long id
	@Override
	public E getUnique(String[] uniqueValues) {
		return get(Long.parseLong(uniqueValues[0]));
	}
	
	@Override
	public List<E> get(Map<String, String> keyValues) {
		List<E> rows = new ArrayList<E>();
		for (Map.Entry<String, String> entry : keyValues.entrySet()) {
			rows.addAll(this.em.createNativeQuery(
					"select * from " + this.entity.getSimpleName() + " where " + entry.getKey() + 
					" = '" + entry.getValue() + "'", this.entity)
					.getResultList());
		}
		return rows;
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
	public void update(E e) throws RollbackException, MySQLIntegrityConstraintViolationException {
		this.em.merge(e);
	}
	
	
	
	
	public static long getSerialversionuid() {return serialVersionUID;}

	
	
	
	
}
