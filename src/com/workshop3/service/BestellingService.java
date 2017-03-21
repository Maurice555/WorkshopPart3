package com.workshop3.service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.workshop3.dao.mysql.*;
import com.workshop3.model.*;

@Path("service/bestelling")
@SessionScoped
public class BestellingService extends DualEntityService<Bestelling, Artikel> {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private BestellingDAO bestelDAO;
	
	private static final int maxNumOfStati = 7;

	public BestellingService() { super(new BestellingDAO(), new ArtikelDAO()); }
	
	
	public void statusUpdate(long id, int status) {
		Bestelling b = get(id);
		b.updateStatus(status);
		update(b);
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
	
	@GET
	@Path("status/" + ID)
	@Produces(MediaType.APPLICATION_JSON)
	public String readStatus(@PathParam("id") long id) {
		switch (getStatus(id)) {
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
	
	public Set<Bestelling> findByDate(LocalDate datum) {
		return new HashSet<Bestelling>(this.bestelDAO.findByDateAndPeriod(datum, Period.ofDays(1)));
	}
	
	public Set<Bestelling> findByDateAndPeriod(LocalDate beginDatum, Period p) {
		return new HashSet<Bestelling>(this.bestelDAO.findByDateAndPeriod(beginDatum, p));
	}
	
	public Set<Bestelling> findByDate(LocalDate beginDatum, LocalDate eindDatum) {
		return new HashSet<Bestelling>(this.bestelDAO.findByDateAndPeriod(beginDatum, beginDatum.until(eindDatum)));
	}
		
	public Set<Bestelling> findByLatestPeriod(Period period) {
		return new HashSet<Bestelling>(this.bestelDAO.findByDateAndPeriod(LocalDate.now().minus(period), period));
	}
	
	@GET
	@Path("/zoek/klant" + ID)
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Bestelling> findByKlant(@PathParam("id") long klantID) {
		return new HashSet<Bestelling>(this.bestelDAO.getEm().createNativeQuery(
				"select * from Bestelling where klantId = " + klantID, Bestelling.class)
				.getResultList());
	}
	
	public Set<Bestelling> findByKlantAndDate(long klantID, LocalDate datum) {
		Set<Bestelling> bestellingen = new HashSet<Bestelling>(findByDate(datum));
		bestellingen.retainAll(findByKlant(klantID));
		return bestellingen;
	}
	
	@GET @Path("zoek/klant/bestelling" + ID)
	@Produces(MediaType.APPLICATION_JSON)
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
		}
		return turnover;
	}
	
	public static Map<String, Number> statusProgress(Set<Bestelling> bestellingen, int minChange) {
		int totaalAantal, totaalVerandering, updates, changed, progress, prev;
		totaalAantal = totaalVerandering = updates = changed = progress = prev = 0;
		for (Bestelling b : bestellingen) {
			for (int i = 0; i < maxNumOfStati; i++) {
				if (b.getStati().containsKey(i + minChange)) {
					progress = i + minChange;
					updates++;
				}
			}
			if (updates > prev) {prev = updates; changed++;}
			totaalAantal++;
			totaalVerandering += progress;
			progress = 0;
		}
		double percentageChanged = changed * 100.0 / totaalAantal;
		double avgProg = totaalVerandering * 1.0 / changed;
		Map<String, Number> progression = new LinkedHashMap<String, Number>();
		progression.put("Aantal getelde bestellingen", totaalAantal);
		progression.put("Percentage met minimale (" + minChange + ") progressie", percentageChanged);
		progression.put("Aantal bestellingen met minimale progressie", changed);
		progression.put("Aantal voldoende statusveranderingen", updates);
		progression.put("Totale waarde van de voldoende statusveranderingen", totaalVerandering);
		progression.put("Gemiddelde progressie bij voldoende verandering", avgProg);
		return  progression;
	}
	
	public Set<Bestelling> findBestellingPerStatus(Period p, int minStatus) {
		return new HashSet<Bestelling>(this.bestelDAO.getEm().createNativeQuery(
				"select * from Bestelling inner join bestellingHasStatus on bestellingHasStatus.datum > '" + 
				LocalDate.now().minus(p).format(DateTimeFormatter.ISO_DATE)	+ 
				"' and bestellingHasStatus.status > " + minStatus + 
				" and Bestelling.id = bestellingHasStatus.bestellingId", Bestelling.class)
				.getResultList());
	}
	
	
	

	public static long getSerialversionuid() {return serialVersionUID;}

	
	
	
}
