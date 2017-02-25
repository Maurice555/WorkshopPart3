package com.workshop3.service;

import java.util.List;

import javax.enterprise.context.*;
import javax.inject.*;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.workshop3.dao.mysql.*;
import com.workshop3.model.Adres;
import com.workshop3.model.Klant;

@Named
@SessionScoped
public class KlantService implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
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
/* 
WErkt NieT;	
	public boolean isKnownEmail(String mail) {
		return this.klantDAO.get(mail.toLowerCase()) != null;
	}
*/	
	
	public void newBestellingUnknownKlant(long bestelID) {
		//new KlantView();
	}
		
	public long addOrUpdate(Klant k) {
		String mail = k.getEmail().toLowerCase(); 
		k.setEmail(mail);
		
		if (isValidEmail(mail)) {
			if (k.getId() > 0) {
				update(k, k.getId());
				return k.getId();
			}
			
			return add(k);
		} 
		return -1;
	}
	
	public void update(Klant k, long id) {
		get(id);
		this.klantDAO.update(k);
	}
	
	
	public long add(Klant k) {
		try {
			this.klantDAO.save(k);
			return k.getId();
		} catch (MySQLIntegrityConstraintViolationException ex) {
			return -2;
		}
	}
	
	public Klant get(long id) {
		return this.klantDAO.get(id);
	}
	
	public Klant get(String mail) {
		if (isValidEmail(mail))
			return this.klantDAO.get(mail.toLowerCase());
		
		return null;
	}
	
	public List<Klant> getTable(){
		return this.klantDAO.getAll();
	}
			
	public Klant del(long id) {
		Klant k = get(id);
		this.klantDAO.delete(id);
		return k;
	}

	public long add(Adres adres) {
		try {
			this.adresDAO.save(adres);
		} catch (MySQLIntegrityConstraintViolationException ex) {
			return -2;
		}
		return adres.getId();
	}
	
	public Adres getAdres(long adresId) {
		return this.adresDAO.get(adresId);
	}
	
	
	
	

	public static long getSerialversionuid() {return serialVersionUID;}



	
}
