package com.workshop3.dao.mysql;

import java.util.*;
import java.time.LocalDate;
import java.time.Period;

import javax.enterprise.context.ConversationScoped;
import javax.persistence.*;

import com.workshop3.model.Bestelling;

@ConversationScoped
public class BestellingDAO extends DAO<Bestelling> {

	private static final long serialVersionUID = 1L;
	
	@PersistenceContext(unitName = "CustomER")
	private EntityManager em;
	
	public BestellingDAO() { super(Bestelling.class); }
	
	
	public List<Bestelling> findByDateAndPeriod(LocalDate datum, Period period) {
		return this.em.createNativeQuery(
				"select * from Bestelling where datum > '" + datum + 
				"' and datum <= '" + datum.plus(period.plusDays(1)) + "'", Bestelling.class)
				.getResultList();
	}

	public List<Bestelling> getByKlant(long klantID) {
		return this.getEm().createNativeQuery(
				"select * from Bestelling where klantId = " + klantID, Bestelling.class)
				.getResultList();
	}
	
}
