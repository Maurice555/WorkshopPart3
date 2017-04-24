package com.workshop3.view;

import javax.enterprise.context.*;
import javax.inject.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.workshop3.model.*;
import com.workshop3.service.KlantService;

@Path("klant")
@SessionScoped
public class KlantView implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Klant klant;
	
	private Account account; // = new Account();
	
	@Inject
	private KlantService service;

	public KlantView() { this.klant = new Klant(); }
	
	@GET @Path("get")
	@Produces(MediaType.APPLICATION_JSON)
	public Klant getKlant() {return this.klant;}
	
	@POST @Path("set")
	@Consumes(MediaType.APPLICATION_JSON)
	public void setKlant(Klant k) {
		if (k.getId() > 0) { this.klant = k; } 
		else { this.klant.setKlant(k); }
	}
	
	@GET @Path("account/get") // testfase
	@Produces(MediaType.APPLICATION_JSON)
	public Account getAccount() {return this.account;}
	
	@POST @Path("account/set")
	@Consumes(MediaType.APPLICATION_JSON)
	public void setAccount(Account act) {this.account = act;}
	
	@POST @Path("adres")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public long addAdresToKlant(Adres a) {
		this.klant.setAdres(this.service.getAdres(this.service.add(new Adres(a))));
		return addOrUpdate(); //setResult();
	}
	
	@POST @Path("account")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public long addAccountToKlant(Account act) {
		Account newAct = new Account(this.klant);
		newAct.setLogin(act.getLogin());
		newAct.setPass(act.getPass());
		// this.klant.getAccounts().add(this.service.getSimple());
		this.service.addSimple(newAct);
		setAccount(newAct);
		return addOrUpdate();
	}
	
	@POST @Path("adres/bezorg")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public long addBezorgAdres(Adres a) {
		this.klant.getBezorgAdressen().add(this.service.getAdres(this.service.add(new Adres(a))));
		return addOrUpdate();
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
	
	public long updateAccount() {
		return this.service.updateSimple(this.account);
	}
	
	@GET @Path("login")
	@Produces(MediaType.TEXT_PLAIN)
	public long login() {
		Account acct = this.service.getUniqueSimple(this.account.identifyingProps());
		if (acct.getPass().equals(this.account.getPass())) {
			setAccount(acct);
			setKlant(acct.getKlant());
			return acct.getKlant().getId();
		}
		return -1;
	}
	
	@GET @Path("logout")
	@Produces(MediaType.TEXT_PLAIN)
	public String logout(){
		this.klant = new Klant();
		this.account = null;
		return "Logged Out";
	}
	

	
	public static long getSerialversionuid() {return serialVersionUID;}

	
}
