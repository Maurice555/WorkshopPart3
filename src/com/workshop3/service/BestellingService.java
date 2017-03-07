package com.workshop3.service;

import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.enterprise.context.SessionScoped;
import javax.inject.*;

import com.workshop3.dao.mysql.*;
import com.workshop3.model.*;

@Named
@SessionScoped
public class BestellingService extends AbstractEntityService<Bestelling> { // implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	@Inject
	private BestellingDAO bestelDAO;
	
	@Inject
	private ArtikelDAO artikelDAO;
	
	private KlantService service;

	public BestellingService() { super(new BestellingDAO()); }
	
	
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
		bestellingen.addAll(fetch());
		return bestellingen;
	}
	
	public Klant getKlantByBestelling(long id) {
		return this.bestelDAO.get(id).getKlant();
	}

	public long add(Artikel artikel) {
		this.artikelDAO.save(artikel);
		return artikel.getId();
	}
	
	public static int artikelCount(Artikel artikel, Set<Bestelling> bestellingen) {
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
/*
	Artikel arti = new Artikel("Warm Broodje", "met vlees en gesmolten kaas", new BigDecimal(3.95));
	Artikel arti2 = new Artikel("Cappucino", "Met melkschuim en cacao", new BigDecimal(1.95));
	Artikel arti3 = new Artikel("Thee", "Alle soorten", new BigDecimal(1.20));
	
	
	this.artikelDAO.save(arti);
	this.artikelDAO.save(arti2);
	this.artikelDAO.save(arti3);
*/		


