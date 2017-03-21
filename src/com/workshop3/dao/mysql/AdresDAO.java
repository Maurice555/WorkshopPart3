package com.workshop3.dao.mysql;

import java.util.*;

import javax.enterprise.context.ConversationScoped;
import javax.persistence.*;

import com.workshop3.model.Adres;
import static com.workshop3.service.KlantService.*;

@ConversationScoped
public class AdresDAO extends DAO<Adres> {
	
	private static final long serialVersionUID = 101L;
	
	@PersistenceContext(unitName = "CustomER")
	private EntityManager em;
	
	public AdresDAO() { super(Adres.class); }
	
		
	public List<Adres> findByStraat(String straat, String plaats) {
		return this.em.createNativeQuery(
				"select * from Adres where straatnaam = '"+ firstCapital(straat) + 
				"' and woonplaats = '"+ firstCapital(plaats) +"'", Adres.class)
				.getResultList();
	}

	public List<Adres> findByPostcode(String postcode) {
		return this.em.createNativeQuery(
				"select * from Adres where postcode = '" + trimUpCase(postcode) + "'", Adres.class)
				.getResultList();
	}
	
	public List<Adres> findByPostcodeAndHuisnummer(String postcode, int huisnummer) {
		return this.em.createNativeQuery(
				"select * from Adres where postcode = '" + trimUpCase(postcode) + 
				"' and huisnummer = " + huisnummer, Adres.class)
				.getResultList();
	}
	
	
	
	public static long getSerialversionuid() {return serialVersionUID;}



		
	
	
}
