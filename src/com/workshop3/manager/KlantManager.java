package com.workshop3.manager;

import java.text.NumberFormat;
import java.time.Period;
import java.util.*;

import javax.enterprise.context.*;
import javax.inject.*;
import javax.faces.bean.ManagedBean;

import com.workshop3.dao.mysql.ArtikelDAO;
import com.workshop3.model.*;
import com.workshop3.view.KlantView;
import com.workshop3.view.VerkoopView;
import com.workshop3.service.BestellingService;
import com.workshop3.service.KlantService;

@Named
@SessionScoped
public class KlantManager implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private KlantView klantView;
	
	private VerkoopView verkoopView;

	private KlantService klantService;
	
	private BestellingService bestelService;

	@Inject
	private ArtikelDAO artiDAO;
	
	public KlantManager() {}
	
	
	
	public KlantView getKlantView() {return this.klantView;}

	@Inject
	public void setKlantView(KlantView view) {this.klantView = view;}
	
	public KlantService getKlantService() {return this.klantService;}

	@Inject
	public void setKlantService(KlantService service) {this.klantService = service;}
		
	public BestellingService getBestelService() {return this.bestelService;}
	
	@Inject
	public void setBestelService(BestellingService bestelService) {this.bestelService = bestelService;}

	@Inject
	public void setVerkoopView(VerkoopView verkoopView) {this.verkoopView = verkoopView;}

	public VerkoopView getVerkoopView() {return this.verkoopView;}

	
	public String delivery(){
		
		//Testfase

		getKlantView().setKlant(new Klant());
		
		
//		Account acct = new Account(klant());
//		acct.setLogin("Bugs");
//		acct.setPass("bunny.com");
//		
//		getKlantView().setAccount(acct);
		
		getKlantService().login("Maurice555", "bugs101");
		
		getKlantView().setAdres(new Adres("Akerkhof", 22, "b", "9701PX", "Groningen"));
		
		
		Bestelling bestelling = new Bestelling();
		bestelling.addArtikel(this.artiDAO.get(4), 7);
		bestelling.addArtikel(this.artiDAO.get(6), 5);
		
		bestelling.removeArtikel(this.artiDAO.get(5), 1);
		
		getVerkoopView().setBestelling(bestelling);
		
		
		
		return "Nice to do business with you! @ " + 
				getKlantView().getAdres() + " " + // adresIDs.length +
				" - - - - for someone like " + 
				getKlantView().getAccount() + 
				" - - With Order -- - - " +   
				getVerkoopView().getBestelling();
	}
	
	
	public String newKlant() {
		
		getKlantView().addBezorgAdres();
		
		getKlantView().addAccountToKlant();
		
		//getKlantService().update(klant(), 21);
		
		//getBestelService().statusUpdate(45, );
		
		Period p = Period.ofDays(4);
		
		
		
		return getVerkoopView().getBestelling() + "- - - - - Amount Earned = " + 
				NumberFormat.getCurrencyInstance().format(getBestelService().turnover(p));
	}
	
	
	private Klant klant() {
		return getKlantView().getKlant();
	}

	
	public static long getSerialversionuid() {return serialVersionUID;}



	

	
}
