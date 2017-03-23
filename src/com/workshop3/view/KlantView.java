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

	public static String result = "InitialValue";
	
	public KlantView() {}
	
	
	public Klant getKlant() {return this.klant;}
	
	public void setKlant(Klant k) {this.klant = k;}
	
	public Adres getAdres() {return this.adres;}
	
	public void setAdres(Adres a) {this.adres = a;}

	public Account getAccount() {return this.account;}

	public void setAccount(Account act) {this.account = act;}

	public static String getResult() {return result;}
	
	
	private static void setResult(long outcome) {
		if (outcome <= 0) {
			switch ((int) outcome) {
				case -3: result = "InvalidEmail"; break;
				case -2: result = "TransactionalException"; break;
				case -1: result = "LoginFailed"; break;
				case  0: result = "SaveFailed"; break;
				default: result = "UnknownError"; break;
			}
		} else { result = "SUCCESS"; }
	}
	
	
	public void addAdresToKlant() {
		this.klant.setAdres(this.service.getAdres(this.service.add(this.adres)));
		setResult(addOrUpdate());
	}
		
	public void addAccountToKlant() {
		this.klant.getAccounts().add(this.service.getSimple(this.service.addSimple(this.account)));
		setResult(addOrUpdate());
	}
	
	public void addBezorgAdres() {
		this.klant.getBezorgAdressen().add(this.service.getAdres(this.service.add(this.adres)));
		setResult(addOrUpdate());
	}

	public long addOrUpdate() {
		String mail = this.klant.getEmail().toLowerCase(); 
		if (KlantService.isValidEmail(mail)) {
			this.klant.setEmail(mail);
			long id = this.klant.getId();
			if (id > 0) {
				return this.service.update(this.klant);
			}
			return this.service.add(this.klant);
		}
		return -3;
	}
	
	public void updateAccount() {
		setResult(this.service.updateSimple(this.account));
	}
	
	public long login() {
		Account acct = this.service.getUniqueSimple(new String[] {this.account.getLogin()});
		if (acct.getPass().equals(this.account.getPass())) {
			setAccount(acct);
			setKlant(acct.getKlant());
			setAdres(acct.getKlant().getAdres());
			return acct.getId();
		}
		return -1;
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
