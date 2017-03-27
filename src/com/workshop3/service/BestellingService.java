package com.workshop3.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.*;
import java.util.*;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import static com.workshop3.service.AbstractEntityService.RestUtil.*;

import com.workshop3.dao.mysql.*;
import com.workshop3.model.*;

@Path("service/bestelling")
@SessionScoped
public class BestellingService extends DualEntityService<Bestelling, Artikel> {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private BestellingDAO bestelDAO;
	
	public BestellingService() { super(new BestellingDAO(), new ArtikelDAO()); }
	
	
	@GET @Path("zoek/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Bestelling> findByDateAndPeriod(@PathParam("query") String query) {
		if (query.matches("all.*")) { return fetch(); }
		Map<String, Object> quantities = getQuantities(getParamValuePairs(query));
		return findByDateAndPeriod(getBeginDate(quantities), getPeriod(quantities));		
	}
	
	public Set<Bestelling> findByDateAndPeriod(LocalDate beginDatum, Period p) {
		return new HashSet<Bestelling>(this.bestelDAO.findByDateAndPeriod(beginDatum, p));
	}
	
	@GET @Path("/zoek/klant" + ID)
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Bestelling> findByKlant(@PathParam("id") long klantID) {
		return new HashSet<Bestelling>(this.bestelDAO.getByKlant(klantID));
	}
	
	@GET @Path("zoek/klant" + ID + "&{temporalType}")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Bestelling> findByKlantAndDate(
			@PathParam("id") long klantID, 
			@PathParam("temporalType") String temporalType) {
		Set<Bestelling> bestellingen = findByDateAndPeriod(temporalType);
		bestellingen.retainAll(findByKlant(klantID));
		return bestellingen;
	}
	
	@GET @Path("zoek/klant/bestelling" + ID)
	@Produces(MediaType.TEXT_PLAIN)
	public long getKlantByBestelling(@PathParam("id") long id) {
		return get(id).getKlant().getId();
	}
	
	@PUT @Path("update/" + ID + "&status={status : [\\d]{1}") // 405 Method Not Allowed
	public void statusUpdate(@PathParam("id") long id, @PathParam("status") int status) {
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
	
	@GET @Path("status/" + ID)
	@Produces(MediaType.TEXT_PLAIN)
	public String readStatus(@PathParam("id") long id) {
		switch (getStatus(id)) {
			case 0:	return "Onbetaald";
			case 1:	return "Betaald";
			case 2:	return "InBehandeling";
			case 3:	return "Verzonden";
			case 4:	return "Afgeleverd";
			default:return "OnduidelijkeStatus";
		}
	}
	
	@GET @Path("zoek/metstatus/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Bestelling> findFromStatusTable(@PathParam("query") String query) {
		Map<String, Object> quantities = getQuantities(getParamValuePairs(query));
		return findFromStatusTable(getBeginDate(quantities), getPeriod(quantities));
	}
	
	public Set<Bestelling> findFromStatusTable(LocalDate d, Period p) {
		return new HashSet<Bestelling>(this.bestelDAO.getEm().createNativeQuery(
				"select * from Bestelling inner join bestellingHasStatus " + 
				"on bestellingHasStatus.datum > '" + d +
				"' and bestellingHasStatus.datum < '" + d.plus(p.plusDays(1)) +
				"' and Bestelling.id = bestellingHasStatus.bestellingId", Bestelling.class)
				.getResultList());
	}
	
	@GET @Path("verkoopcijfers/{query}")
	@Produces(MediaType.TEXT_PLAIN)
	public String salesProgress(@PathParam("query") String temporalQuery) {
		return Count.salesProgress(findByDateAndPeriod(temporalQuery));
	}	
	
	@GET @Path("verkoopcijfers/metstatus/{query}")
	@Produces(MediaType.TEXT_PLAIN)
	public String income(@PathParam("query") String query) {
		if (query.matches("all.*")) {
			return Count.salesProgress(findFromStatusTable(
					LocalDate.ofEpochDay(0), LocalDate.ofEpochDay(0).until(LocalDate.now())));
		}
		return Count.salesProgress(findFromStatusTable(query));
	}
	
	@GET @Path("verkoopcijfers/artikel" + ID + "&{temporalType}")
	@Produces(MediaType.TEXT_PLAIN)
	public int artikelCount(@PathParam("id") long artikelID, @PathParam("temporalType") String temporal) {
		return Count.artikelAmountSold(getSimple(artikelID), findByDateAndPeriod(temporal));
	}
	
	
	public static final class Count { // static counters need Set<Bestelling>
	
		public static final NumberFormat euroFormat() {
			return NumberFormat.getCurrencyInstance(Locale.GERMAN);
		}
		
		private static final String AmountCounted = "Aantal getelde bestellingen";

		public static String salesProgress(Set<Bestelling> bestellingen) {
			Map<String, Object> salesValues = new LinkedHashMap<String, Object>();
			BigDecimal waarde = salesWorth(bestellingen);
			salesValues.put("Waarde van de getelde bestellingen", euroFormat().format(waarde));
			Map<String, Object> stats = statusProgress(bestellingen);
			salesValues.putAll(stats);
			salesValues.put("Gemiddelde totaalprijs van de bestellingen", 
					euroFormat().format(waarde.doubleValue() / (int) stats.get(AmountCounted)));
			return salesValues.toString();
		}
		
		public static BigDecimal price(Bestelling b) {
			BigDecimal price = new BigDecimal(0.0);
			for (Map.Entry<Artikel, Integer> entry : b.getArtikelen().entrySet()) {
				BigDecimal artikelTimesAantal = entry.getKey().getPrijs()
						.multiply(new BigDecimal(entry.getValue()));
				price = price.add(artikelTimesAantal);
			}
			return price;
		}
		
		public static BigDecimal salesWorth(Set<Bestelling> bestellingen) {
			BigDecimal turnover = new BigDecimal(0.0);
			for (Bestelling b : bestellingen) {
				turnover = turnover.add(price(b));
			}
			return turnover;
		}
		
		private static final int MaxNumOfStati = 7;
	
		public static Map<String, Object> statusProgress(Set<Bestelling> bestellingen) {
			int totaalAantal, totaalVerandering, updates, changed, progress, prev;
			totaalAantal = totaalVerandering = updates = changed = progress = prev = 0;
			Date from, until; until = new Date(0); from = new Date();
			for (Bestelling b : bestellingen) {
				if (b.getDatum().compareTo(from) < 0) { from = b.getDatum(); }
				if (b.getDatum().compareTo(until) > 0) { until = b.getDatum(); }
				for (int i = 1; i < MaxNumOfStati; i++) {
					if (b.getStati().containsKey(i)) {
						progress = i;
						updates++;
					}
				}
				if (updates > prev) {prev = updates; changed++;}
				totaalAantal++;
				totaalVerandering += progress;	progress = 0;
			}
			double percentageChanged = changed * 100.0 / totaalAantal;
			double avgProg = totaalVerandering * 1.0 / changed;
			Map<String, Object> progression = new LinkedHashMap<String, Object>();
			progression.put("Bestellingen geteld vanaf", from);
			progression.put("Bestellingen geteld tot", until);
			progression.put(AmountCounted, totaalAantal);
			progression.put("Percentage met status groter dan 0", percentageChanged);
			progression.put("Aantal bestellingen met status groter dan 0", changed);
			progression.put("Aantal statusveranderingen", updates);
			progression.put("Totale waarde van de statusveranderingen", totaalVerandering);
			progression.put("Gemiddelde progressie", avgProg);
			return  progression;
		}
		
		public static int artikelAmountSold(Artikel artikel, Set<Bestelling> bestellingen) {
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
		
		
	}
	
	public static long getSerialversionuid() {return serialVersionUID;}

		
	
}

