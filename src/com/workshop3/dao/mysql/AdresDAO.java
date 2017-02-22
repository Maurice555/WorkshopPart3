package com.workshop3.dao.mysql;

import javax.ejb.*;
import javax.enterprise.context.*;
import javax.enterprise.inject.Produces;
import javax.inject.*;
import javax.persistence.*;
import javax.transaction.TransactionScoped;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.workshop3.model.Adres;

@Named
@Transactional
@ConversationScoped
public class AdresDAO extends com.workshop3.dao.DAO<Adres> {
	
	private static final long serialVersionUID = 101L;
	
	@PersistenceContext(unitName="CustomER")
	private EntityManager em;
	
	public AdresDAO() { super(Adres.class); }

	
	public Adres findAdresByKlant(long klantId) {
		long adresId = (long) this.em.createNativeQuery(
				"select adresId from klantHasAdres where klantId = " + klantId)
				.getResultList()
				.get(0);//Voor de eenvoud even
		return get(adresId);
	}
	
	
	public static long getSerialversionuid() {return serialVersionUID;}
	
	
	
}
