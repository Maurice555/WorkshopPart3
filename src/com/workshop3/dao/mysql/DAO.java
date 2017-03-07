package com.workshop3.dao.mysql;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import javax.enterprise.context.*;
import javax.inject.*;
import javax.jms.TransactionRolledBackException;
import javax.persistence.*;
import javax.servlet.ServletException;
import javax.transaction.Transactional;

import org.eclipse.persistence.exceptions.DatabaseException;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.workshop3.model.EntityTemplate;

@Named
@Dependent
public abstract class DAO<E extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1001L;
	
	@PersistenceContext(unitName = "CustomER")
	private EntityManager em;
	
	private Class<E> entity;
	
	@Inject
	public DAO(Class<E> entity) {this.entity = entity;}
	
	
	public EntityManager getEm() {return this.em;}
	
	public void setEm(EntityManager em) {this.em = em;}
	
	public Class<E> getEntity() {return this.entity;}

	public void setEntity(Class<E> entity) {this.entity = entity;}

	
	public void save(E e) throws RollbackException {
		this.em.persist(e);
		this.em.flush();
	}
	
	public E get(long id) {
		return this.em.find(this.entity, id);	
	}
	
	public List<E> getAll() {
		return this.em.createNativeQuery(
				"select * from " + this.entity.getSimpleName(), this.entity)
				.getResultList();
	}
	
	public void delete(long id) {
		this.em.remove(get(id));
	}
	
	public void update(E e) throws RollbackException {
		this.em.merge(e);
		this.em.flush();
	}
	
	
	
	
	public static long getSerialversionuid() {return serialVersionUID;}

	
	
	
	
}
