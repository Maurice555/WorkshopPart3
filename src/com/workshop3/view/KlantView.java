package com.workshop3.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.enterprise.context.*;
import javax.faces.bean.ManagedBean;
import javax.inject.*;

import com.workshop3.exc.NoKnownAddressException;
import com.workshop3.model.*;
import com.workshop3.model.Adres.AdresType;
import com.workshop3.service.KlantService;

@Named
@SessionScoped
public class KlantView implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Klant klant;
	
	@Inject
	private Adres adres;
	
	@Inject
	private KlantService service;
	
	public KlantView() {}
	
	
	public Klant getKlant() {return this.klant;}
	
	public void setKlant(Klant klant) {this.klant = klant;}
	
	public Adres getAdres() {return this.adres;}
	
	public void setAdres(Adres adres) {this.adres = adres;}


	public void addAdresToKlant() {
		addAdresToKlant(AdresType.AllesInEen);
	}
	
	public void addAdresToKlant(AdresType aType) {
		this.klant.getAdressen().put(aType, this.adres);
		this.service.add(this.klant);
	}
	
	public String getKlantString() {
		if (!KlantService.isValidEmail(this.klant.getEmail())) {
			return "";
		}
		if (this.klant.getAchternaam() == null || this.klant.getAchternaam().equals("")) {
			getNameFromEmail();
		}
		return getName() + "\n" + this.klant.getEmail();
	}
	
	public Adres printAdres(int type) {
		switch (type) {
			case 0: 
				return this.klant.getAdressen()
						.get(AdresType.AllesInEen);
			case 1:
				return this.klant.getAdressen()
						.get(AdresType.Bezorg);
			case 2:
				return this.klant.getAdressen()
						.get(AdresType.Post);
			default:
				return null;
		}
	}
	
	public String getName(){
		return this.klant.getVoornaam() + " " + this.klant.getTussenvoegsel() + this.klant.getAchternaam(); 
	}
	
	public String getGreeting(){
		if (this.klant.getVoornaam() != null) return this.klant.getVoornaam();
		if (this.klant.getAchternaam() == null) return "Meneer/Mevrouw";
		return "Meneer/Mevrouw " + this.klant.getAchternaam();
	}
	
	public void getNameFromEmail() {
		String[] name = this.klant.getEmail().substring(0, this.klant.getEmail().indexOf("@"))
				.split("[\\.\\d-_]");
		
		if (name.length > 2) this.klant.setTussenvoegsel((name[1]));
		if (name.length > 1) this.klant.setVoornaam(name[0]);
		this.klant.setAchternaam(name[name.length - 1]);
	}
	

	public static long getSerialversionuid() {return serialVersionUID;}

	
}
