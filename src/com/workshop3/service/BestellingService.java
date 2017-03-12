package com.workshop3.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import com.workshop3.dao.mysql.*;
import com.workshop3.model.*;

@SessionScoped
public class BestellingService extends DualEntityService<Bestelling, Artikel> {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private BestellingDAO bestelDAO;
	
	private KlantService klantService;

	private static final int maxNumOfStati = 7;

	public BestellingService() { super(new BestellingDAO(), new ArtikelDAO()); }
	
	
	public KlantService getKlantService() {return this.klantService;}
	
	@Inject
	public void setKlantService(KlantService service) {this.klantService = service;}
	

	public long process(Bestelling bestelling) {
		if (bestelling.getKlant() != null && bestelling.getKlant().getId() > 0) { // Klant is set
			return add(bestelling);
		} 
		if (getKlantService().klant() != null && getKlantService().klant().getId() > 0) { // Saved Klant in KlantView
			bestelling.setKlant(getKlantService().klant());
			return add(bestelling);
		}
		long id = add(bestelling);
		getKlantService().addBestellingToNewKlant(id);
		return id;		
	}
	
	public void statusUpdate(long id, int status) {
		this.bestelDAO.statusUpdate(id, status);
	}
	
	public int getStatus(long id) {
		Date currentDate = new Date(0);
		int status = 0;
		for (Map.Entry<Integer, Date> entry : get(id).getStati().entrySet()) {
			if (currentDate.compareTo(entry.getValue()) < 0) { // Find latest
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
		return new HashSet<Bestelling>(this.bestelDAO.getEm().createNativeQuery(
				"select * from Bestelling where klantId = " + klantID, Bestelling.class)
				.getResultList());
	}
	
	public Set<Bestelling> getBestellingList() {
		return new HashSet<Bestelling>(fetch());
	}
	
	public long getKlantByBestelling(long id) {
		return get(id).getKlant().getId();
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
	
	public static BigDecimal salesWorth(Set<Bestelling> bestellingen) {
		BigDecimal turnover = new BigDecimal(0.0);
		for (Bestelling b : bestellingen) {
			for (Map.Entry<Artikel, Integer> entry : b.getArtikelen().entrySet()) {
				BigDecimal artikelTimesAantal = entry.getKey().getPrijs()
						.multiply(new BigDecimal(entry.getValue()));
				turnover = turnover.add(artikelTimesAantal);
			}
			bestellingen.remove(b);
		}
		return turnover;
	}
	
	public String statusProgress(Period p, int begin, int change) {
		Set<Bestelling> bestellingen = bestellingPerStatus(p, begin);
		int som = 0;
		double totaalVerandering = 0.0; 
		int progress = 0;
		for (Bestelling b : bestellingen) {
			for (int i = begin; i < maxNumOfStati; i++) {
				if (b.getStati().containsKey(i + change)) {
					progress = i + change;
				}
			}
			som++; totaalVerandering += progress; progress = 0;
		}
		double avgProg = totaalVerandering / som;
		return "Aantal bestellingen met statusverandering = " + som + " waarde van de statusveranderingen = " + 
				NumberFormat.getIntegerInstance().format(totaalVerandering) +	" gemiddelde progressie = " + 
				avgProg;
	}
	
	public Set<Bestelling> bestellingPerStatus(Period p, int minStatus) {
		return new HashSet<Bestelling>(this.bestelDAO.getEm().createNativeQuery(
				"select * from Bestelling inner join bestellingHasStatus on bestellingHasStatus.datum > '" + 
				LocalDate.now().minus(p).format(DateTimeFormatter.ISO_DATE)	+ 
				"' and bestellingHasStatus.status > " + minStatus + 
				" and Bestelling.id = bestellingHasStatus.bestellingId", Bestelling.class)
				.getResultList());
	}
	
	public Set<Bestelling> bestellingPerPeriod(Period period) {
		Set<Bestelling> bestellingen = new HashSet<Bestelling>();
		bestellingen.addAll(this.bestelDAO.getEm().createNativeQuery(
				"select * from Bestelling where datum > '" + LocalDate.now().minus(period)
				.format(DateTimeFormatter.ISO_DATE) + "'", Bestelling.class)//ofPattern("yyyy-MM-dd HH:mm:ss")
				.getResultList());
		
		return  bestellingen;
	}

	
	
	

	public static long getSerialversionuid() {return serialVersionUID;}

	
	
	
}
