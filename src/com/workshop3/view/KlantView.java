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
import com.workshop3.service.KlantService;

@Named
@SessionScoped
public class KlantView implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Klant klant;
	
	@Inject
	private Adres adres;
	
	private Account account;
	
	@Inject
	private KlantService service;
	
	public KlantView() {}
	
	
	public Klant getKlant() {return this.klant;}
	
	public void setKlant(Klant klant) {this.klant = klant; this.adres = this.klant.getAdres();}
	
	public Adres getAdres() {return this.adres;}
	
	public void setAdres(Adres adres) {this.adres = adres;}

	public Account getAccount() {return this.account;}

	public void setAccount(Account account) {this.account = account; setKlant(this.account.getKlant());}

	public KlantService getService() {return this.service;}
	
	public void setService(KlantService service) {this.service = service;}


	public void addAdresToKlant() {
		this.service.add(this.adres);
		this.klant.setAdres(this.adres);
	
		this.service.addOrUpdate(this.klant);
	}
	
	public void addAccountToKlant() {
		this.service.add(this.account);
		this.klant.getAccounts().add(this.account);
		
		this.service.addOrUpdate(this.klant);
	}
	
	public void addBezorgAdres() {
		this.service.add(this.adres);
		this.klant.getBezorgAdressen().add(this.adres);
		
		this.service.addOrUpdate(this.klant);
	}
	
	public void updateAccount() {
		this.service.update(this.account, this.account.getId());
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
	
	public String getName() {
		return this.klant.getVoornaam() + " " + this.klant.getTussenvoegsel() + this.klant.getAchternaam(); 
	}
	
	public String printAdres(int row) {
		if (row == 0) {
			return this.klant.getAdres().toString();
		}
		return printBezorgAdres(row);			
	}
	
	public String printBezorgAdres(int row) {
		List<Adres> bezorgAdressen = new ArrayList<Adres>();
		bezorgAdressen.addAll(this.klant.getBezorgAdressen());
		try {
			return bezorgAdressen.get(row - 1).toString();
		} catch (IndexOutOfBoundsException e) {
			return "_----___-----___--";
		}
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
