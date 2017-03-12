package com.workshop3.service;

import java.sql.SQLException;
import java.util.*;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.transaction.TransactionalException;

import com.workshop3.dao.mysql.*;
import com.workshop3.model.*;
import com.workshop3.view.KlantView;

@SessionScoped
public class KlantService extends DualEntityService<Klant, Account> {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private AdresDAO adresDAO;
	
	private BestellingService bestelService;
	
	private KlantView klantView;

	public KlantService() { super(new KlantDAO(), new AccountDAO()); }

	
	public BestellingService getBestelService() {return this.bestelService;}
	
	@Inject
	public void setBestelService(BestellingService bestelService) {this.bestelService = bestelService;}

	public KlantView getKlantView() {return this.klantView;}
	
	@Inject
	public void setKlantView(KlantView klantView) {this.klantView = klantView;}


	public static String firstCapital(String s){
		return s.replace(s.substring(0, 1), s.substring(0, 1).toUpperCase());
	}
	
	public static boolean isValidEmail(String mail) {
		return mail.matches("([(\\w)+\\.-]+)[\\w^_]+@[\\w-\\.]+[\\w^_]+(\\.([\\w^0-9_]){2,4}){1,2}");
	}

	public boolean isKnownEmail(String mail) {
		return get(mail.toLowerCase()).getId() > 0;
	}
	
	public Klant klant() {
		return getKlantView().getKlant();
	}
	
	public long addOrUpdate(Klant k) { // Misschien beter in de view dit..
		String mail = k.getEmail().toLowerCase(); 
		if (isValidEmail(mail)) {
			k.setEmail(mail);
			long id = k.getId();
			if (id > 0) {
				update(k, id);
				return id;
			}
			return add(k);
		} 
		return -1;
	}
	
	public void addBestellingToNewKlant(long bestelID) {
		getKlantView().setKlant(new Klant());
		klant().getBestellingen().add(getBestelService().get(bestelID));
	}
	
	public Klant get(String mail) {
		if (isValidEmail(mail)) {
			return get(new String[] {mail.toLowerCase()});
		}
		return null;
	}
	
	public Set<Klant> getKlantByAdres(Adres a) {
		Set<Klant> klanten = new HashSet<Klant>(getAdres(a.getPostcode(), a.getHuisnummer(), a.getToevoeging())
				.getBewoners());
		klanten.addAll(a.getBezorgers());
		return klanten;
	}
	
	public long add(Adres adres) {
		try {
			this.adresDAO.save(adres);
			return adres.getId();
		} catch (SQLException sqlexc) {
			return errorCodeCheck(sqlexc, adres);
		} catch (TransactionalException te) { // Is wat we catchen
			return rollbackCheck(te, adres);
		}
	}
	
	public Adres getAdres(long adresID) {
		return this.adresDAO.get(adresID);
	}
	
	public Adres getAdres(String postcode, int huisnummer, String toevoeging) {
		return this.adresDAO.findByPostcodeAndHuisnummer(postcode, huisnummer, toevoeging);
	}
	
	public Set<Adres> getAdres(String postcode, int huisnummer) {
		return new HashSet<Adres>(this.adresDAO.findByPostcodeAndHuisnummer(postcode, huisnummer));
	}
	
	public Set<Adres> getAdres(String straat, int huisnummer, String toevoeging, String plaats) {
		return new HashSet<Adres>(this.adresDAO.findByStraatAndHuisnummer(straat, huisnummer, plaats));
	}
	
	public Set<Adres> getAdres(String straat, String plaats) {
		return new HashSet<Adres>(this.adresDAO.findByStraat(straat, plaats));
	}
	
	public Set<Account> getAccounts(long id) {
		return get(id).getAccounts();
	}
	
	public void login(String login, String pass) { // Moet misschien ook naar View
		Account acct = getSimple(new String[] {login});
		if (acct.getPass().equals(pass)) {
			getKlantView().setAccount(acct);
		}
	}
	
	
	@Override
	protected long errorCodeCheck(SQLException sqlexc, EntityTemplate e) {
		if (sqlexc.getErrorCode() == duplicateKey) {
			return getAdres(((Adres)e).getPostcode(), ((Adres)e).getHuisnummer(), ((Adres)e).getToevoeging())
					.getId();
		}
		return 0;
	}

	
	
	
	public static long getSerialversionuid() {return serialVersionUID;}
	
	
	
}
