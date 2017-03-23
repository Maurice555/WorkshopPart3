package com.workshop3.dao.mysql;

import java.util.*;

import javax.enterprise.context.ConversationScoped;
import javax.persistence.*;

import com.workshop3.model.Klant;
import static com.workshop3.service.KlantService.*;

@ConversationScoped
public class KlantDAO extends DAO<Klant> {

	private static final long serialVersionUID = 112L;
	
	@PersistenceContext(unitName = "CustomER")
	private EntityManager em;
	
	public KlantDAO() { super(Klant.class); }
	
	@Override
	public Klant getUnique(String[] uniqueValues) {
		return get(uniqueValues[0]);
	}
	
	public Klant get(String mail) {
		return (Klant) this.em.createNativeQuery(
				"select * from Klant where email = '" + mail.toLowerCase() + "'", Klant.class)
				.getSingleResult();
	}
	
	public List<Klant> findByAchternaam(String achter) {
		return this.em.createNativeQuery(
				"select * from Klant where achternaam = '" + firstCapital(achter) + "'", Klant.class)
				.getResultList();
	}
	
	public List<Klant> findByVoorEnAchternaam(String voor, String achter) {
		return this.em.createNativeQuery(
				"select * from Klant where voornaam = '" + firstCapital(voor) + 
				"' and achternaam = '" + firstCapital(achter) + "'", Klant.class)
				.getResultList();
	}
		
	public static long getSerialversionuid() {return serialVersionUID;}
	
		
}

/*	JPA query language:..
this.em.createQuery("SELECT c FROM Customer c WHERE c.name LIKE :custName", Klant.class))
	.setParameter("custName", "Bugs")
	.setMaxResults(10)
	.getResultList();
*/
	
