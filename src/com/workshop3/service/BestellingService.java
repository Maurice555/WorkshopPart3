package com.workshop3.service;

import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.enterprise.context.SessionScoped;
import javax.inject.*;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.workshop3.dao.mysql.*;
import com.workshop3.model.*;
import com.workshop3.view.KlantView;

@Named
@SessionScoped
public class BestellingService implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	@Inject
	private BestellingDAO bestelDAO;
	
	@Inject
	private ArtikelDAO artikelDAO;
	
	private KlantService service;

	public BestellingService() {}
	
	
	public BestellingDAO getBestelDAO() {return this.bestelDAO;}

	public void setBestelDAO(BestellingDAO bestelDAO) {this.bestelDAO = bestelDAO;}

	public ArtikelDAO getArtikelDAO() {return this.artikelDAO;}

	public void setArtikelDAO(ArtikelDAO artikelDAO) {this.artikelDAO = artikelDAO;}
	
	public KlantService getKlantService() {return this.service;}
	
	@Inject
	public void setKlantService(KlantService service) {this.service = service;}
	

	public long process(Bestelling bestelling) {
		if (bestelling.getKlant() != null && bestelling.getKlant().getId() != 0) { // Klant is set
			return add(bestelling);
		} 
		if (getKlantService().klant().getId() != 0) { // Saved Klant in KlantView
			bestelling.setKlant(getKlantService().klant());
			return add(bestelling);
		}
		long id = add(bestelling);
		getKlantService().addBestellingToNewKlant(id);
		return id;		
	}
	
	public List<Artikel> getArtikelList() {
		return this.artikelDAO.getAll();
	}	
	
	public Bestelling get(long id) {
		return this.bestelDAO.get(id);
	}
	
	public Artikel getArtikel(long id) {
		return this.artikelDAO.get(id);
	}

	public void statusUpdate(long id, int status) {
		this.bestelDAO.statusUpdate(id, status);
	}
	
	public int getStatus(long id) {
		Date currentDate = new Date(0);
		int status = 0;
		for (Map.Entry<Integer, Date> entry : get(id).getStati().entrySet()) {
			if (currentDate.compareTo(entry.getValue()) < 0) {
				currentDate = entry.getValue();
				status = entry.getKey();
			}
		}
		return status;
	}
	
	public String readStatus(long id) {
		int status = getStatus(id);
		
		switch (status) {
			case 0:
				return "Onbetaald";
			case 1:
				return "Betaald";
			case 2:
				return "InBehandeling";
			case 3:
				return "Verzonden";
			case 4:
				return "Afgeleverd";
			default:
				return "OnduidelijkeStatus";
		}
	}
	
	public Set<Bestelling> getBestelListByKlant(long klantID) {
		Set<Bestelling> bestellijst = new HashSet<Bestelling>();
		bestellijst.addAll(this.bestelDAO.getEm().createNativeQuery(
				"select * from Bestelling where klantId = " + klantID, Bestelling.class)
				.getResultList());
				
		return bestellijst;
	}
	
	public Set<Bestelling> getBestellingList() {
		Set<Bestelling> bestellingen = new HashSet<Bestelling>();
		bestellingen.addAll(this.bestelDAO.getAll());
		return bestellingen;
	}
	
	public Klant getKlantByBestelling(long id) {
		return this.bestelDAO.get(id).getKlant();
	}

	public long add(Bestelling bestelling) {
		try {
			this.bestelDAO.save(bestelling);
			return bestelling.getId();
		} catch (SQLIntegrityConstraintViolationException e) {
			return -2;
		}
		
	}

	public long add(Artikel artikel) {
		try {
			this.artikelDAO.save(artikel);
			return artikel.getId();
		} catch (SQLIntegrityConstraintViolationException ex) {
			return -2;
		}
		
	}
	
	public int artikelCount(Artikel artikel, Set<Bestelling> bestellingen) {
		int amountSold = 0;
		for (Bestelling b : bestellingen) {
			for(Map.Entry<Artikel, Integer> entry : b.getArtikelen().entrySet()) {
				if (artikel.equals(entry.getKey())) {
					amountSold += entry.getValue();
				}
			}
		}
		
		return amountSold;
	}
	
	public BigDecimal turnover(Period p) {
		BigDecimal turnover = new BigDecimal(0.0);
		for (Bestelling b : bestellingPerPeriod(p)) {
			if (getStatus(b.getId()) > 0) {
				
				for (Map.Entry<Artikel, Integer> entry : b.getArtikelen().entrySet()) {
					BigDecimal artikelTimesAantal = entry.getKey().getPrijs()
							.multiply(new BigDecimal(entry.getValue()));
					turnover = turnover.add(artikelTimesAantal);
				}
			}
		}
		return turnover;
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

