package com.workshop3.dao;

public interface DAOIface<E extends java.io.Serializable> {
	
	void save(E e);
	E get(long id);
	void update(E e);
	void delete(long id);
	java.util.List<E> getAll();
	
}
