package com.workshop3.dao.mysql;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;
import javax.persistence.*;
import javax.transaction.Transactional;

import com.workshop3.dao.DAOIface;
import com.workshop3.model.Artikel;

@ConversationScoped
public class ArtikelDAO extends DAO<Artikel> implements com.workshop3.dao.DAOIface<Artikel> {

	private static final long serialVersionUID = 1L;
	
	@PersistenceContext(unitName = "CustomER")
	private EntityManager em;
	
	public ArtikelDAO() { super(Artikel.class); }

}
