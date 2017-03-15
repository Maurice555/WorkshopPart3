package com.workshop3.service;

import java.sql.SQLException;
import java.util.*;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.transaction.TransactionalException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.workshop3.dao.mysql.*;
import com.workshop3.model.*;

@SessionScoped
public class KlantService extends DualEntityService<Klant, Account> {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private KlantDAO klantDAO;
	
	@Inject
	private AdresDAO adresDAO;
	
	private BestellingService bestelService;
	
	public KlantService() { super(new KlantDAO(), new AccountDAO()); }

	
	public BestellingService getBestelService() {return this.bestelService;}
	
	@Inject
	public void setBestelService(BestellingService bestelService) {this.bestelService = bestelService;}


	public static String firstCapital(String s){
		return s.trim().replace(s.substring(0, 1), s.substring(0, 1).toUpperCase());
	}
	
	public static boolean isValidEmail(String mail) {
		return mail.matches("([(\\w)+\\.-]+)[\\w^_]+@[\\w-\\.]+[\\w^_]+(\\.([\\w^0-9_]){2,4}){1,2}");
	}
	
	public static String trimUpCase(String postcode) {
		return postcode.trim().replace(" ", "").toUpperCase();
	}

	public boolean isKnownEmail(String mail) {
		try {
			if (get(mail.toLowerCase()).getId() > 0) { return true; }
		} catch (NullPointerException ne) {
			// geen klant gevonden
		}
		return false;
	}
	
// Custom Klant-zoekmethoden
	public Klant get(String mail) {
		if (isValidEmail(mail)) { return this.klantDAO.get(mail.toLowerCase());	}
		return get(-3);
	}
	
	public Set<Klant> findByVoorEnAchternaam(String voor, String achter) {
		return new HashSet<Klant>(this.klantDAO.findByVoorEnAchternaam(voor, achter));
	}
	
	public Set<Klant> findByAchternaam(String achter) {
		return new HashSet<Klant>(this.klantDAO.findByAchternaam(achter));
	}
	
	public Set<Klant> findKlantByAdres(Adres a) {
		Set<Klant> klanten = new HashSet<Klant>(findByPostcodeAndHuisnummer(
				a.getPostcode(), a.getHuisnummer(), a.getToevoeging())
				.getBewoners()); 
		klanten.addAll(a.getBezorgers());
		return klanten;
	}
	
// Adres methoden
	public long add(Adres adres) {
		try {
			this.adresDAO.save(adres);
			return adres.getId();
		} catch (SQLException sqlexc) {
			if (isDuplicateKeyError(sqlexc)) {
				return getUniqueAdres(adres.uniqueValue()).getId();
			}
		} catch (TransactionalException te) { // Is wat we catchen
			if (isDuplicateKeyError(isSQLCauseForRollback(te))) {
				return getUniqueAdres(adres.uniqueValue()).getId();
			}
			return txExc;
		}
		return saveExc;
	}
	
	public Adres getAdres(long adresID) {
		return this.adresDAO.get(adresID);
	}
	
	public Adres getUniqueAdres(String[] uniqueValues) {
		return findByPostcodeAndHuisnummer(uniqueValues[0], Integer.parseInt(uniqueValues[1]), uniqueValues[2]);
	}
	
	public Set<Adres> findByPostcodeAndHuisnummer(String postcode, int huisnummer) {
		return new HashSet<Adres>(this.adresDAO.findByPostcodeAndHuisnummer(postcode, huisnummer));
	}
	
	public Adres findByPostcodeAndHuisnummer(String postcode, int huisnummer, String toevoeging) {
		for (Adres a : findByPostcodeAndHuisnummer(postcode, huisnummer)) {
			if (hasEqualToevoeging(a, toevoeging)) { return a; }
		}
		return null;
	}

	public Set<Adres> findByStraatAndPlaats(String straat, String plaats) {
		return new HashSet<Adres>(this.adresDAO.findByStraat(straat, plaats));
	}
	
	public Set<Adres> findByStraatAndHuisnummer(String straat, int huisnummer, String plaats) {
		Set<Adres> adresSet = new HashSet<Adres>();
		for (Adres a : findByStraatAndPlaats(straat, plaats)) {
			if (huisnummer == 0 || a.getHuisnummer() == huisnummer) {
				adresSet.add(a);
			}
		}
		return adresSet;
	}
	
	public Adres findByStraatAndHuisnummer(String straat, int huisnummer, String toevoeging, String plaats) {
		for (Adres a : findByStraatAndHuisnummer(straat, huisnummer, plaats)) {
			if (hasEqualToevoeging(a, toevoeging)) { return a; }
		}
		return null;
	}

	public static boolean hasEqualToevoeging(Adres a, String toevoeging) {
		return a.getToevoeging().trim().equalsIgnoreCase(toevoeging.trim());
	}

// Accountbeheer <Simple>
	public Set<Account> getAccounts(long id) {
		return get(id).getAccounts();
	}
	
	
	
	
	
	
	public static long getSerialversionuid() {return serialVersionUID;}
	
	
	
}
