package com.workshop3.view;

import java.util.*;

import javax.faces.bean.ManagedBean;
import javax.enterprise.context.*;
import javax.inject.*;

import com.workshop3.manager.KlantManager;
import com.workshop3.model.*;
import com.workshop3.model.Bestelling.BestellingStatus;
import com.workshop3.service.BestellingService;

@Named
@SessionScoped
public class VerkoopView implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	@Inject
	private Bestelling bestelling;
	
	private BestellingService bestelService;

	public VerkoopView() {}
	
	
	public BestellingService getBestelService() {return this.bestelService;}
	
	@Inject
	public void setBestelService(BestellingService bestelService) {this.bestelService = bestelService;}
	
	public Bestelling getBestelling() {return this.bestelling;}

	public void setBestelling(Bestelling bestelling) {this.bestelling = bestelling;}
	
	
	public List<Artikel> getArtikelAanbod() {
		return getBestelService().getArtikelList();
	}

	
	public String checkOut() {
		
		//Testfase

		long bestelID = getBestelService().process(this.bestelling);
		if (bestelID < 1) {
			if (bestelID == 0) {
				return "meer artikelen nodig!?! Weer naar VerkoopView";
			} else if (bestelID == -1) {
				return "bestelling heeft nog geen klant! door naar KlantView";
			}
		}
		
		getBestelService().statusUpdate(14, BestellingStatus.Betaald);
		
		return " - - -- -  {{{" + getBestelService().getKlantByBestelling(70);
	}
	
	
	

	
	public static long getSerialversionuid() {return serialVersionUID;}
	
	
	
	
	
	
}
