package com.workshop3.dao;

import com.workshop3.model.*;

import java.util.List;
import java.util.Set;


public interface KlantDAOIface {
	
	void save(Klant k);
	Klant get(long id);
	Klant get(String mail);
	List<Klant> getAll();
	void delete(Klant k);

}
