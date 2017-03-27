package com.workshop3.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;

public interface DAOIface<E extends Serializable> extends Serializable {
	
	void save(E e) throws SQLException;
	E get(long id);
	E getUnique(Map<String, String> identifyingProps);
	List<E> get(Map<String, String> keyValues);
	void update(E e) throws SQLException;
	void delete(long id);
	List<E> getAll();
	List<E> getAll(Map<String, String> keyValues);
	
}
