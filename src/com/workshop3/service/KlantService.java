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

@Path("service/klant")
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
		return mail.matches("([(\\w){2,}\\.-]+)[(\\w){2,}-]@[\\w-\\.]+[\\w^_]+(\\.([\\w^0-9_]){2,4}){1,2}");
	}
	
	public static boolean isValidPostcode(String postcode) {
		return trimUpCase(postcode).matches("[\\d]{4}[\\w]{2}");
	}
	
	public static String trimUpCase(String postcode) {
		return postcode.trim().replace(" ", "").toUpperCase();
	}
	
	public boolean isKnownEmail(String mail) {
		Klant k = get(mail.toLowerCase());
		if (k != null && k.getId() > 0) { return true; } 
		return false;
	}
	
// Custom Klant-zoekmethoden
	@GET @Path("main/email={mail}")
	@Produces(MediaType.APPLICATION_JSON)
	public Klant get(@PathParam("mail") String mail) {
		if (isValidEmail(mail)) { return this.klantDAO.get(mail.toLowerCase());	}
		return get(-3);
	}
	
	@GET @Path("/zoekopnaam/{voor}&{achter}")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Klant> findByVoorEnAchternaam(
			@PathParam("voor") String voor, 
			@PathParam("achter") String achter) {
		return new HashSet<Klant>(this.klantDAO.findByVoorEnAchternaam(voor, achter));
	}
	
	@GET @Path("zoekopnaam/{achter}")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Klant> findByAchternaam(@PathParam("achter") String achter) {
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
	
	@GET @Path("adres/" + ID)
	@Produces(MediaType.APPLICATION_JSON)
	public Adres getAdres(@PathParam("id") long adresID) {
		return this.adresDAO.get(adresID);
	}
	
	public Adres getUniqueAdres(String[] uniqueValues) {
		return findByPostcodeAndHuisnummer(uniqueValues[0], Integer.parseInt(uniqueValues[1]), uniqueValues[2]);
	}
	
	private static final String MetPostcode = "postcode={postcode}";
	private static final String MetHuisnummer = "huisnummer={huisnummer}";
	private static final String MetToevoeging = "toevoeging={toevoeging}";
	private static final String MetStraat = "straat={straat}";
	private static final String MetPlaats = "plaats={plaats}";
	private static final String ZoekAdres = "zoek/adres/";
	private static final String AND = "&";
	
	@GET @Path(ZoekAdres + MetPostcode)
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Adres> findByPostcode(
			@PathParam("postcode") String postcode) {
		return new HashSet<Adres>(this.adresDAO.findByPostcode(postcode));
	}
	
	@GET @Path(ZoekAdres + MetPostcode + AND + MetHuisnummer)
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Adres> findByPostcodeAndHuisnummer(
			@PathParam("postcode") String postcode,
			@PathParam("huisnummer") int huisnummer) {
		return new HashSet<Adres>(this.adresDAO.findByPostcodeAndHuisnummer(postcode, huisnummer));
	}
	
	@GET @Path(ZoekAdres + MetPostcode + AND + MetHuisnummer + AND + MetToevoeging)
	@Produces(MediaType.APPLICATION_JSON)
	public Adres findByPostcodeAndHuisnummer(
			@PathParam("postcode") String postcode, 
			@PathParam("huisnummer") int huisnummer, 
			@PathParam("toevoeging") String toevoeging) {
		for (Adres a : findByPostcodeAndHuisnummer(postcode, huisnummer)) {
			if (hasEqualToevoeging(a, toevoeging)) { return a; }
		}
		return null;
	}

	@GET @Path(ZoekAdres + MetStraat + AND + MetPlaats)
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Adres> findByStraatAndPlaats(
			@PathParam("straat") String straat, 
			@PathParam("plaats") String plaats) {
		return new HashSet<Adres>(this.adresDAO.findByStraat(straat, plaats));
	}
	
	@GET @Path(ZoekAdres + MetStraat + AND + MetHuisnummer + AND + MetPlaats)
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Adres> findByStraatAndHuisnummer(
			@PathParam("straat") String straat, 
			@PathParam("huisnummer") int huisnummer, 
			@PathParam("plaats") String plaats) {
		Set<Adres> adresSet = new HashSet<Adres>();
		for (Adres a : findByStraatAndPlaats(straat, plaats)) {
			if (huisnummer == 0 || a.getHuisnummer() == huisnummer) {
				adresSet.add(a);
			}
		}
		return adresSet;
	}
	
	@GET @Path(ZoekAdres + MetStraat + AND + MetHuisnummer + AND + MetToevoeging + AND + MetPlaats)
	@Produces(MediaType.APPLICATION_JSON)
	public Adres findByStraatAndHuisnummer(
			@PathParam("straat") String straat, 
			@PathParam("huisnummer") int huisnummer, 
			@PathParam("toevoeging") String toevoeging, 
			@PathParam("plaats") String plaats) {
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
