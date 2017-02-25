package com.workshop3.dao.mysql;

import java.util.*;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.context.*;
import javax.enterprise.inject.Produces;
import javax.inject.*;
import javax.persistence.*;
import javax.transaction.*;

import com.workshop3.model.Klant;
import com.workshop3.service.KlantService;

@Named
@Transactional
@ConversationScoped
public class KlantDAO extends com.workshop3.dao.DAO<Klant> {

	private static final long serialVersionUID = 112L;
	
	@PersistenceContext(unitName="CustomER")
	private EntityManager em;
	
	public KlantDAO() { super(Klant.class); }

	
	public Klant get(String mail) {
		return (Klant) this.em.createNativeQuery(
				"select * from Klant where email = '" + mail + "'", Klant.class)
				.getSingleResult();
	}
	
	public List<Klant> getByAchternaam(String achter) {
		return this.em.createNativeQuery(
				"select * from Klant where achternaam = '" + achter + "'", Klant.class)
				.getResultList();
	}
	
	public List<Klant> getByVoorEnAchternaam(String voor, String achter) {
		return this.em.createNativeQuery(
				"select * from Klant where voornaam = '" + KlantService.firstCapital(voor) + 
				"' and achternaam = '" + KlantService.firstCapital(achter) + "'", Klant.class)
				.getResultList();
	}
		
	public static long getSerialversionuid() {return serialVersionUID;}
	
		
}

/*	
this.em.createQuery("SELECT c FROM Customer c WHERE c.name LIKE :custName", Klant.class))
	.setParameter("custName", "Bugs")
	.setMaxResults(10)
	.getResultList();
*/
	
