package com.workshop3.manager;

import java.text.NumberFormat;
import java.time.Period;

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

		//Klant k = new Klant("Pieperdosie", "Villalobos", "pieppp@pikachu.nl");
		Adres b = new Adres("Schuitendiep", 101, "9712PS", "Groningen");
		
		
		
		//getKlantService().add(b);
		
		//getKlantService().add(k);
		//Adres a = getKlantService().getAdres(3);
		
		//Klant k = getKlantService().get("blipps@pikachu.nl");
		
		getKlantView().setKlant(new Klant());
		
		getKlantView().setAdres(b);
		
		Bestelling bestelling = new Bestelling();
		bestelling.addArtikel(this.artiDAO.get(4), 11);
		bestelling.addArtikel(this.artiDAO.get(6), 5);
		
		bestelling.removeArtikel(this.artiDAO.get(5), 1);
		
		getVerkoopView().setBestelling(bestelling);
		
		
		
		return "Nice to do business with you! @ " + 
				getKlantView().getAdres() + 
				" - - - - for someone like " + 
				klant() + 
				" - - With Order -- - - " +   
				getVerkoopView().getBestelling();
	}
	
	
	public String newKlant() {
		
		getKlantView().addAdresToKlant();
		
		//Call klantDAO.merge
		//getKlantService().update(klant(), 21);
		
		//getKlantView().addAdresToKlant();
		
		//getBestelService().add(getVerkoopView().getBestelling());
		
/*
		Artikel arti = new Artikel("Warm Broodje", "met vlees en gesmolten kaas", new BigDecimal(3.95));
		Artikel arti2 = new Artikel("Cappucino", "Met melkschuim en cacao", new BigDecimal(1.95));
		Artikel arti3 = new Artikel("Thee", "Alle soorten", new BigDecimal(1.20));
		
		
		this.artiDAO.save(arti);
		this.artiDAO.save(arti2);
		this.artiDAO.save(arti3);
*/		
		
		//getBestelService().statusUpdate(45, );
		
		Period p = Period.ofDays(4);
		
		
		//Bestelling.klant not null please
		
		
		return getVerkoopView().getBestelling() + "- - - - - Amount Earned = " + 
				NumberFormat.getCurrencyInstance().format(getBestelService().turnover(p));
	}
	
	public String fetchAll() {
		return "Nakkes";
	}


	private Klant klant() {
		return getKlantView().getKlant();
	}

	
	public static long getSerialversionuid() {return serialVersionUID;}



	

	
}
