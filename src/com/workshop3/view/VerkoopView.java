package com.workshop3.view;

import java.util.*;

import javax.enterprise.context.*;
import javax.faces.bean.ManagedBean;
import javax.inject.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.workshop3.model.*;
import com.workshop3.service.BestellingService;

@Path("bestelling")
@SessionScoped
public class VerkoopView implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Bestelling bestelling;
	
	@Inject
	private BestellingService bestelService;
	
//	private KlantView klantView;

	public VerkoopView() { this.bestelling = new Bestelling(); }
	
	@GET @Path("get")
	@Produces(MediaType.APPLICATION_JSON)
	public Bestelling getBestelling() {return this.bestelling;}

	public void setBestelling(Bestelling b) {this.bestelling = b;}
	
//	public KlantView getKlantView() {return this.klantView;}
//	
//	@Inject
//	public void setKlantView(KlantView view) {this.klantView = view;}
//

//	private Klant klant() {
//		return getKlantView().getKlant();
//	}
	
//	@POST @Path("set/artikelen/")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public void setArtikelen(@PathParam("artikelen") Artikel[] artikelen){
//		for (Artikel a : artikelen) {
//			this.bestelling.addArtikel(a, 1);
//		}
//	}
	
	@GET @Path("add/artikelId={id : [\\d]+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Bestelling addArtikel(@PathParam("id") long artikelID) {
		this.bestelling.addArtikel(this.bestelService.getSimple(artikelID), 1);
		return this.bestelling;
	}
	
	@GET @Path("remove/artikelId={id : [\\d]+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Bestelling removeArtikel(@PathParam("id") long artikelID) {
		this.bestelling.removeArtikel(this.bestelService.getSimple(artikelID), 1);
		return this.bestelling;
	}
	
	@POST @Path("save")
	@Produces(MediaType.TEXT_PLAIN)
	public long process() {
		if (this.bestelling.getId() > 0) { // Saved bestelling
			return this.bestelService.update(this.bestelling);
		}
		this.bestelling.setDatum(new Date());
		
		if (this.bestelling.getKlant() != null && this.bestelling.getKlant().getId() > 0) { // Klant is set
			return this.bestelService.add(this.bestelling);
		} 
//		if (klant() != null && klant().getId() > 0) { // Saved Klant in KlantView
//			this.bestelling.setKlant(klant());
//			return this.bestelService.add(this.bestelling);
//		}
		this.bestelling.setKlant(null);
		return this.bestelService.add(this.bestelling);
	}
	
	
	
	

	
	public static long getSerialversionuid() {return serialVersionUID;}
	
	
	
	
	
	
}
