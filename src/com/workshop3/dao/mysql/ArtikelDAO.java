package com.workshop3.dao.mysql;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;
import javax.persistence.*;
import javax.transaction.Transactional;

import com.workshop3.model.Artikel;

@Named
@Transactional
@ConversationScoped
public class ArtikelDAO extends com.workshop3.dao.DAO<Artikel> {

	private static final long serialVersionUID = 1L;
	
	@PersistenceContext(unitName = "CustomER")
	private EntityManager em;
	
	public ArtikelDAO() { super(Artikel.class); }

}
