package com.workshop3.view;

import java.util.*;

import javax.enterprise.context.*;
import javax.faces.bean.ManagedBean;
import javax.inject.*;

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
	
	@Inject
	private Account account;
	
	@Inject
	private KlantService service;
	
	private static final String mailReedsInGebruik = "Dit e-mailadres is reeds in gebruik";

	public KlantView() {}
	
	
	public Klant getKlant() {return this.klant;}
	
	public void setKlant(Klant klant) {this.klant = klant; this.adres = this.klant.getAdres();}
	
	public Adres getAdres() {return this.adres;}
	
	public void setAdres(Adres adres) {this.adres = adres;}

	public Account getAccount() {return this.account;}

	public void setAccount(Account account) {this.account = account; setKlant(this.account.getKlant());}

	
	public void addAdresToKlant() {
		this.klant.setAdres(this.service.getAdres(this.service.add(this.adres)));
		updateKlant();
	}
	
	public void addAccountToKlant() {
		this.klant.getAccounts().add(this.service.getSimple(this.service.addSimple(this.account)));
		updateKlant();
	}
	
	public void addBezorgAdres() {
		this.klant.getBezorgAdressen().add(this.service.getAdres(this.service.add(this.adres)));
		updateKlant();
	}

	public long updateKlant() {
		return this.service.addOrUpdate(this.klant);
	}
	
	public void updateAccount() {
		this.service.updateSimple(this.account, this.account.getId());
	}
	
	public void login() {
		this.service.login(this.account.getLogin(), this.account.getPass());
	}
	
	public String getName() {
		return this.klant.getVoornaam() + " " + this.klant.getTussenvoegsel() + this.klant.getAchternaam(); 
	}
	
	public Adres printAdres(int row) {
		if (row == 0) {
			return this.klant.getAdres();
		}
		return printBezorgAdres(row);			
	}
	
	public Adres printBezorgAdres(int row) {
		List<Adres> bezorgAdressen = new ArrayList<Adres>(this.klant.getBezorgAdressen()); 
		try {
			return bezorgAdressen.get(row - 1);
		} catch (IndexOutOfBoundsException e) {
			return null;
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
