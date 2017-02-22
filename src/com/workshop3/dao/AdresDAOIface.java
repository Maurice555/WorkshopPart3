package com.workshop3.dao;

import com.workshop3.model.Adres;

public interface AdresDAOIface {
	
	void save(Adres adres);
	Adres get(long id);
	
}
