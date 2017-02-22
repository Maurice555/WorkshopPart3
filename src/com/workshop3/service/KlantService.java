package com.workshop3.service;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.context.*;
import javax.inject.*;
import javax.transaction.Transactional;

import com.workshop3.dao.DAO;
import com.workshop3.dao.mysql.*;
import com.workshop3.model.Adres;
import com.workshop3.model.Klant;

@Named
@SessionScoped
public class KlantService implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//@EJB(name = "java:module/localKlantDAO")

	@Inject
	private KlantDAO klantDAO;
	
	@Inject
	private AdresDAO adresDAO;
	
	private BestellingService bestelService;
	
	public KlantService() {}

	public KlantDAO getKlantDAO() {return this.klantDAO;}
	
	public void setKlantDAO(KlantDAO klantDAO) {this.klantDAO = klantDAO;}

	public AdresDAO getAdresDAO() {return this.adresDAO;}

	public void setAdresDAO(AdresDAO adresDAO) {this.adresDAO = adresDAO;}
	
	public BestellingService getBestelService() {return this.bestelService;}

	@Inject
	public void setBestelService(BestellingService bestelService) {this.bestelService = bestelService;}
	

	public static String firstCapital(String s){
		return s.replace(s.substring(0, 1), s.substring(0, 1).toUpperCase());
	}
	
	public static boolean isValidEmail(String mail) {
		return mail.matches("([(\\w)+\\.-]+)[\\w^_]+@[\\w-\\.]+[\\w^_]+(\\.([\\w^0-9_]){2,4}){1,2}");
	}
	
	public boolean isKnownEmail(String mail) {
		return this.klantDAO.get(mail.toLowerCase()).getId() != 0;
	}
	
	public long add(Klant k) {
		if (isValidEmail(k.getEmail())) {
//			if (!isKnownEmail(k.getEmail())) {
				this.klantDAO.save(k);
//			} else {
//				return 0;
//			}
		} else {
			return -1;
		}
		return k.getId();
	}
	
	public Klant get(long id) {
		return this.klantDAO.get(id);
	}
	
	public Klant get(String mail) {
		if (isValidEmail(mail))
			return this.klantDAO.get(mail.toLowerCase());
		
		return null;
	}
	
	public String getTable(){
		StringBuilder everything = new StringBuilder();
		for (Klant k : this.klantDAO.getAll()) {
			everything.append(k.toString());
		}
		
		return everything.toString();
	}
	
	public Klant del(long id) {
		Klant k = get(id);
		this.klantDAO.delete(id);
		return k;
	}

	public long add(Adres adres) {
		this.adresDAO.save(adres);
		return adres.getId();
	}
	
	public Adres getAdres(long adresId) {
		return this.adresDAO.get(adresId);
	}
	
	
	
	

	public static long getSerialversionuid() {return serialVersionUID;}
	
	
	
}
