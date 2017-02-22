package com.workshop3.manager;

import java.math.BigDecimal;
import java.time.Period;

import javax.enterprise.context.*;
import javax.inject.*;
import javax.faces.bean.ManagedBean;

import com.workshop3.dao.mysql.ArtikelDAO;
import com.workshop3.model.Adres;
import com.workshop3.model.Adres.AdresType;
import com.workshop3.model.Artikel;
import com.workshop3.model.Bestelling;
import com.workshop3.model.Klant;
import com.workshop3.view.KlantView;
import com.workshop3.service.BestellingService;
import com.workshop3.service.KlantService;

@Named
@SessionScoped
public class KlantManager implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private KlantView klantView;
	
	private KlantService klantService;
	
	@Inject
	private ArtikelDAO artiDAO;
	
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

	
	
	public String delivery(){
		
		//Testfase

		Klant k = new Klant("Jerry", "Seinfeld", "seinfeld@appartment.com");
		Adres b = new Adres("Herenweg", 555, "9713XX", "Groningen");
		
		
		
		//getKlantService().add(k);
		getKlantService().add(b);
		
		k.getAdressen().put(AdresType.Bezorg, b);
		
		getKlantService().add(k);
		Adres test = getKlantService().getAdres(1);
		
		getKlantView().setAdres(test);
		
		getKlantView().setKlant(getKlantService().get(21));
		
		
		return "Nice to do business with you! @ " + getKlantView().getAdres().toString() + 
				" for someone like " + getKlantView().getKlant().toString();// + " - - - " + createTable;
	
	}
	
	
	public String newKlant() {
		
		getKlantView().addAdresToKlant();
		
		
		getKlantService().add(getKlantView().getKlant());
/*
		Artikel arti = new Artikel("Warm Broodje", "met vlees en gesmolten kaas", new BigDecimal(3.95));
		Artikel arti2 = new Artikel("Cappucino", "Met melkschuim en cacao", new BigDecimal(1.95));
		Artikel arti3 = new Artikel("Thee", "Alle soorten", new BigDecimal(1.20));
		
		
		this.artiDAO.save(arti);
		this.artiDAO.save(arti2);
		this.artiDAO.save(arti3);
*/		
		
		
		Bestelling bestelling = new Bestelling();
		bestelling
				.getArtikelen()
				.put(this.artiDAO
				.get(5), 11);
		bestelling.getArtikelen().put(this.artiDAO.get(6), 5);
		
		getKlantView().getKlant().getBestellingen().add(bestelling);
		
		Period p = Period.ofDays(4);
		
		
		
		getBestelService().add(bestelling);
		
		return getKlantView().getKlantString() + 
				" Tried to save : " + 
				getKlantView().getKlant().getBestellingen().get(0) + 
				"- - - - - Amount Sold = " + 
				getBestelService().artikelCount(getBestelService().getArtikelDAO().get(5), 
						getBestelService().bestellingPerPeriod(p));

	}
	
	public String fetchAll() {
		return getKlantService().getTable();
	}
	
	
	public static long getSerialversionuid() {return serialVersionUID;}
	
	
}
