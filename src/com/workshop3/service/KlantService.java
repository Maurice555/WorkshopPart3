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
	
	public KlantService() { super(new KlantDAO(), new AccountDAO()); }

	
	public static String firstCapital(String str){
		return str.trim().replace(str.substring(0, 1), str.substring(0, 1).toUpperCase());
	}
	
	public static String trimUpCase(String str) {
		return str.trim().replace(" ", "").toUpperCase();
	}
	
	public static boolean isValidEmail(String mail) {
		return mail.matches("[\\w]{3,}([\\w\\.-](\\w){3,})*@[\\w]{3,}([\\w\\.-][\\w^_]+)*(\\.([\\w^0-9_]){2,4}){1,2}");
	}
	
	public static boolean isValidPostcode(String postcode) {
		return trimUpCase(postcode).matches("[\\d]{4}[A-Z]{2}");
	}
	
	public boolean isKnownEmail(String mail) {
		Klant k = get(mail.toLowerCase());
		if (k != null && k.getId() > 0) { return true; } 
		return false;
	}
	
// Custom Klant-zoekmethoden
	@GET @Path("email={mail}")
	@Produces(MediaType.APPLICATION_JSON)
	public Klant get(@PathParam("mail") String mail) {
		if (isValidEmail(mail)) { return this.klantDAO.get(mail.toLowerCase());	}
		return get(-3);
	}
	
	@GET @Path("/zoek/{voor}&{achter}")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Klant> findByVoorEnAchternaam(
			@PathParam("voor") String voor, 
			@PathParam("achter") String achter) {
		return new HashSet<Klant>(this.klantDAO.findByVoorEnAchternaam(voor, achter));
	}
	
	@GET @Path("zoek/{achter}")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Klant> findByAchternaam(@PathParam("achter") String achter) {
		return new HashSet<Klant>(this.klantDAO.findByAchternaam(achter));
	}
	
	public Set<Klant> findKlantByAdres(Adres a) {
		Set<Klant> klanten = new HashSet<Klant>(findByPostcodeAndHuisnummer(
				a.getPostcode(), a.getHuisnummer(), a.getToevoeging()).getBewoners()); 
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
				return findByPostcodeAndHuisnummer(
						adres.getPostcode(), adres.getHuisnummer(), adres.getToevoeging()).getId();
			}
		} catch (TransactionalException te) { // Is wat we catchen
			if (isDuplicateKeyError(isSQLCauseForRollback(te))) {
				return findByPostcodeAndHuisnummer(
						adres.getPostcode(), adres.getHuisnummer(), adres.getToevoeging()).getId();
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
/*******
 * path/straatnaam=str&huisnummer=int&toevoeging=str&postcode=str&woonplaats=str
 */	@GET @Path("zoek/adres/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Adres> findAdres(@PathParam("query") String query) {
		return new HashSet<Adres>(this.adresDAO.get(RestUtil.getParamValuePairs(query)));
	}
	
	public Adres findByPostcodeAndHuisnummer(String postcode, int huisnummer, String toevoeging) {
		for (Adres a : findByPostcodeAndHuisnummer(postcode, huisnummer)) {
			if (trimUpCase(a.getToevoeging()).equals(trimUpCase(toevoeging))) { return a; }
		}
		return null;
	}
	
// Accountbeheer <Simple>
	@GET @Path("simple/klant" + ID)
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Account> getAccounts(@PathParam("id") long id) {
		return get(id).getAccounts();
	}

// Extraas	
	private static final String ZoekAdres = "zoek/adres/";
	private static final String AND = "&";
	private static final String MetPostcode = "postcode={postcode}";
	private static final String MetHuisnummer = "huisnummer={huisnummer}";
	//private static final String MetToevoeging = "toevoeging={toevoeging}";
	private static final String MetStraat = "straat={straat}";
	private static final String MetPlaats = "plaats={plaats}";
	
	@GET @Path(ZoekAdres + MetStraat + AND + MetPlaats)
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Adres> findByStraatAndPlaats(@PathParam("straat") String straat,	@PathParam("plaats") String plaats) {
		return new HashSet<Adres>(this.adresDAO.findByStraat(straat, plaats));
	}
	
	@GET @Path(ZoekAdres + MetPostcode + AND + MetHuisnummer)
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Adres> findByPostcodeAndHuisnummer(@PathParam("postcode") String postcode, @PathParam("huisnummer") int huisnummer) {
		return new HashSet<Adres>(this.adresDAO.findByPostcodeAndHuisnummer(postcode, huisnummer));
	}


	public static long getSerialversionuid() {return serialVersionUID;}
	
	
}
	
/*
 * Overbodige methoden
	public Adres getUniqueAdres(String[] uniqueValues) {
		return findByPostcodeAndHuisnummer(uniqueValues[0], Integer.parseInt(uniqueValues[1]), uniqueValues[2]);
	}
	
	@GET @Path(ZoekAdres + MetPostcode)
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Adres> findByPostcode(
			@PathParam("postcode") String postcode) {
		return new HashSet<Adres>(this.adresDAO.findByPostcode(postcode));
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
*/
