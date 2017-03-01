package com.workshop3.dao.mysql;

import java.util.Date;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import com.workshop3.model.Bestelling;

@Named
@Transactional
@ConversationScoped
public class BestellingDAO extends com.workshop3.dao.DAO<Bestelling> {

	private static final long serialVersionUID = 1L;
	
	@PersistenceContext(unitName = "CustomER")
	private EntityManager em;
	
	public BestellingDAO() { super(Bestelling.class); }
	
	public void statusUpdate(long id, int status) {
		get(id).getStati().put(status, new Date());
		this.em.merge(get(id));
	}

}
