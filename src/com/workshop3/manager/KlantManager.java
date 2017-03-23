package com.workshop3.manager;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import javax.enterprise.context.*;
import javax.faces.bean.ManagedBean;
import javax.inject.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.workshop3.RestResourceConfig;
import com.workshop3.model.*;
import com.workshop3.view.KlantView;
import com.workshop3.view.VerkoopView;
import com.workshop3.service.BestellingService;
import com.workshop3.service.KlantService;

@Path("test")
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
	
	
	@GET @Path("test123")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		return "KlantManager hier.. wat is er aan de hand?"; 
	}
	

	
	private void setView() {
		
		//getKlantView().setKlant(new Klant()); //getKlantService().get("ver@ander.ing"));
		
			
		Account acct = new Account();
		acct.setLogin("Restful");
		acct.setPass("SUCCE666SS");
		//acct.setKlant(klant());
		getKlantView().setAccount(acct);
	
		getKlantView().login();
	
//		getKlantView().setKlant(new Klant(""));
		
		getKlantView().setAdres(new Adres("Reststraat", 222, "a-aa", "3456 Pp", "Hellevoetsluis"));
		
		
	}

	private void setVerkoop() {
		
		Bestelling b = new Bestelling(klant());
		
		b.addArtikel(getBestelService().getSimple(4), 6);
		b.addArtikel(getBestelService().getSimple(5), 5);
		b.removeArtikel(getBestelService().getSimple(3), 2);
		//b.updateStatus(4);
		getVerkoopView().setBestelling(b);
		
		
	}
	
	@GET @Path("delivery")
	@Produces(MediaType.TEXT_PLAIN)
	public String delivery() {
		
		setView();
		
		setVerkoop();
				
		Period p = Period.ofDays(5);
		
		
		Set<Bestelling> onbetaalde = getBestelService().findByLatestPeriod(p);
		onbetaalde.removeAll(getBestelService().findByStatus(p, 1));
		
		
		return "Nice to do business with you! @ Now pay! - - - " +
				onbetaalde +
				" - - - - for someone like " + 
				klant().getAccounts() + 
				" - - With no Order -- - - " +
				getBestelService().readStatus(201);
	}
	
	
	@GET @Path("delivery/add")
	@Produces(MediaType.TEXT_PLAIN)
	public String newKlant() {
		
		getKlantView().addBezorgAdres();
		
		//getKlantView().addAccountToKlant();
		
		getVerkoopView().process();

		
		return getKlantView().getAccount() + 
				"- - - - - Amount Earned = " + 
				BestellingService.statusProgress(getBestelService().fetch(), 1) + 
				"-- -- - Result - -- -" + KlantView.getResult(); 
	}
	
	
	private Klant klant() {
		return getKlantView().getKlant();
	}

	
	
	
	
	public static long getSerialversionuid() {return serialVersionUID;}


	
}
