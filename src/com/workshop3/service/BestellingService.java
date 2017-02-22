package com.workshop3.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

import javax.enterprise.context.SessionScoped;
import javax.inject.*;

import com.workshop3.dao.mysql.*;
import com.workshop3.model.*;

@Named
@SessionScoped
public class BestellingService implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	@Inject
	private BestellingDAO bestelDAO;
	
	@Inject
	private ArtikelDAO artikelDAO;
	
	public BestellingService() {}
	
	
	public BestellingDAO getBestelDAO() {return this.bestelDAO;}

	public void setBestelDAO(BestellingDAO bestelDAO) {this.bestelDAO = bestelDAO;}

	public ArtikelDAO getArtikelDAO() {return this.artikelDAO;}

	public void setArtikelDAO(ArtikelDAO artikelDAO) {this.artikelDAO = artikelDAO;}



	public List<Artikel> getArtikelList() {
		return this.artikelDAO.getAll();
	}	
	
	public Map<Artikel, Integer> getArtikelListByBestelling(long id) {
		return this.bestelDAO.get(id).getArtikelen();
	}
	
	public List<Bestelling> getBestellingList() {
		return this.bestelDAO.getAll();
	}
	
	public Klant getKlantByBestelling(long id) {
		return this.bestelDAO.get(id).getKlant();
	}

	public long add(Bestelling bestelling) {
		this.bestelDAO.save(bestelling);
		return bestelling.getId();
	}
	
	public long add(Artikel artikel) {
		this.artikelDAO.save(artikel);
		return artikel.getId();
	}
	
	public int artikelCount(Artikel artikel, Set<Bestelling> bestellingen) {
		int amountSold = 0;
		for (Bestelling b : bestellingen) {
			for(Map.Entry<Artikel, Integer> entry : getArtikelListByBestelling(b.getId()).entrySet()) {
				if (artikel.getId() == entry.getKey().getId()) {
					amountSold += entry.getValue();
				}
			}
		}
		
		return amountSold;
	}
	
	public Set<Bestelling> bestellingPerPeriod(Period period) {
		Set<Bestelling> bestellingen = new HashSet();
		
		bestellingen.addAll(this.bestelDAO.getEm().createNativeQuery(
				"select id from Bestelling where datum > " + LocalDate.now().minus(period)
				.format(DateTimeFormatter.ISO_DATE), Bestelling.class)
				.getResultList());
		
		return  bestellingen;
	}
}

//ofPattern("yyyy-MM-dd HH:mm:ss.nnn")
