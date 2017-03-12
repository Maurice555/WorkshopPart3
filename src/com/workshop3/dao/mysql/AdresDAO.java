package com.workshop3.dao.mysql;

import java.util.*;

import javax.enterprise.context.ConversationScoped;
import javax.persistence.*;

import com.workshop3.model.Adres;

@ConversationScoped
public class AdresDAO extends DAO<Adres> implements com.workshop3.dao.DAOIface<Adres> {
	
	private static final long serialVersionUID = 101L;
	
	@PersistenceContext(unitName = "CustomER")
	private EntityManager em;
	
	public AdresDAO() { super(Adres.class); }
	
	
	@Override
	public Adres get(String[] uniqueValues) {
		return findByPostcodeAndHuisnummer(uniqueValues[0], Integer.parseInt(uniqueValues[1]), uniqueValues[2]);
	}
		
	public List<Adres> findByStraatAndHuisnummer(String straat, int huisnummer, String plaats) {
		List<Adres> adresList = new ArrayList<Adres>();
		for (Adres a : findByStraat(straat, plaats)) {
			if(huisnummer == 0 || a.getHuisnummer() == huisnummer) {
				adresList.add(a);
			}
		}
		return adresList;
	}
	
	public List<Adres> findByStraat(String straat, String plaats) {
		return this.em.createNativeQuery
				("select * from Adres where straatnaam = '"+ straat +"' and woonplaats = '"+ plaats +"'", Adres.class)
				.getResultList();
	}

	public Adres findByPostcodeAndHuisnummer(String postcode, int huisnummer, String toevoeging) {
		Adres adres = null;
		for (Adres a : findByPostcodeAndHuisnummer(postcode, huisnummer)) {
			if (a.getToevoeging().equalsIgnoreCase(toevoeging)) {
				adres = a;
			}
		}
		return adres;
	}
	
	public List<Adres> findByPostcodeAndHuisnummer(String postcode, int huisnummer) {
		return this.em.createNativeQuery
				("select * from Adres where postcode = '" + postcode + "' and huisnummer = " + huisnummer, Adres.class)
				.getResultList();
	}
	
	
	
	public static long getSerialversionuid() {return serialVersionUID;}


		
	
	
}
