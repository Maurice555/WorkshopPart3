package com.workshop3.dao.mysql;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import com.workshop3.model.Bestelling;
import com.workshop3.model.Bestelling.BestellingStatus;

@Named
@Transactional
@ConversationScoped
public class BestellingDAO extends com.workshop3.dao.DAO<Bestelling> {

	private static final long serialVersionUID = 1L;
	
	@PersistenceContext(unitName = "CustomER")
	private EntityManager em;
	
	public BestellingDAO() { super(Bestelling.class); }
	
	public void statusUpdate(long id, BestellingStatus status) {
		get(id).setStatus(status);
		this.em.merge(get(id));
	}

}
