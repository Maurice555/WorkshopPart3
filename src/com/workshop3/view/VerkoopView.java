package com.workshop3.view;

import java.util.*;

import javax.enterprise.context.*;
import javax.inject.*;

import com.workshop3.model.*;
import com.workshop3.service.BestellingService;

@Named
@SessionScoped
public class VerkoopView implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Bestelling bestelling;
	
	@Inject
	private BestellingService bestelService;
	
	private KlantView klantView;

	public VerkoopView() {}
	
	
	public Bestelling getBestelling() {return this.bestelling;}

	public void setBestelling(Bestelling b) {this.bestelling = b;}
	
	public KlantView getKlantView() {return this.klantView;}
	
	@Inject
	public void setKlantView(KlantView view) {this.klantView = view;}


	public List<Artikel> getArtikelAanbod() {
		return this.bestelService.getSimpleDAO().getAll();
	}

	Klant klant() {
		return getKlantView().getKlant();
	}
	
	public String checkOut() {	
		
		//Testfase

		//process();
			
		this.bestelService.statusUpdate(169, 2);
		this.bestelService.statusUpdate(12, 3);
		
		return "Tried to save: " + this.bestelling + " - -checkOut -- - " + 
				this.bestelService.findByKlant(115) + " - - - - ";
	}
	
	public long process() {
		if (this.bestelling.getId() > 0) { // Saved bestelling
			this.bestelService.update(this.bestelling);
			return this.bestelling.getId();
		}
		this.bestelling.setDatum(new Date());
		if (this.bestelling.getKlant() != null && this.bestelling.getKlant().getId() > 0) { // Klant is set
			return this.bestelService.add(this.bestelling);
		} 
		if (klant() != null && klant().getId() > 0) { // Saved Klant in KlantView
			this.bestelling.setKlant(klant());
			return this.bestelService.add(this.bestelling);
		}
		long id = this.bestelService.add(this.bestelling);
		getKlantView().setKlant(new Klant());
		klant().getBestellingen().add(this.bestelling);
		return id;		
	}
	
	
	
	

	
	public static long getSerialversionuid() {return serialVersionUID;}
	
	
	
	
	
	
}
