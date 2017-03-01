package com.workshop3.view;

import java.util.*;

import javax.faces.bean.ManagedBean;
import javax.enterprise.context.*;
import javax.inject.*;

import com.workshop3.manager.KlantManager;
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

	public VerkoopView() {}
	
	
	public BestellingService getBestelService() {return this.bestelService;}
	
	public void setBestelService(BestellingService bestelService) {this.bestelService = bestelService;}
	
	public Bestelling getBestelling() {return this.bestelling;}

	public void setBestelling(Bestelling bestelling) {this.bestelling = bestelling;}
	
	
	public List<Artikel> getArtikelAanbod() {
		return getBestelService().getArtikelList();
	}

	
	public String checkOut() {	
		//Testfase

		this.bestelService.process(this.bestelling);
			
		//this.bestelling = new Bestelling();
		
		this.bestelService.statusUpdate(2, 2);
		//getBestelService().statusUpdate(8, 1);
		
		return "Tried to save: " + getBestelling() + " - -checkOut -- - " + 
				this.bestelService.getBestelListByKlant(3) + " - - - - ";
	
	}
	
	
	

	
	public static long getSerialversionuid() {return serialVersionUID;}
	
	
	
	
	
	
}