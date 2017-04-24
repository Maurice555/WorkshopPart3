package com.workshop3.view;

import java.util.Date;

import javax.enterprise.context.*;
import javax.inject.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.workshop3.model.*;
import com.workshop3.service.BestellingService;
import com.workshop3.service.KlantService;

@Path("bestelling")
@SessionScoped
public class VerkoopView implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Bestelling bestelling;
	
	@Inject
	private BestellingService bestelService;
	
	@Inject
	private KlantService klantService;
	
	public VerkoopView() { this.bestelling = new Bestelling(); }
	
	@GET @Path("get")
	@Produces(MediaType.APPLICATION_JSON)
	public Bestelling getBestelling() {return this.bestelling;}

	public void setBestelling(Bestelling b) {this.bestelling = b;}
	
	
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
	
	@GET @Path("logout")
	@Produces(MediaType.TEXT_PLAIN)
	public String setNewBestelling(){
		this.bestelling = new Bestelling();
		return this.bestelling.toString();
	}
	
	@GET @Path("klantId={id : [\\d]+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Bestelling setKlant(@PathParam("id") long klantID){
		this.bestelling.setKlant(this.klantService.get(klantID));
		return this.bestelling;
	}
	
	@POST @Path("save")
	@Produces(MediaType.TEXT_PLAIN)
	public long process() {
		if (this.bestelling.getId() > 0) { // Saved bestelling
			return this.bestelService.update(this.bestelling);
		}
		this.bestelling.setDatum(new Date());
//		if (this.bestelling.getKlant() != null && this.bestelling.getKlant().getId() > 0) { // Klant is set
//			return this.bestelService.add(this.bestelling);
//		} 
//		// this.bestelling.setKlant(null);
		return this.bestelService.add(this.bestelling);
	}
	
	
	
	

	
	public static long getSerialversionuid() {return serialVersionUID;}
	
	
	
	
	
	
}
