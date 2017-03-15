package com.workshop3.dao.mysql;

import javax.enterprise.context.ConversationScoped;
import javax.persistence.*;

import com.workshop3.model.Artikel;

@ConversationScoped
public class ArtikelDAO extends DAO<Artikel> {

	private static final long serialVersionUID = 1L;
	
	@PersistenceContext(unitName = "CustomER")
	private EntityManager em;
	
	public ArtikelDAO() { super(Artikel.class); }

}
