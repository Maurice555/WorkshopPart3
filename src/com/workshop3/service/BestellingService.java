package com.workshop3.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

import javax.enterprise.context.SessionScoped;
import javax.inject.*;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.workshop3.dao.mysql.*;
import com.workshop3.manager.KlantManager;
import com.workshop3.model.*;
import com.workshop3.model.Bestelling.BestellingStatus;
import com.workshop3.view.KlantView;
import com.workshop3.view.VerkoopView;

@Named
@SessionScoped
public class BestellingService implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	@Inject
	private BestellingDAO bestelDAO;
	
	@Inject
	private ArtikelDAO artikelDAO;

	private KlantService klantService;
	
	public BestellingService() {}
	
	
	public BestellingDAO getBestelDAO() {return this.bestelDAO;}

	public void setBestelDAO(BestellingDAO bestelDAO) {this.bestelDAO = bestelDAO;}

	public ArtikelDAO getArtikelDAO() {return this.artikelDAO;}

	public void setArtikelDAO(ArtikelDAO artikelDAO) {this.artikelDAO = artikelDAO;}
	
	public KlantService getKlantService() {return this.klantService;}
	
	@Inject
	public void setKlantService(KlantService klantService) {this.klantService = klantService;}


	public List<Artikel> getArtikelList() {
		return this.artikelDAO.getAll();
	}	
	
	public Bestelling get(long id) {
		return this.bestelDAO.get(id);
	}
	
	public Artikel getArtikel(long id) {
		return this.artikelDAO.get(id);
	}

	public Map<Artikel, Integer> getArtikelListByBestelling(long id) {
		return get(id).getArtikelen();
	}
	
	public void statusUpdate(long id, BestellingStatus status) {
		this.bestelDAO.statusUpdate(id, status);
	}
	
	public List<Bestelling> getBestellingList() {
		return this.bestelDAO.getAll();
	}
	
	public Klant getKlantByBestelling(long id) {
		return this.bestelDAO.get(id).getKlant();
	}

	public long process(Bestelling bestelling) {
		if (bestelling.getKlant() == null || bestelling.getKlant().getId() == 0) {
			long id = add(bestelling);
			getKlantService().newBestellingUnknownKlant(id);
			return id;
		}
		return add(bestelling);
			
	}
	
	public long add(Bestelling bestelling) {
		try {
			this.bestelDAO.save(bestelling);
			return bestelling.getId();
		} catch (MySQLIntegrityConstraintViolationException e) {
			return -2;
		}
		
	}

	public long add(Artikel artikel) {
		try {
			this.artikelDAO.save(artikel);
		} catch (MySQLIntegrityConstraintViolationException ex) {
			ex.getMessage();
		}
		return artikel.getId();
	}
	
	public int artikelCount(Artikel artikel, Set<Bestelling> bestellingen) {
		int amountSold = 0;
		for (Bestelling b : bestellingen) {
			for(Map.Entry<Artikel, Integer> entry : getArtikelListByBestelling(b.getId()).entrySet()) {
				if (artikel.equals(entry.getKey())) {
					amountSold += entry.getValue();
				}
			}
		}
		
		return amountSold;
	}
	
	public Set<Bestelling> bestellingPerPeriod(Period period) {
		Set<Bestelling> bestellingen = new HashSet<Bestelling>();
		
		bestellingen.addAll(this.bestelDAO.getEm().createNativeQuery(
				"select id from Bestelling where datum > " + LocalDate.now().minus(period)
				.format(DateTimeFormatter.ISO_DATE), Bestelling.class)//ofPattern("yyyy-MM-dd HH:mm:ss.nnn")
				.getResultList());
		
		return  bestellingen;
	}


	
	

	public static long getSerialversionuid() {return serialVersionUID;}
	
	
	
	
}


