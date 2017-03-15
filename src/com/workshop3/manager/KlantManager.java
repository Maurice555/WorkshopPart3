package com.workshop3.manager;

import java.time.Period;
import java.util.Set;

import javax.enterprise.context.*;
import javax.inject.*;

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

	
	public String delivery() {
		
		getBestelService().statusUpdate(188, 3);
		getBestelService().statusUpdate(165, 4);
		
		Account acct = getKlantService().getUniqueSimple(new String[] {"Abstract"});
		
		getKlantView().setAccount(acct);
		
		getKlantService().addSimple(acct);
		
		getKlantView().setKlant(getKlantService().get("ver@ander.ing"));
		getKlantView().setAdres(new Adres("Oosterstraat", 33, "9712 Pp", "Groningen"));
		
		Period p = Period.ofDays(4);
		
		
		Set<Bestelling> onbetaalde = getBestelService().findByLatestPeriod(p);
		onbetaalde.removeAll(getBestelService().findBestellingPerStatus(p, 1));
		
		
		return "Nice to do business with you! @ Now pay! - - - " +
				onbetaalde +
				" - - - - for someone like " + 
				klant() + 
				" - - With no Order -- - - " +
				getBestelService().readStatus(208);
	}
	
	
	public String newKlant() {
		
		getKlantView().addAdresToKlant();
		
		//getKlantView().addAccountToKlant();
		
		//getKlantService().updateSimple(getKlantView().getAccount(), 5);
		
		getBestelService().statusUpdate(201, 4);
		
		
		

		
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
