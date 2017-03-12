package com.workshop3.manager;

import java.text.NumberFormat;
import java.time.Period;
import java.util.*;

import javax.enterprise.context.*;
import javax.inject.*;
import javax.faces.bean.ManagedBean;

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
//		acct.setLogin("Abstract");
//		acct.setPass("Dually");
//		//acct.setKlant(getKlantService().get(""));
		
		//getKlantView().setAccount(acct);
		
		getKlantService().login("Abstract", "XXXXX");
		
		getKlantView().setAdres(getKlantService().getAdres("9955PP", 2, "b"));
		
		
		Bestelling bestelling = new Bestelling();
		bestelling.addArtikel(getBestelService().getSimple(4), 7);
		bestelling.addArtikel(getBestelService().getSimple(6), 5);
		
		bestelling.removeArtikel(getBestelService().getSimple(5), 1);
		
		getVerkoopView().setBestelling(bestelling);
		
		
		
		return "Nice to do business with you! @ " + 
				getKlantView().getAdres() + " " +
				" - - - - for someone like " + 
				getKlantView().getAccount() + 
				" - - With Order -- - - " +   
				getVerkoopView().getBestelling();
	}
	
	
	public String newKlant() {
		
		getKlantView().addAdresToKlant();
		
		//getKlantView().addAccountToKlant();
		
		//getKlantService().updateSimple(getKlantView().getAccount(), 5);
		
		getBestelService().statusUpdate(150, 4);
		
		Period p = Period.ofDays(4);
		
		
		
		return getKlantService().getKlantByAdres(getKlantView().getAdres())+ "- - - - - Amount Earned = " + 
				getBestelService().statusProgress(p, 0, 1) + 
				"-- -- - Account - -- -" + getKlantView().getAccount();
	}
	
	
	private Klant klant() {
		return getKlantView().getKlant();
	}

	
	public static long getSerialversionuid() {return serialVersionUID;}



	

	
}
