package com.workshop3.dao;

import java.sql.SQLException;

import javax.persistence.EntityManager;

import com.workshop3.model.EntityTemplate;

public interface DAOIface<E extends java.io.Serializable> {
	
	void save(E e) throws SQLException;
	E get(long id);
	E get(String[] uniqueValues);
	void update(E e) throws SQLException;
	void delete(long id);
	java.util.List<E> getAll();
	
}
